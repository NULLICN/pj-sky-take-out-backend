package xyz.nullicn.skytakeserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import xyz.nullicn.utils.PasswordUtil;

@SpringBootTest
class SkyTakeServerApplicationTests {

    @Autowired
    EmployeeService employeeService;

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
}
