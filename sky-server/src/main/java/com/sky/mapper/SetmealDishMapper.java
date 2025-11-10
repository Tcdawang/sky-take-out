package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    /**
     * 批量操作绑定套餐的菜品
     */
    List<Long> selectByDishIds(List<Long> dishIds);

    /**
     * 添加菜品和套餐关系
     */

    void insertSetmealDish(List<SetmealDish> setmealDishes);

    List<SetmealDish> selectBySetmealId(Long setmealId);
}

