package com.example.programiocode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    // 提供健康检查的接口
    @GetMapping("/healthz")
    public String healthCheck() {
        return "OK";
    }
}
