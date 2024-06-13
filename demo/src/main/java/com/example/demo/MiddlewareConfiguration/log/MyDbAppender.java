package com.example.demo.MiddlewareConfiguration.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author yc
 * @date 2024-01-21 21:54
 */
public class MyDbAppender extends DBAppenderBase<ILoggingEvent> {
    /**
     * 插入语句
     */
    protected static final String INSERT_SQL;
    /**
     * 用于在执行插入语句后获取生成的主键
     */
    protected static final Method GET_GENERATED_KEYS_METHOD;

    /**
     * 日志表中的字段
     */
    static final int LOG_TYPE = 1;
    static final int LOG_LEVEL = 2;
    static final int LOG_MSG = 3;
    static final int LOG_SITE = 4;
    static final int LOG_CLASS = 5;
    static final int LOG_READ = 6;

    static {
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
        INSERT_SQL = "INSERT INTO t_log (log_type,log_level,log_msg,log_site,log_class,log_read) VALUES (?, ?, ?, ?, ? ,? )";
    }

    /**
     * subAppend 方法用于实际执行日志的插入操作。它从传入的 ILoggingEvent 中提取信息，创建 Log 对象，并使用预编译的语句将日志信息插入数据库
     *
     * @param event
     * @param connection
     * @param insertStatement
     * @throws Throwable
     */
    @Override
    protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement) throws Throwable {
        // 解析出自定义日志类型，以及存放日志类型
        SystemLog customLog = new SystemLog(event);
        Log log = new Log();
        log.setLogId(0);
        log.setLogType(customLog.getLogType());
        log.setLogLevel(event.getLevel().toString());
        log.setLogMsg(customLog.getLogMessage());
        // 创建一个时区为 GMT+8 的时区偏移量
        ZoneOffset offset = ZoneOffset.ofHours(8);

        // 获取当前时间，并转换为 GMT+8 时区
        LocalDateTime localDateTime = LocalDateTime.now();
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, offset);

        // 将 OffsetDateTime 转换为数据库所需的格式
        Timestamp timestamp = Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());

        log.setLogCreateTime(timestamp.toLocalDateTime());
        if (log.getLogType() == LogConstant.LOG_SYSTEM_NORMAL || !(log.getLogLevel() == LogConstant.ERROR)) {
            log.setLogSite(event.getCallerData()[1].toString().split("\\(")[1]);
            log.setLogClass(event.getCallerData()[1].toString().split("\\(")[0]);
        } else {
            log.setLogClass(customLog.getLogClass());
            log.setLogSite(customLog.getLogSite());
        }
        log.setLogRead(0);


//        System.out.println(log);

        // 预编译语句装载
        insertStatement.setInt(LOG_TYPE, log.getLogType());
        insertStatement.setString(LOG_LEVEL, log.getLogLevel());
        insertStatement.setString(LOG_MSG, log.getLogMsg());
        insertStatement.setString(LOG_SITE, log.getLogSite());
        insertStatement.setString(LOG_CLASS, log.getLogClass());
        insertStatement.setInt(LOG_READ, log.getLogRead());


        // 执行插入日志语句
        int updateCount = insertStatement.executeUpdate();
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
    }

    /**
     * secondarySubAppend 方法用于在插入日志后执行其他操作，比如获取生成的主键
     *
     * @param event
     * @param connection
     * @param eventId
     */
    @Override
    protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) {

    }

    /**
     * getGeneratedKeysMethod 方法用于获取生成的主键
     *
     * @return
     */
    @Override
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    /**
     * getInsertSQL 方法用于获取插入语句
     *
     * @return
     */
    @Override
    protected String getInsertSQL() {
        return INSERT_SQL;
    }
}
