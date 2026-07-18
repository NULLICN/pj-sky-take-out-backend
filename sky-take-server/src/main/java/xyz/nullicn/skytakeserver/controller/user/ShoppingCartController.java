package xyz.nullicn.skytakeserver.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.ShoppingCartService;

import java.util.List;

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

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<String> clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
