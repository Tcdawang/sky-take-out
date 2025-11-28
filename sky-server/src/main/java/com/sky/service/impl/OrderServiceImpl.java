package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentOtherVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    /**
     * 订单支付
     */
    public OrderPaymentOtherVO orderPay(OrdersPaymentDTO ordersPaymentDTO) {
        //生成一个空的json对象
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code")!=null && jsonObject.getString("code").equals("ORDERPAID")){
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentOtherVO vo = jsonObject.toJavaObject(OrderPaymentOtherVO.class);
        return vo;
    }

    @Override
    /**
     * 模拟支付这个的状况
     */
    public void paySuccess(String orderNumber) {
        //根据订单号查询 订单
        Orders orders = orderMapper.getOrderByNumber(orderNumber);

        //根据订单id更新订单的状态、支付方式、支付状态、结账时间
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());

        orderMapper.paySuccess(orders);
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        //根据id 查询出来当前订单
        Orders orders = orderMapper.getOrderById(id);

        //查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetails = orderDetailMapper.getDetailByOrderId(orders.getId());

        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders, vo);
        vo.setOrderDetailList(orderDetails);
        return vo;
    }

    @Override
    public PageResult<OrderVO> queryPage(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult<OrderVO> pr = new PageResult<>();
        //开启分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        //调用mapper获取查询结果
        Page<Orders> orders = (Page<Orders>)orderMapper.queryPage(ordersPageQueryDTO);
        Page<OrderVO> orderVOS = new Page<>();
        for (Orders order : orders) {
            List<OrderDetail> detailByOrderId = orderDetailMapper.getDetailByOrderId(order.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(detailByOrderId);
            orderVOS.add(orderVO);
        }
        pr.setTotal(orderVOS.getTotal());
        pr.setRecords(orderVOS.getResult());
        return pr;
    }

    /**
     * 再来一单
     *
     * @param id
     */
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    /**
     * 取消订单
     */
    public void cancelOrder(Long id) {
        //根据id获取订单
        Orders order = orderMapper.getOrderById(id);
        // 校验订单是否存在
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(order.getId());
        // 订单处于待接单状态下取消，需要进行退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public OrderStatisticsVO countOrder() {
        // 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setToBeConfirmed(toBeConfirmed);
        vo.setConfirmed(confirmed);
        vo.setDeliveryInProgress(deliveryInProgress);

        return vo;
    }

    @Override
    /**
     * 用于管理端的接单
     */
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        //根据id查询订单是否存在
        Orders order = orderMapper.getOrderById(ordersConfirmDTO.getId());
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //再看此时订单的状态
        Integer status = order.getStatus();
        //如果订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款 不为2则抛出订单状态异常
        if (status != 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //然后对订单进行修改 修改订单的状态为已接单
        ordersConfirmDTO.setStatus(Orders.CONFIRMED );
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orderMapper.update(orders);
    }

    @Override
    public void cancelOrderForAdmin(OrdersCancelDTO ordersCancelDTO) {
        //根据id获取订单
        Long id = ordersCancelDTO.getId();
        Orders order = orderMapper.getOrderById(id);
        // 校验订单是否存在
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (order.getStatus() != 3) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders cancelOrder = new Orders();
        cancelOrder.setId(order.getId());
        // 订单处于接单状态下取消，需要进行退款
        if (order.getStatus().equals(Orders.CONFIRMED)) {
            //支付状态修改为 退款
            cancelOrder.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态、取消原因、取消时间
        cancelOrder.setStatus(Orders.CANCELLED);
        cancelOrder.setCancelReason(ordersCancelDTO.getCancelReason());
        cancelOrder.setCancelTime(LocalDateTime.now());
        orderMapper.update(cancelOrder);
    }

    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        //根据id查询订单是否存在
        Orders order = orderMapper.getOrderById(ordersRejectionDTO.getId());
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //再看此时订单的状态
        Integer status = order.getStatus();
        //如果订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款 不为2则抛出订单状态异常
        if (status != 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //然后对订单进行修改 设置拒单原因
        Orders orders = new Orders();
        // 订单处于接单状态下取消，需要进行退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        BeanUtils.copyProperties(order, orders);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    /**
     * 派送订单
     */
    public void deliveryOrder(Long id) {
        //根据id查询订单
        Orders order = orderMapper.getOrderById(id);
        if (order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //查询订单状态
        Integer status = order.getStatus();
        if (status != 3){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders deliveryOrder = new Orders();
        BeanUtils.copyProperties(order, deliveryOrder);
        deliveryOrder.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(deliveryOrder);
    }

    @Override
    public void completeOrder(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getOrderById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

}
