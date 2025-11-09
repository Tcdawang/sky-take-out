package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult<SetmealVO>> queryPage(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult<SetmealVO> pr  = setmealService.queryPage(setmealPageQueryDTO);
        return Result.success(pr);
    }

    @PostMapping
    public Result insertSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("要添加的套餐为:{}", setmealDTO);
        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }
}
