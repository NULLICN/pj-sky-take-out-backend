package xyz.nullicn.skytakeserver.service;


import xyz.nullicn.vo.BusinessDataVO;
import xyz.nullicn.vo.DishOverViewVO;
import xyz.nullicn.vo.OrderOverViewVO;
import xyz.nullicn.vo.SetmealOverViewVO;

public interface WorkspaceService {
    BusinessDataVO businessData();

    SetmealOverViewVO overviewSetmeals();

    DishOverViewVO overviewDishes();

    OrderOverViewVO overviewOrders();
}
