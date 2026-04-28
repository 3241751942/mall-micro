package com.zzl.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface  UserMapper extends BaseMapper<User> {
}
