package xyz.nullicn.skytakeserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.nullicn.skytakeserver.service.ShopService;

@Slf4j
@Service
public class ShopServiceImpl implements ShopService {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
    }

    @Override
    public Integer getStatus() {
        return (Integer) redisTemplate.opsForValue().get(KEY);
    }
}
