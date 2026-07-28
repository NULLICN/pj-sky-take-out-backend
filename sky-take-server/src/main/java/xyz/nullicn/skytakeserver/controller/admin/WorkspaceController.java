package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.WorkspaceService;
import xyz.nullicn.vo.BusinessDataVO;
import xyz.nullicn.vo.DishOverViewVO;
import xyz.nullicn.vo.OrderOverViewVO;
import xyz.nullicn.vo.SetmealOverViewVO;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Tag(name = "工作台面板")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    @Operation(summary = "今日运营数据")
    public Result<BusinessDataVO> businessDataVOResult() {
        log.info("查询今日运行数据 用户号:{}", BaseContext.getCurrentId());
        BusinessDataVO vo = workspaceService.businessData();
        return Result.success(vo);
    }

    @GetMapping("/overviewSetmeals")
    @Operation(summary = "查询套餐总览")
    public Result<SetmealOverViewVO>  overviewSetmealsResult() {
        log.info("查询套餐总览");
        SetmealOverViewVO vo = workspaceService.overviewSetmeals();
        return Result.success(vo);
    }

    @GetMapping("/overviewDishes")
    @Operation(summary = "查询菜品总览")
    public Result<DishOverViewVO> overviewDishesResult() {
        log.info("查询菜品总览");
        DishOverViewVO vo = workspaceService.overviewDishes();
        return Result.success(vo);
    }

    @GetMapping("/overviewOrders")
    @Operation(summary = "查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrdersResult() {
        log.info("查询订单管理数据");
        OrderOverViewVO vo = workspaceService.overviewOrders();
        return Result.success(vo);
    }
}
