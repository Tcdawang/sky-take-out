package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealMapper {

    Integer selectByCategoryId(Long categoryId);

    List<SetmealVO> queryPage(String name, Integer categoryId, Integer status);

    void insertSetmeal(Setmeal setmeal);

    Setmeal selectById(Long id);

    void update(Setmeal setmeal);

    List<Integer> selectStatusByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);

    List<Setmeal> getSetmealByCategoryId(Long categoryId);
}
