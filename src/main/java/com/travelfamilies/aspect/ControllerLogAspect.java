package com.travelfamilies.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Controller 层统一日志切面
 * <p>
 * 自动记录每个请求的：HTTP方法、URI、调用方法、入参、响应摘要、耗时、异常堆栈。
 * 日志级别：正常请求 → info，异常 → error。
 * <p>
 * 可通过修改 {@link #MAX_PARAM_LENGTH} 和 {@link #MAX_RESP_LENGTH} 控制日志截断长度。
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    /** 入参日志最大长度，超出截断 */
    private static final int MAX_PARAM_LENGTH = 500;
    /** 响应日志最大长度，超出截断 */
    private static final int MAX_RESP_LENGTH = 1000;

    /** 敏感字段关键词，日志中脱敏为 *** */
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "password", "passwd", "secret", "token", "idCard", "id_card",
            "accessKey", "accesskey", "authorization", "creditCard"
    );

    @Pointcut("execution(* com.travelfamilies.controller..*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String method = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();

        // ---------- 请求日志 ----------
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String params = buildParams(joinPoint.getArgs());
            log.info("[REQ] {} {} | {} | params: {}",
                    request.getMethod(), request.getRequestURI(), method, params);
        } else {
            log.info("[REQ] {} | params: {}", method, buildParams(joinPoint.getArgs()));
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            // ---------- 响应日志 ----------
            String resp = truncate(String.valueOf(result), MAX_RESP_LENGTH);
            log.info("[RESP] {} | {}ms | result: {}", method, elapsed, resp);

            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            // ---------- 异常日志 ----------
            log.error("[ERROR] {} | {}ms | {}: {}", method, elapsed, e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 构建参数摘要，过滤掉 HttpServletRequest / HttpServletResponse / MultipartFile 等非业务参数，
     * 并对敏感字段脱敏
     */
    private String buildParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Object arg : args) {
            // 跳过 servlet 原生对象和文件
            if (arg instanceof HttpServletRequest
                    || arg instanceof jakarta.servlet.http.HttpServletResponse
                    || arg instanceof MultipartFile) {
                continue;
            }
            if (sb.length() > 1) {
                sb.append(", ");
            }
            String str = String.valueOf(arg);
            str = maskSensitive(str);
            sb.append(truncate(str, MAX_PARAM_LENGTH));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 对包含敏感关键词的字符串进行脱敏：将值替换为 ***
     * 例如 password=abc123 → password=***
     */
    private String maskSensitive(String str) {
        if (str == null) return "null";
        String lower = str.toLowerCase();
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase())) {
                // 匹配 "keyword=xxx" 或 "keyword: xxx" 模式，替换值为 ***
                str = str.replaceAll(
                        "(?i)(" + keyword + "\\s*[=:]\\s*)[^,}\\]]*",
                        "$1***"
                );
            }
        }
        return str;
    }

    private String truncate(String str, int maxLen) {
        if (str == null) {
            return "null";
        }
        return str.length() > maxLen ? str.substring(0, maxLen) + "...(truncated)" : str;
    }
}
