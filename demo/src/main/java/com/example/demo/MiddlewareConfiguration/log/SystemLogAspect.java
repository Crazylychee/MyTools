package com.example.demo.MiddlewareConfiguration.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yc
 * @date 2023-12-04 22:10
 */

/**
 * 日志管理
 */
@Aspect
@Component

public class SystemLogAspect {

    private static final ThreadLocal<Date> beginTimeThreadLocal = new NamedThreadLocal<Date>("ThreadLocal beginTime");
    private static final Logger log = LoggerFactory.getLogger(SystemLogAspect.class);


    @Autowired(required = false)
    private HttpServletRequest request;

    private String description;

    private String type;


    /**
     * Controller层切点,注解方式
     */
    @Pointcut("@annotation(com.qgstudio.newer.log.GetLog)")
    public void controllerAspect() {
    }

    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* com.qgstudio.newer.controller..*.*(..))")
    public void operExceptionLogPoinCut() {
    }


    /**
     * 前置通知 (在方法执行之前返回)用于拦截Controller层记录用户的操作的开始时间
     *
     * @param joinPoint 切点
     * @throws InterruptedException
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {

        //线程绑定变量（该数据只有当前请求的线程可见）
        Date beginTime = new Date();
        beginTimeThreadLocal.set(beginTime);
    }

    /**
     * 后置通知(在方法执行之后并返回数据) 用于拦截Controller层无异常的操作
     *
     * @param joinPoint 切点
     */
    @AfterReturning(value = "controllerAspect()", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {
        try {
            bindRequest(joinPoint);
            GetLog systemlog = getSystemLog(joinPoint);
            Object logParams = getlogParams(joinPoint, systemlog);
            String logtype = getLogtype();
            //如果是获取不到参数
            if (logParams == null) {
                logParams = "无参数";
            }

            //请求开始时间
            long beginTime = beginTimeThreadLocal.get().getTime();
            long endTime = System.currentTimeMillis();
            //请求耗时
            Long logElapsedTime = endTime - beginTime;
            String msg = SystemLog.buildLogMsg(logtype,
                    description,
                    type,
                    request.getRequestURI(),
                    request.getMethod(),
                    logParams.toString(),
                    logElapsedTime.intValue(),
                    result);
            log.info(msg);
            beginTimeThreadLocal.remove();
        } catch (Exception e) {
            log.error("AOP后置通知异常", e);
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static Map<String, Object> getControllerMethodInfo(JoinPoint joinPoint) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>(16);
        //获取目标类名
        String targetName = joinPoint.getTarget().getClass().getName();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        //获取相关参数
        Object[] arguments = joinPoint.getArgs();
        //生成类对象
        Class targetClass = Class.forName(targetName);
        //获取该类中的方法
        Method[] methods = targetClass.getMethods();

        String description = "";
        String type;

        for (Method method : methods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            Class[] clazzs = method.getParameterTypes();
            if (clazzs.length != arguments.length) {
                //比较方法中参数个数与从切点中获取的参数个数是否相同，原因是方法可以重载哦
                continue;
            }
            try {
                description = method.getAnnotation(GetLog.class).description();
                type = method.getAnnotation(GetLog.class).type();
            }catch (Exception e) {
                description = "未知";
                type = "未知";
            }
            map.put("description", description);
            map.put("type", type);
        }
        return map;
    }


    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "operExceptionLogPoinCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        try {
            bindRequest(joinPoint);
            GetLog systemlog = getSystemLog(joinPoint);
            Object logParams = getlogParams(joinPoint, systemlog);
            String logtype = getLogtype();
            //如果是获取不到参数
            if (logParams == null) {
                logParams = "无参数";
            }

            String msg = SystemLog.buildErrLogMsg(logtype,
                    description,
                    type,
                    request.getRequestURI(),
                    request.getMethod(),
                    logParams.toString(),
                    (Exception) e);

            //把日志打出来
            log.error(msg);
            beginTimeThreadLocal.remove();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    private Object getlogParams(JoinPoint joinPoint, GetLog systemlog) {
        Object logParams;
        try {
            logParams=  AnnotationResolver.newInstance().resolver(joinPoint, systemlog.requestParam());
        }catch (Exception e) {
            return "无参数";
        }
        return logParams;
    }

    private String getLogtype() {
        String uri = request.getRequestURI();

        // 判断 URI 是否以 "/newer" 开头
        if (uri.startsWith("/newer")) {
            return LogConstant.NEWER;
        } else {
            return LogConstant.OPERATION_LOG;
        }

    }


    public GetLog getSystemLog(JoinPoint joinPoint) {

        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        GetLog systemLog = (GetLog) method.getAnnotation(GetLog.class);
        return systemLog;
    }

    public void bindRequest(JoinPoint joinPoint) throws Exception {
        description = getControllerMethodInfo(joinPoint).get("description").toString();
        type = getControllerMethodInfo(joinPoint).get("type").toString();
    }

}