package xyz.nullicn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "员工分页查询模型")
public class EmployeePageQueryDTO implements Serializable {

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "姓名仅接受数字、英文、中文")
    @Size(max = 20, message = "姓名最长20位")
    @Schema(description = "姓名，模糊匹配", example = "张三")
    private String name;

    @Positive(message = "页码必须为正数")
    @Schema(description = "页码，从1开始", example = "1")
    private int page;

    @Min(value = 1, message = "每页记录数最小为1")
    @Max(value = 100, message = "每页记录数最大为100")
    @Schema(description = "每页显示记录数", example = "10")
    private int pageSize;

}
