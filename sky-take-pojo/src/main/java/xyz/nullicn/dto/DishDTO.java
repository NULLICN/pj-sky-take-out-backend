package xyz.nullicn.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import xyz.nullicn.entity.DishFlavor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO implements Serializable {

    private Long id;

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 32, message = "菜品名称最长32位")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    private String image;

    @Size(max = 255, message = "描述信息最长255位")
    private String description;

    private Integer status;

    private List<DishFlavor> flavors = new ArrayList<>();

}
