package com.ontic.perf.aspect;

import com.ontic.perf.tracker.Perf;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author rajesh
 * @since 21/01/25 20:11
 */
@Aspect
public class PerfAspect {

    @Around("execution(* *(..)) && @annotation(track)")
    public Object track(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        String tag = track.value().isEmpty() ? joinPoint.getSignature().toShortString() : track.value();
        try (Perf ignored = Perf.in(tag)) {
            return joinPoint.proceed();
        }
    }
}
