package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    public PageResult<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询参数:{}", setmealPageQueryDTO);
        //创建分页查询的结果对象
        PageResult<SetmealVO> pr = new PageResult<>();
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //调用mapper查询数据
        Page<SetmealVO> vos = (Page<SetmealVO>)setmealMapper.queryPage(setmealPageQueryDTO.getName(), setmealPageQueryDTO.getCategoryId(),
                setmealPageQueryDTO.getStatus());

        pr.setTotal(vos.getTotal());
        pr.setRecords(vos.getResult());
        return pr;
    }

    @Override
    @Transactional

    public void insertSetmeal(SetmealDTO setmealDTO) {
        //添加套餐数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        Long currentId = BaseContext.getCurrentId();
        setmeal.setCreateUser(currentId);
        setmeal.setUpdateUser(currentId);
        setmeal.setStatus(StatusConstant.DISABLE);//默认停售
        setmealMapper.insertSetmeal(setmeal);

        //回显套餐id
        Long id = setmeal.getId();
        //保存菜品和套餐的关系
        //获取添加的菜品信息集合
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        log.info("套餐中菜品信息:{}", setmealDishes);
        //由于此时还没有在setmealDish的表中添加数据因此我们是无法获取到套餐的id的我们可以遍历集合将回显回来的id赋值
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        //批量添加菜品和套餐关系
        setmealDishMapper.insertSetmealDish(setmealDishes);
    }

    @Override
    public SetmealVO selectById(Long id) {
        //先根据id查询出来套餐情况
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO sv = new SetmealVO();
        BeanUtils.copyProperties(setmeal, sv);
        //再根据setmealId查询出来套餐中包含的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        sv.setSetmealDishes(setmealDishes);
        return sv;
    }
}
