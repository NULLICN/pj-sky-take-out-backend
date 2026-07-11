package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.ShopService;
import xyz.nullicn.skytakeserver.service.impl.ShopServiceImpl;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    @GetMapping("/status")
    @Operation(summary = "获取店铺经营状态", description = "1为营业 0为打样")
    public Result<Integer> getStatus() {
        Integer status = shopService.getStatus();
        log.info("获取店铺经营状态: {}", status);
        return Result.success(status);
    }
}
