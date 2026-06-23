package com.zzl.logservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zzl.commonapi.dto.logservicedto.LogRequest;
import com.zzl.logservice.entity.LogRecord;

import java.time.LocalDateTime;

public interface LogService {
    void saveLog(LogRequest request);

    IPage<LogRecord> pageQuery(Integer pageNum, Integer pageSize, String logType, Long userId, String operation,
                               LocalDateTime startTime, LocalDateTime endTime);
}