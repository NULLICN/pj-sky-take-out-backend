package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCart getByUserIdAndDishIdAndFlavor(ShoppingCart shoppingCart);

    void updateNumberById(ShoppingCart shoppingCart);

    @AutoFill(OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);
}
