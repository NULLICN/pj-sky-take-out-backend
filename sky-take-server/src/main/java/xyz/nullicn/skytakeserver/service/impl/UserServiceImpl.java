package xyz.nullicn.skytakeserver.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.dto.UserLoginDTO;
import xyz.nullicn.entity.User;
import xyz.nullicn.exception.LoginFailedException;
import xyz.nullicn.properties.WeChatProperties;
import xyz.nullicn.skytakeserver.mapper.UserMapper;
import xyz.nullicn.skytakeserver.service.UserService;
import xyz.nullicn.utils.HttpClientUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    UserMapper userMapper;


    @Override
    public User wechatLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());


        // 是否获取到openid
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断是否为新用户
        User user = userMapper.getByOpenid(openid);

        // 为新用户完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            try {
                userMapper.insert(user);
            } catch (DuplicateKeyException e) {
                // 并发场景下另一线程已插入相同openid，回查即可
                user = userMapper.getByOpenid(openid);
            }
        }

        return user;
    }

    private String getOpenid(String code) {
        // 调用微信接口获取openid
        Map<String, String> parameters = new HashMap<>();
        parameters.put("appId", weChatProperties.getAppid());
        parameters.put("secret", weChatProperties.getSecret());
        parameters.put("js_code", code);
        parameters.put("grant_type", "authorization_code");

        String jsonResult;
        try {
            jsonResult = HttpClientUtil.doGet(WX_LOGIN, parameters);
        } catch (Exception e) {
            log.warn("调用微信jscode2session失败", e);
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonResult);

        // 微信接口错误响应包含 errcode，成功时无该字段或为 0
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode != 0) {
            log.warn("微信登录失败 errcode={}, errmsg={}", errcode, jsonObject.getString("errmsg"));
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        return jsonObject.getString("openid");
    }
}
