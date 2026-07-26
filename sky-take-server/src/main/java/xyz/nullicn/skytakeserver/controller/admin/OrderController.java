package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.dto.OrdersCancelDTO;
import xyz.nullicn.dto.OrdersConfirmDTO;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPageQueryDTO;
import xyz.nullicn.dto.OrdersRejectionDTO;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.OrderService;
import xyz.nullicn.vo.OrderStatisticsVO;
import xyz.nullicn.vo.OrderVO;

import java.util.List;

@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Tag(name = "管理端订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/conditionSearch")
    @Operation(summary = "搜索订单")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.pageSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "订单详情")
    public Result<OrdersDTO> detail(@PathVariable Long id) {
        log.info("订单详情 {}", id);
        OrdersDTO ordersDTO = orderService.detail(id);
        return Result.success(ordersDTO);
    }

    @GetMapping("/statistics")
    @Operation(summary = "订单状态统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("订单状态统计 管理端用户:{}", BaseContext.getCurrentId());
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @PutMapping("/cancel")
    @Operation(summary = "订单取消")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("管理端用户:{} 取消订单:{} 理由:{}", BaseContext.getCurrentId(), ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());
        orderService.adminCancel(ordersCancelDTO.getCancelReason(), ordersCancelDTO.getId());
        return Result.success();
    }

    @PutMapping("/rejection")
    @Operation(summary = "订单拒接")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("管理端用户:{} 拒接订单:{} 理由:{}", BaseContext.getCurrentId(), ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());
        orderService.adminRejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "订单完成")
    public Result<String> complete(@PathVariable Long id) {
        log.info("管理端用户:{} 订单完成 {}", BaseContext.getCurrentId(), id);
        orderService.adminComplete(id);
        return Result.success();
    }

    @PutMapping("/confirm")
    @Operation(summary = "订单接单")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("管理端用户:{} 订单接单 {}", BaseContext.getCurrentId(), ordersConfirmDTO.getId());
        orderService.adminConfirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @Operation(summary = "订单派送")
    public Result<String> delivery(@PathVariable Long id) {
        log.info("管理端用户:{} 订单派送 {}", BaseContext.getCurrentId(), id);
        orderService.adminDelivery(id);
        return Result.success();
    }
}
