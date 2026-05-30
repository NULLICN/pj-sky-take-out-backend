package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import xyz.nullicn.constant.JwtClaimsConstant;
import xyz.nullicn.dto.EmployeeDTO;
import xyz.nullicn.dto.EmployeeLoginDTO;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.properties.JwtProperties;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import xyz.nullicn.utils.JwtUtil;
import xyz.nullicn.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee") // /admin/employee
@Slf4j
@Tag(name = "员工管理接口", description = "员工登录相关操作")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @Operation(summary = "员工登录", description = "员工通过用户名和密码进行登录，登录成功后返回JWT令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "500", description = "用户名或密码错误，返回错误信息")
    })
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
    @ApiResponse(responseCode = "200", description = "退出成功")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @Operation(summary = "新增员工", description = "添加一个新员工，默认密码为123456，状态默认为启用")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "400", description = "参数校验失败，返回字段错误信息"),
        @ApiResponse(responseCode = "500", description = "用户名已存在或服务端错误")
    })
    @PostMapping
    public Result<EmployeeDTO> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        boolean result = employeeService.addEmployee(employeeDTO);
        if (result) {
            return Result.success();
        }
        return Result.error("新增员工失败");
    }


}
