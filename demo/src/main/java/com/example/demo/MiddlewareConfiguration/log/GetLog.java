package com.example.demo.MiddlewareConfiguration.log;


import java.lang.annotation.*;

/**
 * @author yc
 * @date 2023-12-04 22:05
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetLog {
    /**
     * 日志名称
     *
     * @return
     */
    String description() default "";

    /**
     * 日志类型
     *
     * @return
     */
    String type() default LogConstant.OPERATION;

    /**
     * 请求参数
     */
    String requestParam() default "无请求参数";
}