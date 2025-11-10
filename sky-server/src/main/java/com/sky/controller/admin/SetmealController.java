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

import java.util.List;

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
    @ApiOperation("添加套餐")
    public Result insertSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("要添加的套餐为:{}", setmealDTO);
        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        SetmealVO sv = setmealService.selectById(id);
        return Result.success(sv);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("要修改的套餐数据为:{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        log.info("查看要批量删除的id:{}", ids);
        setmealService.delete(ids);
        return Result.success();
    }


    @PostMapping("status/{status}")
    @ApiOperation("起售和停售菜品")
    public Result startAndStop(@PathVariable Integer status, Long id){
        log.info("状态值为,要修改的id为:{}, {}", status, id);
        setmealService.startAndStop(status, id);
        return Result.success();
    }
}
