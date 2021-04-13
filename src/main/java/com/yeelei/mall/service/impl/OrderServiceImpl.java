package com.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.yeelei.mall.common.Constant;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
import com.yeelei.mall.filter.UserFilter;
import com.yeelei.mall.model.dao.CartMapper;
import com.yeelei.mall.model.dao.OrderItemMapper;
import com.yeelei.mall.model.dao.OrderMapper;
import com.yeelei.mall.model.dao.ProductMapper;
import com.yeelei.mall.model.pojo.Cart;
import com.yeelei.mall.model.pojo.Order;
import com.yeelei.mall.model.pojo.OrderItem;
import com.yeelei.mall.model.pojo.Product;
import com.yeelei.mall.model.query.CreateOrderReq;
import com.yeelei.mall.model.vo.CartVO;
import com.yeelei.mall.model.vo.CategoryVO;
import com.yeelei.mall.model.vo.OrderItemVO;
import com.yeelei.mall.model.vo.OrderVO;
import com.yeelei.mall.service.CartService;
import com.yeelei.mall.service.OrderService;
import com.yeelei.mall.utils.OrderCodeFactory;
import com.yeelei.mall.utils.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：订单Service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Value("${file.upload.ip}")
    private String ip;

    //开启数据库事务

    /**
     * 创建订单
     * @param createOrderReq 创建订单请求参数
     * @return 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户id
        Integer userId = UserFilter.currUser.getId();
        List<CartVO> cartVOList = cartService.list(userId);
        //查出已勾选的商品
        List<CartVO> cartVOListTemp = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转化为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //把购物车中的已勾选的商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //插入到Order表
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在，商品是否上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_SALE);
            }
            if (product.getStock() < cartVO.getQuantity()) {
                throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    /**
     * 根据订单号查询订单详情
     * @param orderNo 订单号
     * @return 订单VO
     */
    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        //判断订单所属
        if (!UserFilter.currUser.getId().equals(order.getUserId())) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOS = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOS.add(orderItemVO);
        }
        //设置orderItemVOList
        orderVO.setOrderItemVOList(orderItemVOS);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    /**
     * 查询前台订单列表
     * @param pageNum 页码
     * @param pageSize 每页显示的条数
     * @return 分页列表
     */
    @Override
    public PageInfo listOrderForConsumer(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //获取当前用户id
        Integer userId = UserFilter.currUser.getId();
        //根据userId获取orderList
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        //根据orderList获取orderVOList
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOS = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOS.add(orderVO);
        }
        return orderVOS;
    }

    /**
     * 根据订单号取消订单
     * @param orderNo 订单号
     */
    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //订单不存在存在
        if (order == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        //需要判断所属
        Integer userId = UserFilter.currUser.getId();
        if (!order.getUserId().equals(userId)) {
            //不是自己的订单
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CANCEL_ERROR);
        }
        //如果用户订单状态为未付款，则可以取消
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.WRONG_ORDER_STATUS);
        } else {
            //用户未付款，可以取消
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }

    /**
     * 根据订单号生成前台支付二维码图片
     * @param orderNo 订单号
     * @return
     */
    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //支付url
        String url = "http://" + ip + ":" + request.getLocalPort() + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(url, 350, 350,
                    Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //二维码地址
        String pngAddress = "http://" + ip + ":" + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    /**
     * 根据订单号支付订单
     * @param orderNo 订单号
     */
    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        //需要判断所属
        Integer userId = UserFilter.currUser.getId();
        if (!order.getUserId().equals(userId)) {
            //不是自己的订单
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CANCEL_ERROR);
        }
        //判断订单状态是否支付
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            //未支付
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.WRONG_ORDER_STATUS);
        } else {
            //已支付
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setPayTime(new Date());
            //更新订单
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }

    /**
     * 查询后台订单列表
     * @param pageNum 页码
     * @param pageSize 每页显示的条数
     * @return 分页列表
     */
    @Override
    public PageInfo listOrderForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * 根据订单号进行发货
     * @param orderNo 订单号
     */
    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单报错
        if (order == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        //判断订单状态是否支付
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            //未付款状态
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.WRONG_ORDER_STATUS);
        } else {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }

    }

    /**
     * 根据订单号进行签收
     * @param orderNo 订单号
     */
    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单报错
        if (order == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NO_ORDER);
        }
        //判断订单状态是否发货
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            //未付款状态
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.WRONG_ORDER_STATUS);
        } else {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
    }
}
