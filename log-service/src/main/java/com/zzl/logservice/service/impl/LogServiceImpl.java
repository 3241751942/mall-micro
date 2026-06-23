package com.zzl.logservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.commonapi.dto.logservicedto.LogRequest;
import com.zzl.logservice.converter.LogConverter;
import com.zzl.logservice.entity.LogRecord;
import com.zzl.logservice.mapper.LogRecordMapper;
import com.zzl.logservice.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl extends ServiceImpl<LogRecordMapper, LogRecord> implements LogService {

    @Async("logExecutor")
    @Override
    public void saveLog(LogRequest request) {
        try {
            LogRecord record = LogConverter.toEntity(request);
            save(record);
        } catch (Exception e) {
            // 异步日志记录失败不能影响主业务，只打印错误
            log.error("保存日志失败: {}", e.getMessage());
        }
    }

    @Override
    public IPage<LogRecord> pageQuery(Integer pageNum, Integer pageSize, String logType, Long userId,
                                      String operation, LocalDateTime startTime, LocalDateTime endTime) {
        Page<LogRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LogRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(logType), LogRecord::getLogType, logType);
        wrapper.eq(userId != null, LogRecord::getUserId, userId);
        wrapper.like(StringUtils.hasText(operation), LogRecord::getOperation, operation);
        wrapper.ge(startTime != null, LogRecord::getCreateTime, startTime);
        wrapper.le(endTime != null, LogRecord::getCreateTime, endTime);
        wrapper.orderByDesc(LogRecord::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }
}