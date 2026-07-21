package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPaymentDTO;
import xyz.nullicn.dto.OrdersSubmitDTO;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.OrderService;
import xyz.nullicn.vo.OrderPaymentVO;
import xyz.nullicn.vo.OrderSubmitVO;
import xyz.nullicn.vo.OrderVO;

import java.util.List;

@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
@Tag(name = "订单接口")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("订单提交: {}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrdersDTO> getById(@PathVariable Long id) {
        log.info("查询订单: {}", id);
        OrdersDTO order = orderService.getById(id);
        return Result.success(order);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> page(@RequestParam int page, @RequestParam int pageSize, @RequestParam(required = false, defaultValue = "0") Integer status) {
        log.info("分页查询: 页码{} 大小{} 状态{}",  page, pageSize, status);
        PageResult pageResult = orderService.page(page, pageSize, status);
        return Result.success(pageResult);
    }

    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }
}
