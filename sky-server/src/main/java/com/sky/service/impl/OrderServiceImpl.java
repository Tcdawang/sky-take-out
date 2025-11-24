package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    /**
     * 用户下单
     */
    @Transactional
    public OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO) {


        //接收前端订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(BaseContext.getCurrentId());
        //根据地址簿id获取配送信息
        AddressBook addressBook = addressBookMapper.getAdressById(orders.getAddressBookId());
        //先做业务异常的处理 地址簿不能为null 和 购物车不能没有数据
        if (addressBook == null){
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        //根据当前用户id获取购物车数据
        shoppingCart.setUserId(orders.getUserId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        String address  = addressBook.getProvinceName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());
        //在订单表中添加数据
        orderMapper.sumbitOrder(orders);
        //主键回显并向订单明细表添加数据
        Long ordersId = orders.getId();
        //遍历购物车,并将购物车数据添加到购物明细表中
        for (ShoppingCart cart : shoppingCartList) {
           OrderDetail orderDetail = OrderDetail.builder()
                   .orderId(ordersId)
                   .name(cart.getName())
                   .image(cart.getImage())
                   .dishId(cart.getDishId())
                   .setmealId(cart.getSetmealId())
                   .dishFlavor(cart.getDishFlavor())
                   .number(cart.getNumber())
                   .amount(cart.getAmount()).build();
           orderDetailMapper.insertOrderDetail(orderDetail);
        }
        //情况购物车结果
        shoppingCartMapper.delete(shoppingCart);
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO(orders.getId(), orders.getNumber(),
                orders.getAmount(), orders.getOrderTime());
        log.info("订单时间为:{}", orders.getOrderTime());
        return orderSubmitVO;
    }
}
