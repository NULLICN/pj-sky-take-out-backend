package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import xyz.nullicn.constant.JwtClaimsConstant;
import xyz.nullicn.dto.EmployeeDTO;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.dto.EmployeePageQueryDTO;
import xyz.nullicn.dto.PasswordEditDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.properties.JwtProperties;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import xyz.nullicn.utils.JwtUtil;
import xyz.nullicn.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee") // /admin/employee
@Slf4j
@Validated
@Tag(name = "员工管理接口", description = "员工登录相关操作")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @Operation(summary = "员工登录", description = "员工通过用户名和密码进行登录，登录成功后返回JWT令牌")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @Operation(summary = "员工退出", description = "员工退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @Operation(summary = "新增员工", description = "添加一个新员工，默认密码为123456，状态默认为启用")
    @PostMapping
    public Result<EmployeeDTO> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    @Operation(summary = "员工分页查询", description = "根据传入页号与每页条数返回数据")
    @GetMapping("/page")
    public Result<PageResult> page(@Valid EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页员工查询: {}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "启用禁用员工账号", description = "根据员工ID启用或禁用员工账号，status为0禁用，1启用")
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable @NotNull(message = "状态值不能为空") Integer status,
                                  @RequestParam @NotNull(message = "员工ID不能为空") Long id) {
        log.info("启用禁用员工账号：status={}, id={}", status, id);
        employeeService.updateStatus(status, id);
        return Result.success();
    }

    @Operation(summary = "根据id查询员工", description = "返回员工所有信息")
    @GetMapping("/{id}")
    public Result<Employee> searchById(@PathVariable @NotNull(message = "员工ID不能为空") Long id) {
        return Result.success(employeeService.getEmployee(id));
    }

    @Operation(summary = "更新员工数据", description = "根据员工id更新员工数据")
    @PutMapping
    public Result<String> editEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        employeeService.editEmployee(employeeDTO);
        return Result.success();
    }

    @Operation(summary = "修改密码", description = "")
    @PutMapping("/editPassword")
    public Result<String> editPassword(@Valid @RequestBody PasswordEditDTO passwordEditDTO) {
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}

