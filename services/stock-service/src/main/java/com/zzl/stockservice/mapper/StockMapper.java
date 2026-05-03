package com.zzl.stockservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.stockservice.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}
