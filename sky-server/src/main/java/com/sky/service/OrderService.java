package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentOtherVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentOtherVO orderPay(OrdersPaymentDTO ordersPaymentDTO);

    void paySuccess(String orderNumber);

    OrderVO getOrderDetail(Long id);

    PageResult<OrderVO> queryPage(OrdersPageQueryDTO ordersPageQueryDTO);

    void repetition(Long id);

    void cancelOrder(Long id);

    OrderStatisticsVO countOrder();


    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void cancelOrderForAdmin(OrdersCancelDTO ordersCancelDTO);

    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    void deliveryOrder(Long id);

    void completeOrder(Long id);
}
