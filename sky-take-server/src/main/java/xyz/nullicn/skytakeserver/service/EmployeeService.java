package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.EmployeeDTO;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.dto.EmployeePageQueryDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 员工登录数据模型
     * @return 员工对象
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 新员工数据模型
     * @return 是否创建成功
     */
    boolean addEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 更新员工账号状态
     * @param status
     * @param id
     */
    void updateStatus(int status, long id);

    /**
     * 查询一个员工数据
     * @param id
     * @return
     */
    Employee getEmployee(long id);

    /**
     * 更新员工数据
     * @param employeeDTO 员工新数据
     */
    void editEmployee(EmployeeDTO employeeDTO);
}
