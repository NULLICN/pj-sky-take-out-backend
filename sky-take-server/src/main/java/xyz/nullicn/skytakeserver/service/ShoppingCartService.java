package xyz.nullicn.skytakeserver.service;

import org.springframework.stereotype.Service;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.OrderDetail;
import xyz.nullicn.entity.ShoppingCart;

import java.util.List;

@Service
public interface ShoppingCartService {
    void addToCart(ShoppingCartDTO goods);

    List<ShoppingCart> list();

    void clean();

    void sub(ShoppingCartDTO shoppingCartDTO);

    void addToCartBatch(List<OrderDetail> orderDetails);
}
