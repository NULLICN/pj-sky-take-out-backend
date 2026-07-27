package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xyz.nullicn.entity.User;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    Integer countUsersBeforeDate(@Param("date") LocalDate date,
                                  @Param("employeeId") Long employeeId);

    List<java.util.Map<String, Object>> getNewUserCountByDate(@Param("begin") LocalDate begin,
                                                               @Param("end") LocalDate end,
                                                               @Param("employeeId") Long employeeId);
}
