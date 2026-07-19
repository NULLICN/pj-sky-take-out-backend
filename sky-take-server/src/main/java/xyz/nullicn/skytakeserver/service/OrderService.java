package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPaymentDTO;
import xyz.nullicn.dto.OrdersSubmitDTO;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.vo.OrderPaymentVO;
import xyz.nullicn.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    OrdersDTO getById(Long id);

    PageResult page(int page, int pageSize, int status);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);
}
