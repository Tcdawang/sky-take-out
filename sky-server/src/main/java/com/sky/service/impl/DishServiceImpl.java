package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult<DishVO> queryPage(DishPageQueryDTO dishPageQueryDTO) {
        //创建分页查询的对象
        PageResult<DishVO> pr = new PageResult<>();
        //开启分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //调用mapper
        Page<DishVO> dishes = (Page<DishVO>)dishMapper.queryPage(dishPageQueryDTO.getName(), dishPageQueryDTO.getCategoryId(),
                dishPageQueryDTO.getStatus());
        log.info("查询出来的结果:{}", dishes);
        pr.setTotal(dishes.getTotal());
        pr.setRecords(dishes.getResult());
        return pr;
    }
}
