package xyz.nullicn.skytakeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "xyz.nullicn")
@EnableCaching
@EnableScheduling
public class SkyTakeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyTakeServerApplication.class, args);
    }

}
