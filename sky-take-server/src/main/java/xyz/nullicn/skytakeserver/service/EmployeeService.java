package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.EmployeeDTO;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.entity.Employee;

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
}
