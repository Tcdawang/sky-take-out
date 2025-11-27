package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    void sumbitOrder(Orders orders);

    Orders getOrderByNumber(String orderNumber);

    void paySuccess(Orders orders);

    Orders getOrderById(Long id);

    List<Orders> queryPage(Integer status, Long userId);

    void update(Orders orders);
}
