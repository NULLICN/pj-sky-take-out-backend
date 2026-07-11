package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.vo.DishVO;

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
    @Operation(summary = "新增菜品", description = "新增一个菜品和对应的口味组")
    public Result<String> addDish(@RequestBody @Valid DishDTO dish) {
        dishService.addWithFlavor(dish);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询菜品", description = "返回页大小的菜品数量")
    public Result<PageResult> page(@Valid DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页菜品查询: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "通过id删除菜品", description = "通过id删除菜品")
    public Result<String> deleteByIds(@RequestParam @Valid List<Long> ids) {
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新菜品", description = "根据id更新对应菜品")
    public Result<String> update(@RequestBody @Valid DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过id查询菜品", description = "根据传入id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        DishVO dishVO = dishService.getDByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @GetMapping("/list")
    @Operation(summary = "根据种类id查询菜品", description = "根据传入的菜品种类id查询所有对应菜品")
    public Result<List<Dish>> getDishList(@Parameter Long categoryId) {
        log.info("查询菜品种类id: {}", categoryId);
        List<Dish> dishList = dishService.getDishesByCategoryId(categoryId);
        return Result.success(dishList);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "停售起售菜品", description = "操作id菜品装填，1为起售，0为停售")
    public Result<String> status(@PathVariable int status, @Parameter Long id) {
        log.info("状态: {} id: {}", status, id);
        dishService.status(status, id);
        return Result.success();
    }
}
