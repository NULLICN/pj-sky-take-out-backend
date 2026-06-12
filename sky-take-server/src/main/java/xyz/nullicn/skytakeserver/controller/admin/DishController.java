package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.DishService;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Validated
@Tag(name = "菜品管理接口", description = "")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<String> addDish(@RequestBody @Valid DishDTO dish) {
        dishService.addWithFlavor(dish);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(@Valid DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页菜品查询: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam @Valid List<Long> ids) {
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping
    public Result<String> editDish(@RequestBody @Valid DishDTO dishDTO) {
        dishService.editDish(dishDTO);
        return Result.success();
    }
}
