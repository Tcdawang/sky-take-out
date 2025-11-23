package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端购物车接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result insertShoppingCard(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车详情:{}", shoppingCartDTO.toString());
        shoppingCartService.insertShoppingCard(shoppingCartDTO);
        return Result.success();
    }


    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> carts = shoppingCartService.list();
        return Result.success(carts);
    }
}
