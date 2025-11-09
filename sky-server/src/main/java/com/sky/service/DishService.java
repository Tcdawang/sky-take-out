package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    PageResult<DishVO> queryPage(DishPageQueryDTO dishPageQueryDTO);

    void insertDish(DishDTO dishDTO);

    void update(DishDTO dishDTO);

    DishVO selectById(Long id);

    void delete(List<Long> ids);

    void startAndStop(Integer status, Long id);

    List<Dish> list (Long categoryId, String name);

}
