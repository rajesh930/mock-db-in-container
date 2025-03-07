package com.ontic.perf.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author rajesh
 * @since 21/01/25 20:04
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface Track {
    String value() default "";
}
