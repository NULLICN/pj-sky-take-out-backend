package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.*;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.vo.OrderPaymentVO;
import xyz.nullicn.vo.OrderStatisticsVO;
import xyz.nullicn.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrdersDTO getById(Long id);

    PageResult page(int page, int pageSize, int status);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    void cancel(Long id);

    void repetition(Long id);

    PageResult pageSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrdersDTO detail(Long id);

    OrderStatisticsVO statistics();

    void adminCancel(String cancelReason, Long id);

    void adminRejection(OrdersRejectionDTO ordersRejectionDTO);

    void adminComplete(Long id);

    void adminConfirm(OrdersConfirmDTO ordersConfirmDTO);

    void adminDelivery(Long id);
}
