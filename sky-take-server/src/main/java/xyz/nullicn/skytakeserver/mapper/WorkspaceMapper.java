package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface WorkspaceMapper {

    Double getTodayTurnover(@Param("begin") LocalDate begin,
                            @Param("end") LocalDate end,
                            @Param("status") Integer status,
                            @Param("employeeId") Long employeeId);

    Integer getTodayValidOrderCount(@Param("begin") LocalDate begin,
                                    @Param("end") LocalDate end,
                                    @Param("status") Integer status,
                                    @Param("employeeId") Long employeeId);

    Integer getTodayTotalOrderCount(@Param("begin") LocalDate begin,
                                    @Param("end") LocalDate end,
                                    @Param("employeeId") Long employeeId);

    Integer getTodayNewUsers(@Param("begin") LocalDate begin,
                             @Param("end") LocalDate end,
                             @Param("employeeId") Long employeeId);

    Integer countSetmealByStatus(@Param("status") Integer status);

    Integer countDishByStatus(@Param("status") Integer status);

    Integer getTodayOrderCountByStatus(@Param("begin") LocalDate begin,
                                       @Param("end") LocalDate end,
                                       @Param("status") Integer status,
                                       @Param("employeeId") Long employeeId);
}
