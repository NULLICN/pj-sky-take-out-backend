package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import xyz.nullicn.dto.EmployeePageQueryDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.result.PageResult;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("INSERT INTO employee(username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int insert(Employee employee);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO 分页查询DTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    @Update("update employee set status = #{status} where id = #{id}")
    int updateStatus(long id, int status);
}
