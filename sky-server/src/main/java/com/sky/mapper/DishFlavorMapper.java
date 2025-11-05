package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface DishFlavorMapper {
    /**
     * 添加风味
     */

    void insertFlavors(List<DishFlavor>  flavors);
}
