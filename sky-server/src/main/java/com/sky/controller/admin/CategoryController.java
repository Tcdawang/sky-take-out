package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/category")
@Api(tags = "分类管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult<Category>> queryPage(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("前端传来的数据为：{}", categoryPageQueryDTO);
        PageResult<Category> page = categoryService.queryPage(categoryPageQueryDTO);
        return Result.success(page);
    }

    @PostMapping
    @ApiOperation(value = "新增分类")
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.insertCategory(categoryDTO);
        return Result.success();
    }
}
