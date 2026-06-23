package com.zzl.logservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.logservice.entity.LogRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogRecordMapper extends BaseMapper<LogRecord> {
}