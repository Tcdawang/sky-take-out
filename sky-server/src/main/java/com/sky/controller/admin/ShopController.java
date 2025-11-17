package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺营业操作")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
      log.info("店铺营业状态值为: {}", status);
      //将此时的状态值存储到redis中
        redisTemplate.opsForValue().set("SHOP_STATUS", status);
      return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("查看店铺营业状态")
    public Result<Integer> getStatus(){
        //在redis中获取存储到的status的值
        Integer statu = (Integer)redisTemplate.opsForValue().get("SHOP_STATUS");
        return Result.success(statu);
    }
}
