package xyz.nullicn.skytakeserver.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import xyz.nullicn.constant.AutoFillConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* xyz.nullicn.skytakeserver.mapper.*.*(..)) && @annotation(xyz.nullicn.skytakeserver.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    /**
     * 前置通知
     * @param joinPoint
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段填充");

        // 从方法签名上获取 @AutoFill 注解，读取操作类型（INSERT / UPDATE）
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        // 软约定：被注解的 Mapper 方法第一个参数为要填充的实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        // 当前时间和操作人ID，通过反射调用实体上的 setter 完成填充
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        int matchedCount = 0;

        if (operationType == OperationType.INSERT) {
            matchedCount += trySet(entity, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
            matchedCount += trySet(entity, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
            matchedCount += trySet(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            matchedCount += trySet(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        } else if (operationType == OperationType.UPDATE) {
            matchedCount += trySet(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            matchedCount += trySet(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }

        if (matchedCount == 0) {
            throw new RuntimeException("无任何一个方法得到匹配");
        }
    }

    /**
     * 尝试通过反射调用实体上的 setter 方法，实体没有该方法时静默跳过
     * @return 1=方法存在且调用成功，0=方法不存在（NoSuchMethodException）
     */
    private int trySet(Object entity, String methodName, Class<?> paramType, Object value) {
        try {
            Method method = entity.getClass().getDeclaredMethod(methodName, paramType);
            method.invoke(entity, value);
            return 1;
        } catch (NoSuchMethodException e) {
            return 0;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
