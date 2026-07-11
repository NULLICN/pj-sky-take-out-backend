package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.UserLoginDTO;
import xyz.nullicn.entity.User;
import xyz.nullicn.vo.UserLoginVO;

public interface UserService {
    User wechatLogin(UserLoginDTO userLoginDTO);
}
