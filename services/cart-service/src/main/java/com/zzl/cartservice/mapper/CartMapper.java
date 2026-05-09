package com.zzl.cartservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.cartservice.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
