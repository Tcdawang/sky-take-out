package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

public interface CategoryService {
    PageResult<Category> queryPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void insertCategory(CategoryDTO categoryDTO);

    Category selectByName(String name);

    void updateCategory(CategoryDTO categoryDTO);
}
