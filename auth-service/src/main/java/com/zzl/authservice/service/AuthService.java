package com.zzl.authservice.service;

import com.zzl.authservice.dto.request.LoginRequest;
import com.zzl.authservice.dto.response.LoginResponse;
import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    TokenVerifyResponse verifyToken(String token);
}