package com.zzl.stockservice.config;

import com.zzl.commonapi.dto.logservicedto.LogRequest;
import com.zzl.commonapi.feign.logservicefeign.LogFeignClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WebLogAspect {

    private final LogFeignClient logFeignClient;
    @Value("${spring.application.name:unknown}")
    private String serviceName;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logFeignClient == null) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        LogRequest logRequest = new LogRequest();
        logRequest.setLogType("API_CALL");
        logRequest.setServiceName(serviceName);
        logRequest.setDurationMs((int) duration);
        logRequest.setStatusCode(200);

        if (request != null) {
            logRequest.setTraceId(request.getHeader("X-Trace-Id"));
            logRequest.setUsername(request.getHeader("X-User-Name"));
            logRequest.setUserId(parseLongHeader(request.getHeader("X-User-Id")));
            logRequest.setOperation(request.getMethod() + " " + request.getRequestURI());
            logRequest.setRequestUrl(request.getRequestURI());
            logRequest.setHttpMethod(request.getMethod());
            logRequest.setIpAddress(getClientIp(request));
            logRequest.setUserAgent(request.getHeader("User-Agent"));
        } else {
            logRequest.setOperation(joinPoint.getSignature().toShortString());
        }

        CompletableFuture.runAsync(() -> {
            try {
                logFeignClient.addLog(logRequest);
            } catch (Exception e) {
                log.error("记录API日志失败", e);
            }
        });

        return result;
    }

    private Long parseLongHeader(String header) {
        if (header == null) return null;
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}