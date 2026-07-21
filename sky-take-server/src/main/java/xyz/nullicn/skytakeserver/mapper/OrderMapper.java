package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;
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

    void update(Orders orders);
}
