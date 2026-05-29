package xyz.nullicn.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;

public class PasswordUtil {

    // 可以根据服务器性能调整，建议 10~12
    private static final int WORKLOAD = 12;

    /**
     * 加密密码
     */
    public static String hashPassword(String plainPassword) {
        if (StrUtil.isBlank(plainPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * 校验密码
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (StrUtil.isBlank(plainPassword) || StrUtil.isBlank(hashedPassword)) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
