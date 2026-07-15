package xyz.nullicn.skytakeserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Value("${spring.cache.redis.key-prefix:}")
    private String keyPrefix;

    @Value("${spring.cache.redis.time-to-live:120s}")
    private Duration defaultTtl;

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redis模板类...");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(createObjectMapper());

        return builder -> builder
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                                .prefixCacheNameWith(keyPrefix)
                                .entryTtl(defaultTtl)
                )
                .withCacheConfiguration("dish",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                                .prefixCacheNameWith(keyPrefix)
                                .entryTtl(Duration.ofMinutes(5))
                )
                .withCacheConfiguration("employee",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                                .prefixCacheNameWith(keyPrefix)
                                .entryTtl(Duration.ofMinutes(30))
                );
    }

    // ======================== 统一 TTL 版本（无差异化过期时间） ========================
    // @Bean
    // public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    //     GenericJackson2JsonRedisSerializer jsonSerializer =
    //             new GenericJackson2JsonRedisSerializer(createObjectMapper());
    //
    //     return builder -> builder.cacheDefaults(
    //             RedisCacheConfiguration.defaultCacheConfig()
    //                     .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
    //                     .prefixCacheNameWith(keyPrefix)
    //     );
    // }
}
