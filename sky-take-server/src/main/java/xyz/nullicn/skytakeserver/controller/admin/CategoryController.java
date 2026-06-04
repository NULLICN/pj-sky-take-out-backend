package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.dto.CategoryDTO;
import xyz.nullicn.dto.CategoryPageQueryDTO;
import xyz.nullicn.entity.Category;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.CategoryService;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Tag(name = "分类相关接口", description = "分类相关操作")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "新增分类", description = "添加一个新分类，状态默认为禁用")
    @PostMapping
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @Operation(summary = "分类分页查询", description = "根据传入页号与每页条数返回数据")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "删除分类", description = "根据分类ID删除分类，若分类关联了菜品或套餐则删除失败")
    @DeleteMapping
    public Result<String> deleteById(@RequestParam Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "修改分类", description = "修改分类信息")
    @PutMapping
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @Operation(summary = "启用禁用分类", description = "根据分类ID启用或禁用分类，status为0禁用，1启用")
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable("status") Integer status, @RequestParam Long id) {
        log.info("启用禁用分类：status={}, id={}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    @Operation(summary = "根据类型查询分类", description = "根据分类类型查询分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam Integer type) {
        log.info("根据类型查询分类：type={}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
