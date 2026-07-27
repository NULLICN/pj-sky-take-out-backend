package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.dto.OrdersPageQueryDTO;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;
import xyz.nullicn.vo.OrderStatisticsVO;
import xyz.nullicn.vo.OrderVO;

@Mapper
public interface OrderMapper {


    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into orders" +
            "        (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status," +
            "         amount, remark, user_name, phone, address, consignee, cancel_reason, rejection_reason," +
            "         cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number," +
            "         tableware_status)" +
            "        values (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{payMethod}, #{payStatus}," +
            "                #{amount}, #{remark}, #{userName}, #{phone}, #{address}, #{consignee}, #{cancelReason}, #{rejectionReason}," +
            "                #{cancelTime}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{deliveryTime}, #{packAmount}, #{tablewareNumber}," +
            "                #{tablewareStatus})")
    void insert(Orders orders);

    OrdersDTO getById(@Param("id") Long id, @Param("userId") Long userId);

    Page<OrdersDTO> pageQuery(@Param("status") Integer status, @Param("userId") Long userId);

    Orders getByNumber(@Param("number") String number, @Param("userId") Long userId);

    int update(Orders orders);

    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics(@Param("employeeId") Long employeeId);

    int adminCancel(@Param("id") Long id, @Param("cancelReason") String cancelReason,
                    @Param("cancelTime") LocalDateTime cancelTime, @Param("status") Integer status,
                    @Param("employeeId") Long employeeId);

    int adminRejection(@Param("id") Long id, @Param("rejectionReason") String rejectionReason,
                       @Param("status") Integer status, @Param("employeeId") Long employeeId);

    int adminComplete(@Param("id") Long id, @Param("status") Integer status,
                      @Param("employeeId") Long employeeId);

    int adminConfirm(@Param("id") Long id, @Param("status") Integer status,
                     @Param("employeeId") Long employeeId);

    int adminDelivery(@Param("id") Long id, @Param("status") Integer status,
                      @Param("employeeId") Long employeeId);

    int batchCancelTimeoutOrders(@Param("status") Integer status,
                                 @Param("timeoutThreshold") LocalDateTime timeoutThreshold,
                                 @Param("cancelledStatus") Integer cancelledStatus,
                                 @Param("cancelReason") String cancelReason,
                                 @Param("cancelTime") LocalDateTime cancelTime);

    int batchCompleteDeliveryOrders(@Param("status") Integer status,
                                    @Param("completedStatus") Integer completedStatus,
                                    @Param("deliveryTime") LocalDateTime deliveryTime);

    List<java.util.Map<String, Object>> getTurnoverByDate(@Param("begin") LocalDate begin,
                                                           @Param("end") LocalDate end,
                                                           @Param("status") Integer status,
                                                           @Param("employeeId") Long employeeId);

    List<java.util.Map<String, Object>> getOrderCountByDate(@Param("begin") LocalDate begin,
                                                             @Param("end") LocalDate end,
                                                             @Param("validStatus") Integer validStatus,
                                                             @Param("employeeId") Long employeeId);
}
