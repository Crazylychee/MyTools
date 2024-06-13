package com.example.demo.MiddlewareConfiguration.log;

/**
 * @author yc
 * @date 2024-01-31 22:21
 */
public class LogConstant {

    /**
     * 日志类型枚举
     */
    /**
     * 系统日志
     **/
    public static final int LOG_SYSTEM_NORMAL = 0;
    /**
     * 系统日志
     */
    public static final int LOG_SYSTEM = 100;

    /**
     * 操作日志
     */
    public static final int LOG_OPERATION = 101;

    /**
     * 新生日志
     */
    public static final int LOG_NEWER = 102;


    public static final String OPERATION = "INFO";

    public static final String ERROR = "ERROR";

    public static final String NEWER = "NEWER";

    public static final String SYSTEM = "SYSTEM";

    public static final String OPERATION_LOG = "OPERATION";
}
