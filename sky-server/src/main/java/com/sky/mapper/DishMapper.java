package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    Integer selectByCategoryId(Long categoryId);

    List<DishVO> queryPage(String name, Integer categoryId, Integer status);
}
