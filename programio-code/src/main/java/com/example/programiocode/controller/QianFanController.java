package com.example.programiocode.controller;

import com.example.programiocode.service.AiCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QianFanController {

    @Autowired
    private AiCodeService aiCodeService;


    @PostMapping("/ai/sendMsg")
    public ResponseEntity<String> sendMsg(@RequestBody String problem) {
        String resultData = null;
        resultData = aiCodeService.chat(problem);
        return ResponseEntity.ok(resultData);
    }
}