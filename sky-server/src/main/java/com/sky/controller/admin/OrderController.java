package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("要查询的订单id为：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }
    

    @GetMapping("/conditionSearch")
    @ApiOperation("查询历史订单")
    public Result<PageResult<OrderVO>> queryPage(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult<OrderVO> pageResult = orderService.queryPage(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrderForAdmin(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("要取消的订单id为:{}", ordersCancelDTO.getId());
        orderService.cancelOrderForAdmin(ordersCancelDTO);
        return Result.success();
    }

    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> countOrder(){
        OrderStatisticsVO statisticsVO = orderService.countOrder();
        return Result.success(statisticsVO);
    }
    
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("要拒绝的订单:{}",ordersRejectionDTO.getId());
        orderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable Long id){
        log.info("要派送的订单为:{}", id);
        orderService.deliveryOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Long id){
        log.info("完成配送的订单id为:{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
