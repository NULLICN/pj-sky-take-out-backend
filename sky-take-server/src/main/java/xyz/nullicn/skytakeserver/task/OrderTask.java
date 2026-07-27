package xyz.nullicn.skytakeserver.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.skytakeserver.mapper.OrderMapper;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTask {

    private final OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("开始处理超时订单...");
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(15);
        int count = orderMapper.batchCancelTimeoutOrders(
                Orders.PENDING_PAYMENT,
                timeoutThreshold,
                Orders.CANCELLED,
                "订单超时",
                LocalDateTime.now()
        );
        log.info("处理超时订单完成，共取消 {} 个订单", count);
    }


    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("开始处理派送中订单...");
        int count = orderMapper.batchCompleteDeliveryOrders(
                Orders.DELIVERY_IN_PROGRESS,
                Orders.COMPLETED,
                LocalDateTime.now()
        );
        log.info("处理派送中订单完成，共完成 {} 个订单", count);
    }

}
