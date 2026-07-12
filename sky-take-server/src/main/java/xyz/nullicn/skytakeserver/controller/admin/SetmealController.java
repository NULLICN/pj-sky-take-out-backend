package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.dto.SetmealPageQueryDTO;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.vo.SetmealVO;

import java.util.List;

/**
 * 套餐管理
 */
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Validated
@Tag(name = "套餐管理接口", description = "")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @Operation(summary = "新增套餐", description = "新增一个套餐和其套餐菜品")
    public Result<String> addSetmeal(@RequestBody @Valid SetmealDTO setmealDTO) {
        log.info("新增套餐: {}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过套餐id查询套餐", description = "查询一个套餐和其套餐菜品")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id) {
        log.info("查询套餐id: {}", id);
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询套餐", description = "返回页大小的套餐数量")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "停售起售套餐", description = "操作id套餐装填，1为起售，0为停售")
    public Result<String> status(@PathVariable int status, @Parameter Long id) {
        setmealService.status(status, id);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "套餐修改", description = "更新套餐和套餐菜品")
    public Result<String> update(@RequestBody @Valid SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> deleteBatchById(@RequestParam @NotNull List<Long> ids) {
        setmealService.deleteBatchByIds(ids);
        return Result.success();
    }
}
