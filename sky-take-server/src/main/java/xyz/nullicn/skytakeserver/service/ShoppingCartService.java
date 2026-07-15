package xyz.nullicn.skytakeserver.service;

import org.springframework.stereotype.Service;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.ShoppingCart;

@Service
public interface ShoppingCartService {
    void addToCart(ShoppingCartDTO goods);
}
