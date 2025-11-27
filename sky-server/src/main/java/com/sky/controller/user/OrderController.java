package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "订单管理")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> sumbitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.sumbitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    @PutMapping("/payment")
    @ApiOperation("微信支付")
    public Result<OrderPaymentOtherVO> orderPay(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("订单支付:{}", ordersPaymentDTO);
        //个人用户无法使用微信支付 因此我们直接模拟支付操作即可

        OrderPaymentOtherVO vo = orderService.orderPay(ordersPaymentDTO);

        //模拟支付成功的操作
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(vo);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("要查询的订单id为：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult<OrderVO>> queryPage(Integer page, Integer pageSize, Integer status){
        PageResult<OrderVO> pageResult = orderService.queryPage(page, pageSize, status);
        return Result.success(pageResult);
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }


    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){

        log.info("要取消的订单id为:{}", id);
        orderService.cancelOrder(id);
        return Result.success();
    }
}
