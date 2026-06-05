package xyz.nullicn.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜品口味
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long dishId;

    @NotBlank(message = "口味名称不能为空")
    @Size(max = 32, message = "口味名称最长32位")
    private String name;

    @NotBlank(message = "口味值不能为空")
    @Size(max = 255, message = "口味值最长255位")
    private String value;

}
