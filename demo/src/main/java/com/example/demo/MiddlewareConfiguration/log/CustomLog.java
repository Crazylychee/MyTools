package com.example.demo.MiddlewareConfiguration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author : Ice'Clean
 * @date : 2022-01-16
 * <p>
 * 生成自定义日志格式
 */
public class CustomLog {
    /**
     * 日志类型枚举
     */
    public static final int LOG_SYSTEM_NORMAL = 0;
    public static final int LOG_SYSTEM = 100;
    public static final int LOG_OPERATION = 101;
    public static final int LOG_NEWER = 102;


    /**
     * 日志级别
     */
    private String level = "INFO";
    /**
     * 日志类型（全部日志，默认为简单系统日志）
     */
    private Integer logType = LOG_SYSTEM_NORMAL;
    /**
     * 基础日志信息（全部日志）
     */
    private final ArrayList<String> logMsgList = new ArrayList<>(5);

    /**
     * 日志的请求链接（系统日志，针对于 Controller 层）
     */
    private String requestUrl;
    /**
     * 产生日志的类名，方法名（系统日志）
     */
    private String className, methodName;
    /**
     * 产生日志的方法中的参数（系统日志）
     */
    private Integer parameterCount;
    private String parameters;
    /**
     * 调用链信息（系统日志，针对于 ERROR 日志）
     */
    private final ArrayList<StackTraceElement> stackMsg = new ArrayList<>(10);

    public CustomLog() {
    }

    public CustomLog(Integer logType) {
        this.logType = logType;
    }

    public CustomLog(Integer logType, String logMessage) {
        this.logType = logType;
        this.logMsgList.add(getOutCaller() + logMessage);
    }

    /**
     * 将原生的日志格式，解析成自定义日志格式
     */
    public CustomLog(ILoggingEvent event) {
        try {
            parseMessage(event.getFormattedMessage());
            // 如果日志级别是 ERROR 的话，给出调用链信息
            if (event.getLevel().equals(Level.ERROR)) {
                addStackTrace(event.getCallerData());
            }
        } catch (Exception e) {
            // 如果不是规定的格式，则进行格式解析
            StackTraceElement caller = event.getCallerData()[1];
            logMsgList.add("[" + Objects.requireNonNull(caller.getFileName())
                    .substring(0, caller.getFileName().length() - 5) + "_"
                    + caller.getMethodName() + "_" + caller.getLineNumber() + "] "
                    + event.getFormattedMessage());
        }
    }

    /**
     * 解析日志信息
     */
    private void parseMessage(String logMessage) {
        // 在获取类型这里，判断输入的日志是否为自定义日志 customLog 的格式
        logType = Integer.parseInt(logMessage.substring(0, 3));
        // 拆分出源日志信息
        String[] logMsgArray = logMessage.split("\\|");

        // 如果是系统日志的话，解析详细系统日志信息
        if (logType == LOG_SYSTEM) {
            // 拆分出请求连接，类名，方法名
            requestUrl = logMsgArray[1].split(" : ")[1].trim();
            className = logMsgArray[2].split(" : ")[1].trim();
            methodName = logMsgArray[3].split(" : ")[1].trim();

            // 拆分出参数
            String[] parameterArray = logMsgArray[4].split(" : ")[1].split(" >> ");
            parameterCount = Integer.parseInt(parameterArray[0]);
            parameters = parameterArray[1].trim();
        }

        // 解析详细信息（是系统日志的话，取第 5 ，否则取第 1）
        String[] messageArray = logMsgArray[logType == LOG_SYSTEM ? 5 : 1]
                .split(" : ")[1].split(" >> ");
        for (int i = 1; i < messageArray.length; i++) {
            logMsgList.add(messageArray[i].trim());
        }
    }

    /**
     * 获取调用本类方法的外部方法信息（本类中调用本类不算）
     * 偏移量 offset 表示要显示的外部方法的第几个父类
     * 0 表示调用方法本身，1 表示调用方法的调用者，以此类推
     */
    private String getOutCaller() {
        // 数组中的 3 表示外部方法，1 表示本层方法，2 表示上一层方法（即在本类调用），所以 3 才是外部
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        return "[" + Objects.requireNonNull(caller.getFileName())
                .substring(0, caller.getFileName().length() - 5) + "_"
                + caller.getMethodName() + "_" + caller.getLineNumber() + "] ";
    }

    /**
     * 添加携带调用者信息的日志信息，默认会附上日志所在类所在方法所在行数
     */
    public void addMessage(String message) {
        logMsgList.add(getOutCaller() + message);
    }

    /**
     * 添加普通日志信息，没有调用者信息
     */
    public void addMessageNoCaller(String message) {
        logMsgList.add(message);
    }

    /**
     * 添加调用链信息
     */
    public void addStackTrace(StackTraceElement[] elements) {
        stackMsg.addAll(Arrays.asList(elements));
    }

    /**
     * 设置日志类型
     */
    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    /**
     * 设置日志调用父类名字
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 设置日志调用方法名字
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 设置日志调用方法的参数个数、参数名以及参数值
     */
    public void setParameter(Parameter[] parameters, Object[] datum) {
        // 参数长度是真正的参数长度减去 1 个自定义日志格式
        this.parameterCount = parameters.length - 1;
        StringBuilder paramBuilder = new StringBuilder();
        // 将参数名和对应的参数值对上，这里兼容了自定义日志类型位置不确定的情况
        for (int i = 0; i <= parameterCount; i++) {
            if (!parameters[i].getType().equals(CustomLog.class)) {
                paramBuilder.append("[").append(parameters[i].getName())
                        .append("=").append(datum[i]).append("] ");
            }
        }
        // 为参数日志赋值
        this.parameters = paramBuilder.toString();
    }

    /**
     * 设置请求链接
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * 获取日志类型
     */
    public int getLogType() {
        return logType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取数据库存储格式化日志信息
     * 不会拼接上日志类型 type，用于最后写入数据库用
     */
    public String getLogMessage() {
        StringBuilder logMessage = new StringBuilder();

        if (logType == LOG_SYSTEM) {
            // 将详细信息格式化
            logMessage.append(requestUrl).append("|");
            logMessage.append(className).append("|");
            logMessage.append(methodName).append("|");
            logMessage.append(parameterCount).append(" >> ").append(parameters).append("|");
        }

        // 将日志信息列表格式化
        for (String message : logMsgList) {
            logMessage.append(message).append("##");
        }
        logMessage.append("|");
        // 将堆栈调用列表格式化
        for (StackTraceElement stack : stackMsg) {
            logMessage.append(stack).append("##");
        }
        return logMessage.toString();
    }

    /**
     * 在初始化自定义日志时使用
     * 往后要获取自定义日志时，需使用 getCustomLog
     * 同时控制台输出的内容也是按这个格式
     */
    @Override
    public String toString() {
        StringBuilder formatMessage = new StringBuilder();

        if (logType == LOG_SYSTEM) {
            // 将详细信息格式化
            formatMessage.append("\n\t|请求连接 : ").append(requestUrl);
            formatMessage.append("\n\t|所属父类 : ").append(className);
            formatMessage.append("\n\t|所属方法 : ").append(methodName);
            formatMessage.append("\n\t|传入参数 : ").append(parameterCount).append(" >> ").append(parameters);
        }

        // 将日志信息列表格式化
        formatMessage.append("\n\t|信息输出 : ");
        for (String message : logMsgList) {
            formatMessage.append("\n\t >> ").append(message);
        }

        // 将堆栈信息列表格式化
        if (stackMsg.size() > 0) {
            formatMessage.append("\n\t|堆栈调用 : ");
            for (StackTraceElement stack : stackMsg) {
                formatMessage.append("\n\t >> ").append(stack);
            }
        }
        return logType + formatMessage.toString();
    }
}
