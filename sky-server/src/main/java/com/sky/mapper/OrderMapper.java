package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void sumbitOrder(Orders orders);

    Orders getOrderByNumber(String orderNumber);

    void paySuccess(Orders orders);

    Orders getOrderById(Long id);

    List<Orders> queryPage(OrdersPageQueryDTO ordersPageQueryDTO);

    void update(Orders orders);

    Integer countStatus(Integer status);
}
