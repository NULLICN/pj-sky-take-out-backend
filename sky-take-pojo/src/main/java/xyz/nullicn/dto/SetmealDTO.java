package xyz.nullicn.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import xyz.nullicn.entity.SetmealDish;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDTO implements Serializable {

    @NotNull(message = "套餐ID不能为空")
    private Long id;

    //分类id
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    //套餐名称
    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 32, message = "套餐名称最长32位")
    private String name;

    //套餐价格
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //描述信息
    @Size(max = 255, message = "描述信息最长255位")
    private String description;

    //图片
    @NotBlank(message = "图片不能为空")
    private String image;

    //套餐菜品关系
    @Size(min = 1, message = "套餐至少包含一项菜品")
    private List<SetmealDish> setmealDishes = new ArrayList<>();

}
