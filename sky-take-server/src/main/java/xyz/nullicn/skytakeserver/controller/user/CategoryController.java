package xyz.nullicn.skytakeserver.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.entity.Category;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.CategoryService;

import java.util.List;

@Slf4j
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name="C端-分类接口")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "获取种类列表", description = "得到所有启用的种类")
    public Result<List<Category>> getCategory(@Parameter @RequestParam(required = false) Integer type) {
        log.info("种类查询type: {}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
