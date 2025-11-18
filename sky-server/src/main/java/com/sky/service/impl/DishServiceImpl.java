package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    /**
     * 该分页查询的写法有bug 即我们从第二页进行查询的时候 并此时在表单项加入筛选条件
     * 此时查询出来的页面没有值的 即使我们的第二页上有符合条件的值
     * 因为此时查询出来的数据会重新重第一页开始排序 一直往下排 而我们此时查询出来的结果是第二页的数据但是此时可能没有第二页所有会显示没有数据
     */

    //TODO 在分页查询之前添加条件判断如果此时查询没有带条件值 我们就正常进行分页查询 如果有就将页面重置为1
    public PageResult<DishVO> queryPage(DishPageQueryDTO dishPageQueryDTO) {
        //创建分页查询的对象
        PageResult<DishVO> pr = new PageResult<>();
        //开启分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //调用mapper
        Page<DishVO> dishes = (Page<DishVO>) dishMapper.queryPage(dishPageQueryDTO.getName(), dishPageQueryDTO.getCategoryId(),
                dishPageQueryDTO.getStatus());
        log.info("查询出来的结果:{}", dishes);
        pr.setTotal(dishes.getTotal());
        pr.setRecords(dishes.getResult());
        return pr;
    }

    /**
     * 对应添加菜品 在表单项中有口味的选择 因此这里会操作两种表 因此我们需要通过事务来保证数据的原子性
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void insertDish(DishDTO dishDTO) {
        //打日志
        log.info("新增员工信息:{}", dishDTO);

        //为口味表中插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //获取当前登录者的id
        Long currentId = BaseContext.getCurrentId();
        dish.setCreateUser(currentId);
        dish.setUpdateUser(currentId);
        //插入前主键
        log.info("插入前主键:{}", dish.getId());
        dishMapper.insertDish(dish);
        //插入后主键
        log.info("插入后主键:{}", dish.getId());
        Long id = dish.getId();//这是主键回显回来的id

        //添加完后为口味表中添加n条数据 n = 0~n 因为前端的口味的表单项可以不写
        List<DishFlavor> flavors = dishDTO.getFlavors();
        log.info("菜品风味:{}", flavors);
        if (flavors != null && flavors.size() > 0) {
            //因为此时还没有执行添加dishflavor的语句 说以此时还没有dishId值 可以通关dish表的添加语句通过逐主键回显来获取id
            for (DishFlavor flavor : flavors) {
                //为每一个菜品的flavor添加dishId
                flavor.setDishId(id);
            }
            dishFlavorMapper.insertFlavors(flavors);
        }
    }

    /**
     * 修改菜品
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //修改dish
        dishMapper.update(dish);
        //对口味进行修改 思路  先将原来的口味表中数据删除 然后添加新的数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());


        //删除后将新的数据添加进去
        List<DishFlavor> newDishFlavors = dishDTO.getFlavors();

        if (newDishFlavors != null && newDishFlavors.size() > 0) {
            for (DishFlavor newDishFlavor : newDishFlavors) {
                newDishFlavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertFlavors(newDishFlavors);
        }

    }

    @Override
    public DishVO selectById(Long id) {
        //根据id查询出菜品的值
        Dish dish = dishMapper.selectById(id);
        //再根据dishId的值查询出来菜品口味的值
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            //判断商品是否是启售中 如果是则无法删除
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否与套餐关联
        List<Long> id = setmealDishMapper.selectByDishIds(ids);
        if (id != null &&  id.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //批量删除菜品
        dishMapper.delete(ids);
        //批量删除口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public void startAndStop(Integer status, Long id) {
        Dish dish = new Dish();
        Long currentId = BaseContext.getCurrentId();
        dish.setStatus(status);
        dish.setId(id);
        dish.setUpdateUser(currentId);
        dishMapper.update(dish);
    }


    @Override
    public List<Dish> list(Long categoryId, String name) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .name(name)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }


    @Override
    public List<DishVO> getDishByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getDishByCategoryId(categoryId);
        List<DishVO> dishVOS = new ArrayList<>();
        //根据dishid查出菜品口味
        for (Dish dish : dishes) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(dish.getId());
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavors);
            dishVOS.add(dishVO);
        }
        return dishVOS;
    }
}
