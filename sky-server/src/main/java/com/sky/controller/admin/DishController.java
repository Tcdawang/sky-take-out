package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;

import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult<DishVO>> queryPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询相关数据:{}", dishPageQueryDTO);
        PageResult<DishVO> pr = dishService.queryPage(dishPageQueryDTO);
        return Result.success(pr);
    }

    @PostMapping
    @ApiOperation("新增菜品")
    public Result insertDish(@RequestBody DishDTO dishDTO) {
        log.info("新增数据:{}", dishDTO);
        dishService.insertDish(dishDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        //查看要修改的值
        log.info("新增数据:{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> selectById(@PathVariable Long id) {
        log.info("要查询的id值为:{}",id);
        DishVO dishvo = dishService.selectById(id);
        return Result.success(dishvo);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId,String name) {
        log.info("要查询的分类id值为:{}",categoryId);
        List<Dish> dishes = dishService.list(categoryId,name);
        return Result.success(dishes);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("查看id:{}", ids);
        dishService.delete(ids);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    public Result startAndStop(@PathVariable Integer status, Long id){
        log.info("要启用或禁用的菜品id:{}", id);
        dishService.startAndStop(status,id);
        return Result.success();
    }
}
