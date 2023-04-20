package com.zzl.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.zzl.*.service.Impl..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("#### 开始执行{}.{} ####", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long takeTime = end - start;
        if (takeTime > 1500) {
            logger.warn("接口执行时间：{}ms",takeTime);
        }
        logger.info("接口执行时间：{}ms",takeTime);

        return result;
    }

}
