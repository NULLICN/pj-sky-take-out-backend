package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.vo.SetmealVO;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Validated
@Tag(name = "套餐管理接口", description = "")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐: {}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id) {
        log.info("查询套餐id: {}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }
}
