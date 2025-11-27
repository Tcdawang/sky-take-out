package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderPaymentOtherVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentOtherVO orderPay(OrdersPaymentDTO ordersPaymentDTO);

    void paySuccess(String orderNumber);
}
