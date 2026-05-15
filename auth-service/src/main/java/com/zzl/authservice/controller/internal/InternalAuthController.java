package com.zzl.authservice.controller.internal;

import com.zzl.authservice.service.AuthService;
import com.zzl.commonapi.dto.authservicedto.TokenVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;

    /**
     * Token 校验
     */
    @PostMapping("/verify")
    public TokenVerifyResponse verify(@RequestHeader("Authorization") String authorization) {
        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        return authService.verifyToken(token);
    }
}
