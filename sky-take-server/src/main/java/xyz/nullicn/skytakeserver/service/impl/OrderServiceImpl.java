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

        PageHelper.startPage(page, pageSize);
        Page<OrdersDTO> aPage = orderMapper.pageQuery(status == 0 ? null : status, BaseContext.getCurrentId());

        long total = aPage.getTotal();
        List<OrdersDTO> orders = aPage.getResult();

        return new PageResult(total, orders);
    }

    @Override
    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 传入的订单号与userId做数据库查询，无结果则直接抛异常说明水平越权操作

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

    /**
     * 模拟支付成功的微信回调 把订单状态更改为已支付
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo, BaseContext.getCurrentId());

        // 传入的订单号与userId做数据库查询，无结果则直接抛异常说明水平越权操作
        if (ordersDB == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .userId(BaseContext.getCurrentId())
                .build();

        orderMapper.update(orders);
    }
}
