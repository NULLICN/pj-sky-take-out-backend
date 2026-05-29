package xyz.nullicn.skytakeserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.nullicn.utils.PasswordUtil;

@SpringBootTest
class SkyTakeServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void generateEncodePassword() {
        String pas = PasswordUtil.hashPassword("123456");
        System.out.println("pas: " + pas);
    }
}
