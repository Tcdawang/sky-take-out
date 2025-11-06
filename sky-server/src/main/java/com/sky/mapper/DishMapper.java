package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    Integer selectByCategoryId(Long categoryId);

    List<DishVO> queryPage(String name, Integer categoryId, Integer status);

    void insertDish(Dish dish);

    Dish selectById(Long id);

    void update(Dish dish);
}
