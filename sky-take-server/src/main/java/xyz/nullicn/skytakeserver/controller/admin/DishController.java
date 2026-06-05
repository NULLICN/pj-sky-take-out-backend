package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.DishService;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Validated
@Tag(name = "菜品管理接口", description = "")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    Result<String> addDish(@RequestBody @Valid DishDTO dish) {
        dishService.addWithFlavor(dish);
        return Result.success();
    }
}
