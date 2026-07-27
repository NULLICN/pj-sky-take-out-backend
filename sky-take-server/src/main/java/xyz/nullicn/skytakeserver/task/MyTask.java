package xyz.nullicn.skytakeserver.task;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class MyTask {

    /*@Scheduled(cron = "0/5 * * * * *")
    void task() {
        log.info("task {}", new Date());
    }*/
}
