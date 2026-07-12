package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.vo.DishVO;

import java.util.List;

@Slf4j
@RestController("userDishController")
@RequestMapping("/user/dish")
@Tag(name="C端-菜品浏览接口")
public class DishController {

    @Autowired
    DishService dishService;

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询所属菜品")
    public Result<List<DishVO>> getDishList(@Parameter Long categoryId) {
        log.info("查询菜品种类id: {}", categoryId);
        List<DishVO> dishList = dishService.listWithFlavor(categoryId);
        return Result.success(dishList);
    }
}
