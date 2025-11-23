package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Stack;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    @Transactional
    public void insertShoppingCard(ShoppingCartDTO shoppingCartDTO) {
        //添加之前先查询菜品表中有没有这个数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        //如果不为null 那么再次添加一样的商品或者套餐时就执行一个update操作 将数量加1即可
        if (shoppingCartList != null &&  shoppingCartList.size() > 0){
            ShoppingCart cart = shoppingCartList.get(0);
            shoppingCartMapper.updateNumber(cart);
        }else {
            //如果为null 就根据是添加的菜品还是套餐来添加数据
            //如果添加的是菜品根据菜品id查询菜品
            if (shoppingCartDTO.getDishId() != null){
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
            }

            //如果添加的是套餐 就根据套餐id查询套餐
            if (shoppingCartDTO.getSetmealId() != null){
                Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
            }
            shoppingCartMapper.insertShoppingCard(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        //根据菜品id 和 套餐id 查找套餐要删除购物车中一份的数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        ShoppingCart deleteShoppingCart = shoppingCartList.get(0);
        //实际上是一个修改操作 将份数-1即可
        if (deleteShoppingCart != null){
            //如果份数为1 则自己在购物车中删除
            Integer number = deleteShoppingCart.getNumber();
            if (number <= 1){
                shoppingCartMapper.delete(deleteShoppingCart);
            }
            shoppingCartMapper.deleteOne(deleteShoppingCart);
        }
    }

    /**
     * 清空购物车
     */

    @Override
    public void deleteAll() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
    }
}
