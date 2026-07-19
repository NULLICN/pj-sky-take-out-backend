package xyz.nullicn.skytakeserver.converter;

import org.mapstruct.Mapper;
import xyz.nullicn.dto.OrdersSubmitDTO;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.vo.OrderSubmitVO;

@Mapper(componentModel = "spring")
public interface OrderConverter {

    Orders toOrders(OrdersSubmitDTO dto);

}
