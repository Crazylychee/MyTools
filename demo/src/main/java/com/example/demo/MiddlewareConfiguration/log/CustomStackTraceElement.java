package com.example.demo.MiddlewareConfiguration.log;


/**
 * @author yc
 * @date 2024-01-31 16:21
 */

public class CustomStackTraceElement {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder result = new StringBuilder();

        for (StackTraceElement element : stackTrace) {
            result.append(formatStackTraceElement(element)).append("\n");
        }

        this.msg = result.toString();
    }

    private static String formatStackTraceElement(StackTraceElement element) {
        return element.getClassName() + "." + element.getMethodName() +
                "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
    }
}

