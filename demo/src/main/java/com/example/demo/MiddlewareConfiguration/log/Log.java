package com.example.demo.MiddlewareConfiguration.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @author yc
 * @date 2024-01-21 21:31
 */
import java.time.LocalDateTime;
import java.util.Objects;

@TableName("t_log")
@ApiModel(value = "log对象", description = "日志对象 log")
public class Log {

    @TableId(value = "log_id", type = IdType.AUTO)
    @ApiModelProperty("日志ID")
    private Integer logId;

    @ApiModelProperty("日志类型")
    @TableField(value = "log_type")
    private Integer logType;

    @ApiModelProperty("日志等级")
    @TableField(value = "log_level")
    private String logLevel;

    @ApiModelProperty("日志信息")
    @TableField(value = "log_msg")
    private String logMsg;

    @ApiModelProperty("日志产生位置")
    @TableField(value = "log_site")
    private String logSite;

    @ApiModelProperty("日志产生类")
    @TableField(value = "log_class")
    private String logClass;

    @ApiModelProperty("日志是否已读")
    @TableField(value = "log_read")
    private Integer logRead;

    @ApiModelProperty("创建时间")
    @TableField(value = "log_create_time")
    private LocalDateTime logCreateTime;

    // No-args constructor
    public Log() {
    }

    // All-args constructor
    public Log(Integer logId, Integer logType, String logLevel, String logMsg, String logSite,
               String logClass, Integer logRead, LocalDateTime logCreateTime) {
        this.logId = logId;
        this.logType = logType;
        this.logLevel = logLevel;
        this.logMsg = logMsg;
        this.logSite = logSite;
        this.logClass = logClass;
        this.logRead = logRead;
        this.logCreateTime = logCreateTime;
    }

    // Getters and Setters
    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }

    public String getLogSite() {
        return logSite;
    }

    public void setLogSite(String logSite) {
        this.logSite = logSite;
    }

    public String getLogClass() {
        return logClass;
    }

    public void setLogClass(String logClass) {
        this.logClass = logClass;
    }

    public Integer getLogRead() {
        return logRead;
    }

    public void setLogRead(Integer logRead) {
        this.logRead = logRead;
    }

    public LocalDateTime getLogCreateTime() {
        return logCreateTime;
    }

    public void setLogCreateTime(LocalDateTime logCreateTime) {
        this.logCreateTime = logCreateTime;
    }

    // toString method
    @Override
    public String toString() {
        return "Log{" +
                "logId=" + logId +
                ", logType=" + logType +
                ", logLevel='" + logLevel + '\'' +
                ", logMsg='" + logMsg + '\'' +
                ", logSite='" + logSite + '\'' +
                ", logClass='" + logClass + '\'' +
                ", logRead=" + logRead +
                ", logCreateTime=" + logCreateTime +
                '}';
    }

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(logId, log.logId) && Objects.equals(logType, log.logType) && Objects.equals(logLevel, log.logLevel) && Objects.equals(logMsg, log.logMsg) && Objects.equals(logSite, log.logSite) && Objects.equals(logClass, log.logClass) && Objects.equals(logRead, log.logRead) && Objects.equals(logCreateTime, log.logCreateTime);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(logId, logType, logLevel, logMsg, logSite, logClass, logRead, logCreateTime);
    }
}