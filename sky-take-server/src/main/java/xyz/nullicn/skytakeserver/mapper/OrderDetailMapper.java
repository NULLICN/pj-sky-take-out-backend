package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.nullicn.entity.OrderDetail;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insert(List<OrderDetail> orderDetail);

    List<java.util.Map<String, Object>> getTop10Sales(@Param("begin") LocalDate begin,
                                                       @Param("end") LocalDate end,
                                                       @Param("status") Integer status,
                                                       @Param("employeeId") Long employeeId);
}
