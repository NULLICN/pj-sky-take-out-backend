package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @AutoFill(OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> list(Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clean(Long userId);

    void sub(ShoppingCart shoppingCart);

    ShoppingCart getByCondition(ShoppingCart shoppingCart);

    void deleteByCondition(ShoppingCart shoppingCart);
}
