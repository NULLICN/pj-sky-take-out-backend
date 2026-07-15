package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

@Mapper
public interface ShoppingCartMapper {

    @AutoFill(OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);
}
