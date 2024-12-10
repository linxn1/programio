package com.example.programiocode.controller;

import com.example.programiocode.service.CodeExecuteService;
import com.example.programiocommon.pojo.to.CodeRequestTO;
import com.example.programiocommon.pojo.to.CodeRespondTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/code")
@Api(tags = "代码提交")
public class CodeExecutionController {

    @Autowired
    private CodeExecuteService codeExecuteService;

    @PostMapping("/execute")
    public CompletableFuture<ResponseEntity<CodeRespondTO>> executeCode(@RequestBody CodeRequestTO codeRequestTO) {
        String userId = codeRequestTO.getUserId();
        Integer questionId = codeRequestTO.getQuestionId();
        String language = codeRequestTO.getLanguage();
        String code = codeRequestTO.getCode();
        String userInput = codeRequestTO.getUserInput();
        Boolean aiDebug = codeRequestTO.getAiDebug();

        // 异步执行代码并处理结果
        CompletableFuture<CodeRespondTO> future = codeExecuteService.executeCode(language, code, userInput, aiDebug);

        return future.thenApply(result -> ResponseEntity.ok(result))  // 成功时返回 ResponseEntity 包装的 CodeRespondTO
                .exceptionally(ex -> {
                    // 如果发生异常，返回内部服务器错误并将异常信息返回
                    CodeRespondTO errorResponse = new CodeRespondTO();
                    errorResponse.setIsCorrect("error");
                    errorResponse.setCodeOutput("Error executing code: " + ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PostMapping("/submit")
    public ResponseEntity<CodeRespondTO> submitCode(@RequestBody CodeRequestTO codeRequestTO) {
        String userId = codeRequestTO.getUserId();
        Integer questionId = codeRequestTO.getQuestionId();
        String language = codeRequestTO.getLanguage();
        String code = codeRequestTO.getCode();
        String userInput = codeRequestTO.getUserInput();
        codeRequestTO.setAiDebug(Boolean.FALSE);
        // 异步执行代码并处理结果
        CompletableFuture<CodeRespondTO> future = codeExecuteService.submitCode(language, code, userInput, questionId);

        // 等待异步结果并返回 ResponseEntity
        return future
                .thenApply(result -> ResponseEntity.ok(result))  // 成功时返回 ResponseEntity 包装的 CodeRespondTO
                .exceptionally(ex -> {
                    // 如果发生异常，返回内部服务器错误并将异常信息返回
                    CodeRespondTO errorResponse = new CodeRespondTO();
                    errorResponse.setIsCorrect("error");
                    errorResponse.setCodeOutput("Error executing code: " + ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                })
                .join();  // 阻塞直到异步任务完成并返回最终结果
    }
}
