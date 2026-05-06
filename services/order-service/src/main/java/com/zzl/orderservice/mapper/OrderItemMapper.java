package com.zzl.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.orderservice.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
