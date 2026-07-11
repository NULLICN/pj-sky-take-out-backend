package xyz.nullicn.skytakeserver.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.cache.annotation.Cacheable;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.constant.PasswordConstant;
import xyz.nullicn.constant.StatusConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.dto.EmployeeDTO;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.dto.EmployeePageQueryDTO;
import xyz.nullicn.dto.PasswordEditDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.exception.AccountLockedException;
import xyz.nullicn.exception.AccountNotFoundException;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.exception.PasswordErrorException;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.skytakeserver.mapper.EmployeeMapper;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.nullicn.utils.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    private long id;
    private long status;

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
        //
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

    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 检查用户名是否已存在
        Employee existing = employeeMapper.getByUsername(employeeDTO.getUsername());
        if (existing != null) {
            throw new BaseException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }

        // 此处dto到对象的映射，当前少字段可用此方式
        /*
        当字段达到20+或更多时考虑更换为MapStruct方式 性能更高
        20+ 字段的 DTO 映射
        多处需要相同映射（复用 Mapper 接口）
        字段名不一致需要 @Mapping 标注
        * */
        Employee employee = Employee.builder()
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .password(PasswordUtil.hashPassword(PasswordConstant.DEFAULT_PASSWORD))
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber())
                .status(StatusConstant.ENABLE)
                .build();

        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        String name = employeePageQueryDTO.getName();
        int page = employeePageQueryDTO.getPage();
        int pageSize = employeePageQueryDTO.getPageSize();

        PageHelper.startPage(page, pageSize);
        Page<Employee> aPage = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = aPage.getTotal();
        List<Employee> employees = aPage.getResult();

        return new PageResult(total, employees);
    }

    @Override
    public void updateStatus(int status, long id) {
        if (id <= 0) {
            throw new BaseException("员工ID必须为正整数");
        }
        if (status != 0 && status != 1) {
            throw new BaseException("状态值必须为0或1");
        }

        Employee employee = Employee.builder()
                                    .status(status)
                                    .id(id)
                                    .build();

        employeeMapper.update(employee);

    }

    @Override
    public Employee getEmployee(long id) {
        if (id <= 0) {
            throw new BaseException("员工ID必须为正整数");
        }

        Employee employee = employeeMapper.findById(id);
        employee.setPassword("2778");
        return employee;
    }

    @Override
    @Cacheable(value = "employee", key = "#id", unless = "#result == null")
    public Employee getEmployeeCached(long id) {
        return employeeMapper.findById(id);
    }

    @Override
    public void editEmployee(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber())
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        // BeanUtils.copyProperties(employeeDTO, employee); // 这行方法的缺点 反射方式，运行时可能才发现错误

        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
     String oldPassword = passwordEditDTO.getOldPassword();
     String newPassword = passwordEditDTO.getNewPassword();

        // 通过id先拿到员工旧密码然后对比加密新密码
        Employee employee = employeeMapper.findById(BaseContext.getCurrentId());

        boolean compareResult = PasswordUtil.checkPassword(oldPassword, employee.getPassword());

        if(!compareResult) {
            throw new PasswordErrorException("旧密码错误");
        }

        String newEncodePassword = PasswordUtil.hashPassword(newPassword);
        Employee newEmployeePasData = Employee.builder()
                .id(employee.getId())
                .password(newEncodePassword)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();


        employeeMapper.update(newEmployeePasData);
    }

}
