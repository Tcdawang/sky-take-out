package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/list")
    public Result<List<DishVO>> getDishByCategoryId(Long categoryId){
        /*由于我们每次登陆小程序端我们点击各种分类的时候 都会去查询数据库 如果数据过多 会降低数据库效率 因此我们可以将查询出来的数据
          缓存到redis中 下次再看菜品或者套餐分类的时候 就直接走缓存 减少查询压力
        */
        //我们采用String的形式去存储数据 先定义key的形式
        String key = "dish_" + categoryId;
        //根据这个key先查询redis中是否有这个数据
        List<DishVO> dishVOS = (List<DishVO>)redisTemplate.opsForValue().get(key);
        //如果有我们就直接返回数据给前端
        if(dishVOS != null && dishVOS.size() > 0){
            return Result.success(dishVOS);
        }
        //如果没有我们就查询数据库 并将数据库中的数据缓存到redis中
        dishVOS = dishService.getDishByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key, dishVOS);
        return Result.success(dishVOS);
    }
}
