package xyz.nullicn.skytakeserver.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.nullicn.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

/**
 * 登录接口限流拦截器
 * 基于客户端IP + Redis计数，限制单位时间内的登录请求次数
 */
@Component
@Slf4j
public class LoginRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 10;          // 窗口内最大请求次数
    private static final Duration WINDOW = Duration.ofMinutes(1);  // 统计窗口

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String ip = getClientIp(request);
        String key = "login:rate:" + ip;

        Long count;
        try {
            count = redisTemplate.opsForValue().increment(key);
            // 第一次进入窗口时设置过期时间，避免key永不过期
            if (count != null && count == 1L) {
                redisTemplate.expire(key, WINDOW);
            }
        } catch (Exception e) {
            // Redis故障时fail-open，避免影响正常登录
            log.warn("登录限流Redis异常，放行 ip={}", ip, e);
            return true;
        }

        if (count != null && count > MAX_REQUESTS) {
            log.warn("登录限流触发 ip={}, count={}", ip, count);
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    OBJECT_MAPPER.writeValueAsString(Result.error("登录请求过于频繁，请稍后再试")));
            return false;
        }

        return true;
    }

    /**
     * 获取客户端真实IP，处理反向代理场景
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip == null ? "unknown" : ip;
    }
}
