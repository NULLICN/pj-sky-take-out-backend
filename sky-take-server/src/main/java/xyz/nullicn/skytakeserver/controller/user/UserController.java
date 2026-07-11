package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.constant.JwtClaimsConstant;
import xyz.nullicn.dto.UserLoginDTO;
import xyz.nullicn.entity.Category;
import xyz.nullicn.entity.User;
import xyz.nullicn.properties.JwtProperties;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.CategoryService;
import xyz.nullicn.skytakeserver.service.UserService;
import xyz.nullicn.skytakeserver.service.impl.UserServiceImpl;
import xyz.nullicn.utils.JwtUtil;
import xyz.nullicn.vo.UserLoginVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/user")
@Tag(name="用户端用户模块接口", description = "用户相关的操作接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "登录", description = "接收授权码调取微信api得到用户标识码")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        log.info("微信用户登录: {}", userLoginDTO.getCode());

        // 微信登录
        User user = userService.wechatLogin(userLoginDTO);

        // 生成jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
//                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }
}
