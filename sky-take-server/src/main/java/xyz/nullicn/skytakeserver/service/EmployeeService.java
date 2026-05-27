package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
