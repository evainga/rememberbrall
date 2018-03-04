package de.rememberbrall;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class MethodExecutionTimeAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(de.rememberbrall.TrackTime)")
    public Object measureMethodExecutionTimes(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object returnObj = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;

        logger.info("Time taken by method execution of {} is {} milliseconds", joinPoint.getSignature().getName(), timeTaken);
        return returnObj;
    }

}