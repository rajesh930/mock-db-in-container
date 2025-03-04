package com.ontic.test.base;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author rajesh
 * @since 27/02/25 14:07
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface RequireES {
    /**
     * If elastic search is required for test case
     */
    boolean value() default true;

    /**
     * If server should be restarted to clean data
     */
    boolean requiresNew() default false;
}
