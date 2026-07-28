package xyz.nullicn.skytakeserver;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
public class HttpClientTest {

    @Test
    void testToday() {
        LocalDate today = LocalDate.now();
        System.out.println(today.toString());
    }

}
