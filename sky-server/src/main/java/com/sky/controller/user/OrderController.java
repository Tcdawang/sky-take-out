package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentOtherVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public Result<OrderPaymentOtherVO> orderPay(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("订单支付:{}", ordersPaymentDTO);
        //个人用户无法使用微信支付 因此我们直接模拟支付操作即可

        OrderPaymentOtherVO vo = orderService.orderPay(ordersPaymentDTO);

        //模拟支付成功的操作
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success(vo);
    }
}
