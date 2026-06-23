package com.zzl.logservice.converter;

import com.zzl.commonapi.dto.logservicedto.LogRequest;
import com.zzl.logservice.dto.response.LogResponse;
import com.zzl.logservice.entity.LogRecord;
import org.springframework.beans.BeanUtils;

public class LogConverter {


    public static LogRecord toEntity(LogRequest request) {
        LogRecord record = new LogRecord();
        BeanUtils.copyProperties(request, record);
        return record;
    }
    public static LogResponse toResponse(LogRecord record) {
        LogResponse response = new LogResponse();
        BeanUtils.copyProperties(record, response);
        return response;
    }
}