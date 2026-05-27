package xyz.nullicn.skytakeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "xyz.nullicn")
public class SkyTakeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyTakeServerApplication.class, args);
    }

}
