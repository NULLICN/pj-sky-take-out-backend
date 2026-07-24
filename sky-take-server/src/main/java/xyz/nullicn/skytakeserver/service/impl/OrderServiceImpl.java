package xyz.nullicn.skytakeserver.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPageQueryDTO;
import xyz.nullicn.dto.OrdersPaymentDTO;
import xyz.nullicn.dto.OrdersSubmitDTO;
import xyz.nullicn.entity.*;
import xyz.nullicn.exception.AddressBookBusinessException;
import xyz.nullicn.exception.OrderBusinessException;
import xyz.nullicn.exception.ShoppingCartBusinessException;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.skytakeserver.converter.OrderConverter;
import xyz.nullicn.skytakeserver.converter.OrderDetailConvertor;
import xyz.nullicn.skytakeserver.mapper.OrderDetailMapper;
import xyz.nullicn.skytakeserver.mapper.OrderMapper;
import xyz.nullicn.skytakeserver.service.AddressBookService;
import xyz.nullicn.skytakeserver.service.OrderService;
import xyz.nullicn.skytakeserver.service.ShoppingCartService;
import xyz.nullicn.skytakeserver.service.UserService;
import xyz.nullicn.vo.OrderPaymentVO;
import xyz.nullicn.vo.OrderSubmitVO;
import xyz.nullicn.vo.OrderVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderConverter orderConverter;
    private final OrderDetailConvertor  orderDetailConvertor;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 检查地址是否存在
        AddressBook checkAddress = addressBookService.getById(ordersSubmitDTO.getAddressBookId());
        if(checkAddress == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 检查购物车是否为空
        List<ShoppingCart> goods = shoppingCartService.list();
        if(goods == null || goods.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 计算购物车商品总数量和商品总价
        int totalQuantity = goods.stream().mapToInt(ShoppingCart::getNumber).sum();

        BigDecimal goodsTotal = goods.stream()
                .map(cart -> cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 打包费 = 商品总数量 × 1元
        BigDecimal packagingFee = BigDecimal.valueOf(totalQuantity);

        // 订单总价 = 商品总价 + 打包费 + 配送费(6元)
        BigDecimal totalAmount = goodsTotal.add(packagingFee).add(BigDecimal.valueOf(6));

        // 向订单表插入订单数据
        LocalDateTime now = LocalDateTime.now();
        String address =
                checkAddress.getProvinceName() + " " +
                checkAddress.getCityName() + " " +
                checkAddress.getDistrictName() + " " +
                checkAddress.getDetail();

        Orders orders = orderConverter.toOrders(ordersSubmitDTO);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(now);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber("order" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        orders.setPackAmount(packagingFee.intValue());
        orders.setAmount(totalAmount);
        orders.setAddress(address);
        orders.setConsignee(checkAddress.getConsignee());
        orders.setPhone(checkAddress.getPhone());
        orders.setTablewareNumber(ordersSubmitDTO.getTablewareNumber());
        orders.setTablewareStatus(ordersSubmitDTO.getTablewareStatus());
        orders.setDeliveryStatus(ordersSubmitDTO.getDeliveryStatus());
        orders.setEstimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime());
        orderMapper.insert(orders);

        // 向订单明细插入购物车商品数据
        List<OrderDetail> orderDetails = orderDetailConvertor.toOrderDetails(goods);
        orderDetails.forEach(o -> o.setOrderId(orders.getId()));
        orderDetailMapper.insert(orderDetails);

        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderTime(now)
                .id(orders.getId())
                .orderAmount(totalAmount)
                .orderNumber(orders.getNumber())
                .build();

        // 清空购物车
        shoppingCartService.clean();

        return orderSubmitVO;
    }

    @Override
    public OrdersDTO getById(Long id) {
        OrdersDTO order = orderMapper.getById(id, BaseContext.getCurrentId());
        return order;
    }

    @Override
    public PageResult page(int page, int pageSize, int status) {
        try (Page<OrdersDTO> ignored = PageHelper.startPage(page, pageSize)) {
            Page<OrdersDTO> aPage = orderMapper.pageQuery(status == 0 ? null : status, BaseContext.getCurrentId());
            return new PageResult(aPage.getTotal(), aPage.getResult());
        }
    }

    @Override
    public PageResult pageSearch(OrdersPageQueryDTO dto) {
        try (Page<Orders> ignored = PageHelper.startPage(dto.getPage(), dto.getPageSize())) {
            Page<Orders> aPage = orderMapper.conditionSearch(dto);
            return new PageResult(aPage.getTotal(), aPage.getResult());
        }
    }

    @Override
    public OrdersDTO detail(Long id) {
        return orderMapper.getById(id, null);
    }

    @Override
    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 传入的订单号与userId做数据库查询，无结果则直接抛异常说明水平越权操作
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber(), BaseContext.getCurrentId());

        // 传入的订单号与userId做数据库查询，无结果则直接抛异常说明水平越权操作
        if (ordersDB == null) {
            throw new OrderBusinessException("订单不存在");
        }

//        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 直接模拟支付成功
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    @Override
    public void cancel(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .userId(BaseContext.getCurrentId())
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .expectedStatuses(List.of(Orders.PENDING_PAYMENT, Orders.TO_BE_CONFIRMED, Orders.CONFIRMED))
                .cancelReason("用户主动取消")
                .build();

        int row = orderMapper.update(orders);
        if (row == 0) {
            throw new OrderBusinessException("订单状态已变更，取消失败");
        }
    }

    @Override
    @Transactional
    public void repetition(Long id) {
        // 检查水平越权
        OrdersDTO ordersDB = orderMapper.getById(id, BaseContext.getCurrentId());
        if(ordersDB == null) {
            throw new OrderBusinessException("订单不存在");
        }
        // 查询订单id下的商品内容存放到购物车内
        List<OrderDetail> orderDetails = ordersDB.getOrderDetails();
        // 此方法内部会判断商品不存在或停售则直接抛出异常并回滚数据库操作
        shoppingCartService.addToCartBatch(orderDetails);
    }

    /**
     * 模拟支付成功的微信回调 把订单状态更改为已支付
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo, BaseContext.getCurrentId());
        if (ordersDB == null) {
            throw new OrderBusinessException("订单不存在");
        }
        if (!ordersDB.getStatus().equals(Orders.PENDING_PAYMENT)) {
            log.warn("订单状态异常，当前状态: {}, 订单号: {}", ordersDB.getStatus(), outTradeNo);
            throw new OrderBusinessException("订单状态已变更，无法完成支付");
        }

        // 乐观锁：UPDATE时带上status前置条件，防止并发取消导致的丢失更新
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .expectedStatuses(List.of(Orders.PENDING_PAYMENT))
                .userId(BaseContext.getCurrentId())
                .build();

        int rows = orderMapper.update(orders);
        if (rows == 0) {
            throw new OrderBusinessException("订单状态已变更，支付失败");
        }
    }
}
