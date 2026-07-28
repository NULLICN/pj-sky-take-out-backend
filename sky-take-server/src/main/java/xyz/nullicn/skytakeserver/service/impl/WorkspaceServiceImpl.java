package xyz.nullicn.skytakeserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.skytakeserver.mapper.WorkspaceMapper;
import xyz.nullicn.skytakeserver.service.WorkspaceService;
import xyz.nullicn.vo.BusinessDataVO;
import xyz.nullicn.vo.DishOverViewVO;
import xyz.nullicn.vo.OrderOverViewVO;
import xyz.nullicn.vo.SetmealOverViewVO;

import java.time.LocalDate;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Override
    public BusinessDataVO businessData() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Long employeeId = BaseContext.getCurrentId();

        Double turnover = workspaceMapper.getTodayTurnover(today, tomorrow, Orders.COMPLETED, employeeId);

        Integer validOrderCount = workspaceMapper.getTodayValidOrderCount(today, tomorrow, Orders.COMPLETED, employeeId);

        Integer totalOrderCount = workspaceMapper.getTodayTotalOrderCount(today, tomorrow, employeeId);

        double orderCompletionRate = totalOrderCount > 0 ? (double) validOrderCount / totalOrderCount : 0.0;

        double unitPrice = validOrderCount > 0 ? turnover / validOrderCount : 0.0;

        Integer newUsers = workspaceMapper.getTodayNewUsers(today, tomorrow, employeeId);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer sold = workspaceMapper.countSetmealByStatus(1);
        Integer discontinued = workspaceMapper.countSetmealByStatus(0);
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    @Override
    public DishOverViewVO overviewDishes() {
        Integer sold = workspaceMapper.countDishByStatus(1);
        Integer discontinued = workspaceMapper.countDishByStatus(0);
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    @Override
    public OrderOverViewVO overviewOrders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Long employeeId = BaseContext.getCurrentId();

        Integer waitingOrders = workspaceMapper.getTodayOrderCountByStatus(
                today, tomorrow, Orders.TO_BE_CONFIRMED, employeeId);
        Integer deliveredOrders = workspaceMapper.getTodayOrderCountByStatus(
                today, tomorrow, Orders.CONFIRMED, employeeId);
        Integer completedOrders = workspaceMapper.getTodayOrderCountByStatus(
                today, tomorrow, Orders.COMPLETED, employeeId);
        Integer cancelledOrders = workspaceMapper.getTodayOrderCountByStatus(
                today, tomorrow, Orders.CANCELLED, employeeId);
        Integer allOrders = workspaceMapper.getTodayTotalOrderCount(today, tomorrow, employeeId);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
