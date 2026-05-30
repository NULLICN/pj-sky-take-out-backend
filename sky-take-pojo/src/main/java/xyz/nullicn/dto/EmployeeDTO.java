package xyz.nullicn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "员工数据传输模型")
public class EmployeeDTO implements Serializable {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户名仅接受数字、英文")
    @Size(max = 20, message = "用户名最长20位")
    @Schema(description = "用户名，仅接受数字、英文", example = "admin001")
    private String username;

    @NotBlank(message = "姓名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "姓名仅接受数字、英文、中文")
    @Size(max = 20, message = "姓名最长20位")
    @Schema(description = "姓名，仅接受数字、英文、中文", example = "张三")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号，11位中国大陆手机号", example = "13800138000")
    private String phone;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^[01]$", message = "性别仅接受0或1")
    @Schema(description = "性别：0=女，1=男", example = "1", allowableValues = {"0", "1"})
    private String sex;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
             message = "身份证号格式不正确")
    @Schema(description = "身份证号，18位中国公民身份证号码", example = "110101199001011234")
    private String idNumber;

}
