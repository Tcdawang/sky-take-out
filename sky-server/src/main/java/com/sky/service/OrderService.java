package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentOtherVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentOtherVO orderPay(OrdersPaymentDTO ordersPaymentDTO);

    void paySuccess(String orderNumber);

    OrderVO getOrderDetail(Long id);

    PageResult<OrderVO> queryPage(Integer page, Integer pageSize, Integer status);

    void repetition(Long id);

    void cancelOrder(Long id);
}
