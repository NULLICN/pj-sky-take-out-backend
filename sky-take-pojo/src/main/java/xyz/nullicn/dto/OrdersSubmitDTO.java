package xyz.nullicn.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {
    //地址簿id
    @NotNull(message = "地址簿id不能为空")
    private Long addressBookId;
    //付款方式
    @NotNull(message = "付款方式不能为空")
    private int payMethod;
    //备注
    @NotBlank(message = "备注不能为空")
    private String remark;
    //预计送达时间
    @NotNull(message = "预计送达时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;
    //配送状态  1立即送出  0选择具体时间
    @NotNull(message = "配送状态不能为空")
    private Integer deliveryStatus;
    //餐具数量
    @NotNull(message = "餐具数量不能为空")
    private Integer tablewareNumber;
    //餐具数量状态  1按餐量提供  0选择具体数量
    @NotNull(message = "餐具数量状态不能为空")
    private Integer tablewareStatus;
    //打包费
    @NotNull(message = "打包费不能为空")
    private Integer packAmount;
    //总金额
    @NotNull(message = "总金额不能为空")
    private BigDecimal amount;
}
