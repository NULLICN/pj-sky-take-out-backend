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

    /**
     * 员工账号状态更新
     * @param id 员工id
     * @param status 账号状态（0禁用，1启用）
     * @return
     */
    @Update("update employee set status = #{status} where id = #{id}")
    int updateStatus(long id, int status);

    /**
     * 更新员工数据
     * @param employee 员工新数据
     */
    void update(Employee employee);

    /**
     * 查询一个员工的所有数据
     * @param id 员工id
     * @return 对应员工数据
     */
    @Select("SELECT * FROM employee where id = #{id}")
    Employee findById(long id);
}
