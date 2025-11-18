package com.sky.controller.user;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    @ApiOperation("条件查询分类")
    public Result<List<Category>> getCategoryType(Integer type){
        //log.info("分类值为:{}", type == 1 ? "菜品分类" : "套餐分类" );
        List<Category> categories = categoryService.selectByType(type);
        return Result.success(categories);
    }
}
