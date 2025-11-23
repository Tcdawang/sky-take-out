package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端套餐浏览接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @GetMapping("/list")
    @ApiOperation("根据分类id显示套餐")
    //通过Cacheable这个注解可以查询缓存中是否有我们这个分类id的套餐如果有就直接从缓存中读数据 没有就执行sql再将数据存储到缓存
    @Cacheable(cacheNames = "setmeal", key="#categoryId")
    public Result<List<Setmeal>> getSetmealByCategoryId(Long categoryId){
        List<Setmeal> setmeals = setmealService.getSetmealByCategoryId(categoryId);
        return Result.success(setmeals);
    }


    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishBySetmealId(@PathVariable Long id){
        log.info("要查询的套餐id为:{}", id);
        List<DishItemVO> dishItemVOS = setmealService.getDishBySetmealId(id);
        return Result.success(dishItemVOS);
    }
}
