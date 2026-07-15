package xyz.nullicn.skytakeserver.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.ShoppingCartService;

@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addToCart(shoppingCartDTO);
        return Result.success();
    }
}
