package xyz.nullicn.skytakeserver;

import cn.hutool.core.bean.BeanUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.utils.PasswordUtil;
import xyz.nullicn.vo.SetmealVO;

@SpringBootTest
class SkyTakeServerApplicationTests {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealMapper setmealMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void generateEncodePassword() {
        String pas = PasswordUtil.hashPassword("123456");
        System.out.println("pas: " + pas);
    }

    @Test
    void searchEmployeeTest() {
        System.out.println(employeeService.getEmployee(1));

    }

    @Test
    void getSetmealByIdTest() {
        Setmeal setmeal = setmealMapper.getById(33L);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtil.copyProperties(setmeal, setmealVO);
        System.out.println(setmeal);
        System.out.println(setmealVO);
    }
}
