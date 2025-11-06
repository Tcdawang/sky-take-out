package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    /**
     * 批量操作绑定套餐的菜品
     */
    List<Long> selectByDishIds(List<Long> dishIds);
}

