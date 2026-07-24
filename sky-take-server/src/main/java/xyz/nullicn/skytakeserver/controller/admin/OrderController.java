package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPageQueryDTO;
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
}
