package com.zzl.logservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zzl.commonapi.dto.logservicedto.LogRequest;
import com.zzl.commoncore.result.Result;
import com.zzl.logservice.entity.LogRecord;
import com.zzl.logservice.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Validated
public class LogController {

    private final LogService logService;

    @PostMapping
    public Result<Void> addLog(@Valid @RequestBody LogRequest request) {
        logService.saveLog(request);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<IPage<LogRecord>> pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        IPage<LogRecord> page = logService.pageQuery(pageNum, pageSize, logType, userId, operation, startTime, endTime);
        return Result.success(page);
    }
}