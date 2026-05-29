package xyz.nullicn.skytakeserver.service.impl;

import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.constant.StatusConstant;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.exception.AccountLockedException;
import xyz.nullicn.exception.AccountNotFoundException;
import xyz.nullicn.exception.PasswordErrorException;
import xyz.nullicn.skytakeserver.mapper.EmployeeMapper;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nullicn.utils.PasswordUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        /* 替代这里，使用hutool的加盐方式加密密码
        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
         */
        boolean passwordMath = PasswordUtil.checkPassword(password, employee.getPassword());
        if(!passwordMath) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

}
