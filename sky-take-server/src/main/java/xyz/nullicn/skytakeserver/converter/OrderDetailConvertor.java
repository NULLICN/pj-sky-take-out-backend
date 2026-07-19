package xyz.nullicn.skytakeserver.converter;

import org.mapstruct.Mapper;
import xyz.nullicn.entity.OrderDetail;
import xyz.nullicn.entity.ShoppingCart;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailConvertor {

    List<OrderDetail> toOrderDetails(List<ShoppingCart>  shoppingCarts);

}
