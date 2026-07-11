package xyz.nullicn.skytakeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "xyz.nullicn")
@EnableCaching
public class SkyTakeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyTakeServerApplication.class, args);
    }

}
