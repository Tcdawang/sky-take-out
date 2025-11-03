package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     */
    List<Category> queryPage(String name, Integer type);

    /**
     * 新增分类
     */

    void insertCategory(Category category);

    /**
     * 根据id查询
     */

    Category selectByName(String name);

    /**
     * 修改分类
     */

    void updateCategory(Category category);

    /**
     * 根据分类进行查询
     * @param type
     */
    List<Category> selectByType(Integer type);

    /**
     * 根据id进行删除
     */
    void delete(Long id);
}
