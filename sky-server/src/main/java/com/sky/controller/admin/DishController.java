package com.sky.controller.admin;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult<DishVO>> queryPage(DishPageQueryDTO dishPageQueryDTO){
        PageResult<DishVO> pr = dishService.queryPage(dishPageQueryDTO);

        return Result.success(pr);
    }
}
