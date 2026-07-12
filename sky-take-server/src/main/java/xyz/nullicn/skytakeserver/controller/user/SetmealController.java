package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.vo.DishItemVO;
import xyz.nullicn.vo.SetmealVO;

import java.util.List;

@Slf4j
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Tag(name="根据套餐分类id查询套餐")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @GetMapping("/list")
    @Operation(summary = "根据套餐分类id查询所属套餐")
    public Result<List<Setmeal>> getSetmealByCategoryId(@Parameter Long categoryId) {
        log.info("套餐种类id: {}", categoryId);
        List<Setmeal> setmeals = setmealService.getSetmealsByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐id查询所属菜品")
    public Result<List<DishItemVO>>  getDishByCategoryId(@PathVariable Long id) {
        log.info("查询套餐id: {}的菜品", id);
        List<DishItemVO> dishes = setmealService.getDishesBySetmealId(id);
        return Result.success(dishes);
    }
}
