package xyz.nullicn.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordEditDTO implements Serializable {

    private Long empId;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "密码需为6-20位字母与数字")
    private String oldPassword;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "密码需为6-20位字母与数字")
    private String newPassword;

}
