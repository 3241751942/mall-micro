package com.zzl.authservice.service.impl;

import com.zzl.authservice.dto.request.LoginRequest;
import com.zzl.authservice.dto.response.LoginResponse;
import com.zzl.authservice.service.AuthService;
import com.zzl.authservice.utils.JwtUtil;
import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import com.zzl.commonapi.dto.userservicedto.UserValidateRequest;
import com.zzl.commonapi.dto.userservicedto.UserValidateResponse;
import com.zzl.commonapi.feign.userservicefeign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserFeignClient userFeignClient;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 调用 user-service 验证账号密码
        UserValidateRequest validateRequest = new UserValidateRequest();
        validateRequest.setAccount(request.getAccount());
        validateRequest.setPassword(request.getPassword());
        UserValidateResponse user = userFeignClient.validate(validateRequest);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            log.warn("登录失败：账号或密码错误，account={}", request.getAccount());
            throw new RuntimeException("账号或密码错误");
        }

        // 2. 生成 JWT（用户角色可扩展，暂用默认 role）
        String roles = "ROLE_USER";   // 后续可从 user-service 获取真实角色
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        // 3. 返回结果
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setStatus(user.getStatus());
        return response;
    }

    @Override
    public TokenVerifyResponse verifyToken(String token) {
        TokenVerifyResponse response = new TokenVerifyResponse();
        boolean valid = jwtUtil.validateToken(token);
        response.setValid(valid);
        if (valid) {
            response.setUserId(jwtUtil.getUserIdFromToken(token));
            // 可选：从 token 中解析用户名和角色
        }
        return response;
    }
}