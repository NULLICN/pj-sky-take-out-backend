package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import xyz.nullicn.dto.OrdersDTO;
import xyz.nullicn.entity.OrderDetail;
import xyz.nullicn.entity.Orders;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insert(List<OrderDetail> orderDetail);

}
