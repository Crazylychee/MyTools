package com.example.demo.MiddlewareConfiguration.log;


import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.example.demo.MiddlewareConfiguration.log.LogConstant.*;


/**
 * @author yc
 * 自定义日志格式
 * @date 2024-01-21 20:49
 */
public class SystemLog {

    /**
     * 日志类型（全部日志，默认为简单系统日志）
     */
    private Integer logType = LOG_SYSTEM_NORMAL;

    /**
     * 日志信息
     */
    private String logMsg;

    /**
     * 日志定位
     */
    private String logSite;

    /**
     * 日志出现的类
     */
    private String logClass;

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getLogClass() {
        return logClass;
    }

    public void setLogClass(String logClass) {
        this.logClass = logClass;
    }

    public String getLogSite() {
        return logSite;
    }

    public void setLogSite(String logSite) {
        this.logSite = logSite;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }

    /**
     * 构造自定义日志
     */
    public SystemLog(ILoggingEvent event) {
        //TODO: 在这里通过event得到对应的日志类型和日志信息
        this.logType = getTypeFromEvent(event);
        bindStackTraceElement(getLogMsgFromEvent(event));
    }


    private String getLogMsgFromEvent(ILoggingEvent event) {
        String msg = event.getFormattedMessage();

        return extractFirstWord(msg);
    }

    private String extractFirstWord(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        int firstSpaceIndex = message.indexOf(" | ");
        if (firstSpaceIndex != -1 && firstSpaceIndex < message.length() - 1) {
            return message.substring(firstSpaceIndex);
        } else {
            return message;
        }
    }

    private Integer getTypeFromEvent(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        Integer index = msg.indexOf(" | ");
        if (index == -1) {
            return LOG_SYSTEM_NORMAL;
        }
        String type = msg.substring(0, index);
        switch (type) {
            case LogConstant.SYSTEM:
                return LOG_SYSTEM;
            case LogConstant.OPERATION:
                return LOG_OPERATION;
            case LogConstant.NEWER:
                return LOG_NEWER;
            default:
                return LOG_SYSTEM_NORMAL;
        }
    }

    private void bindStackTraceElement(String log) {
        // 存储堆栈信息的变量
        StringBuilder stackTraceBuilder = new StringBuilder();

        // 查找以 |## 开始的堆栈信息部分
        int stackTraceIndex = log.indexOf("|##");

        // 如果找到了堆栈信息部分，将其添加到 StringBuilder 中
        if (stackTraceIndex != -1) {
            String stackTracePart = log.substring(stackTraceIndex);
            // 将堆栈信息以换行符分割，获取前三行
            String[] stackTraceLines = stackTracePart.split("\n", 4);

            bindLogSiteAndClass(stackTraceLines[0]);
            // 将堆栈信息添加到 StringBuilder 中
            for (int i = 0; i < stackTraceLines.length - 1; i++) {

                // 判断是否为最后一个元素，如果不是则追加换行符
                stackTraceBuilder.append(stackTraceLines[i]);
                stackTraceBuilder.append("\n");
            }
            // 获取存储的堆栈信息
            String simpleStackTrace = stackTraceBuilder.toString();

            StringBuilder logMsgBuilder = new StringBuilder();
            //把原来的堆栈信息去掉
            logMsgBuilder.append(log.substring(0, stackTraceIndex))
                    .append(simpleStackTrace);
            this.logMsg = logMsgBuilder.toString();
        } else {
            this.logMsg = log;
        }


    }

    private void bindLogSiteAndClass(String stackTraceLine) {
        int startIndex = stackTraceLine.indexOf("(");
        int endIndex = stackTraceLine.indexOf(")");

        if (startIndex != -1 && endIndex != -1) {
            this.logSite = stackTraceLine.substring(startIndex + 1, endIndex);
            this.logClass = stackTraceLine.substring(3, startIndex);
        }
    }


    public String getLogMessage() {
        return this.logMsg;
    }


    public Integer getLogType() {
        return this.logType;
    }

    public static String buildLogMsg(String logType, String description, String type, String requestUrl, String method, String logParams, Integer logElapsedTime, Object result) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = null;

        

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(logType)
                .append(" | ");

        // 添加 description、type、logParams 和 logElapsedTime，并在它们之间添加 | 分隔符
        stringBuilder.append(name+description)
                .append(" | ")
                .append(type)
                .append(" | ")
                .append(requestUrl)
                .append(" | ")
                .append(method)
                .append(" | ")
                .append(logParams)
                .append(" | ")
                .append(logElapsedTime)
                .append(" | ");
        if (result != null) {
            stringBuilder.append(result.toString());
        }


        // 返回拼接后的字符串
        return stringBuilder.toString();
    }

    public static String buildErrLogMsg(String system, String description, String type, String requestURI, String method, String string, Exception e) {

        StringBuilder stringBuilder = new StringBuilder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = null;

        CustomStackTraceElement customStackTraceElement = new CustomStackTraceElement();
        customStackTraceElement.formatStackTrace(e.getStackTrace());

        stringBuilder.append(system)
                .append(" | ");

        // 添加 description、type、logParams 和 logElapsedTime，并在它们之间添加 | 分隔符
        stringBuilder.append(name+description)
                .append(" | ")
                .append(type)
                .append(" | ")
                .append(requestURI)
                .append(" | ")
                .append(method)
                .append(" | ")
                .append(string)
                .append(" | ")
                .append(e.getClass().getName())
                .append(" | ")
                .append(e.getMessage())
                .append(" |## ")
                .append(customStackTraceElement.getMsg());

        // 返回拼接后的字符串
        return stringBuilder.toString();
    }
}

