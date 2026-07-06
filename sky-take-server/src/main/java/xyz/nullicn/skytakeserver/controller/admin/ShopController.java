package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.ShopService;
import xyz.nullicn.skytakeserver.service.impl.ShopServiceImpl;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    ShopServiceImpl shopServiceImpl;

    @PutMapping("/{status}")
    @Operation(summary = "设置店铺经营状态", description = "1为营业 0为打样")
    public Result<String> setStatus(@PathVariable Integer status) {
        log.info("设定店铺经营状态: {}", status);
        shopServiceImpl.setStatus(status);
        return Result.success();
    }

    @GetMapping("/status")
    @Operation(summary = "获取店铺经营状态", description = "1为营业 0为打样")
    public Result<Integer> getStatus() {
        Integer status = shopServiceImpl.getStatus();
        log.info("获取店铺经营状态: {}", status);
        return Result.success(status);
    }
}
