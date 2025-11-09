package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;

public interface SetmealService {
   PageResult<SetmealVO> queryPage(SetmealPageQueryDTO setmealPageQueryDTO);

   void insertSetmeal(SetmealDTO setmealDTO);
}
