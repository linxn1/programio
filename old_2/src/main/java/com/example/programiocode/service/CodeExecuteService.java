package com.example.programiocode.service;

import com.example.programiocode.mapper.question.QuestionAnswerMapper;
import com.example.programiocode.utils.ExecutionStrategy;
import com.example.programiocode.utils.IOTypeConverter;
import com.example.programiocode.utils.ProcessUtils;
import com.example.programiocommon.pojo.dto.QuestionAnswerDTO;
import com.example.programiocommon.pojo.to.CodeRespondTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CodeExecuteService {
    @Autowired
    @Qualifier("testTaskExecutor")
    private ThreadPoolTaskExecutor testTaskExecutor;  // 注入线程池

    @Autowired
    @Qualifier("submitTaskExecutor")
    private ThreadPoolTaskExecutor submitTaskExecutor;  // 注入线程池

    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;


    // 执行用户请求并返回结果
    public CompletableFuture<CodeRespondTO> executeCode(String language, String code, String userInput) {
        return CompletableFuture.supplyAsync(() -> {
            // 根据语言选择执行策略
            ExecutionStrategy strategy = getExecutionStrategy(language);
            CodeRespondTO codeRespondTO = strategy.executeCode(language, code, userInput);

            return codeRespondTO;
        }, testTaskExecutor);
    }

    // 执行用户提交并返回结果
    public CompletableFuture<CodeRespondTO> submitCode(String language, String code, String userInput, Integer questionId) {
        return CompletableFuture.supplyAsync(() -> {
            // 根据语言选择执行策略
            ExecutionStrategy strategy = getExecutionStrategy(language);
            CodeRespondTO codeRespondTO = strategy.executeCode(language, code, userInput);

            // 获取题目答案并计算响应结果
            CodeRespondTO codeRespondTOForAll = new CodeExecutionResultProcessor(questionId, language, codeRespondTO, code).process();

            return codeRespondTOForAll;
        }, submitTaskExecutor);
    }

    // 根据语言选择执行策略
    private ExecutionStrategy getExecutionStrategy(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return new PythonExecutionStrategy();
            case "java":
                return new JavaExecutionStrategy();
            case "c++":
                return new CppExecutionStrategy();
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    // 私有类：处理代码执行的结果，计算总的返回对象
    private class CodeExecutionResultProcessor {

        private Integer questionId;
        private String language;
        private CodeRespondTO codeRespondTO;
        private String code;

        public CodeExecutionResultProcessor(Integer questionId, String language, CodeRespondTO codeRespondTO, String code) {
            this.questionId = questionId;
            this.language = language;
            this.codeRespondTO = codeRespondTO;
            this.code = code;
        }

        public CodeRespondTO process() {
            List<QuestionAnswerDTO> answerList = questionAnswerMapper.getQuestionAnswerByQuestionId(questionId);

            // 初始化结果
            String isCorrect = "correct";
            String time = codeRespondTO.getTime();
            String memory = codeRespondTO.getMemory();
            String codeOutput = "";
            String errorOutput = "";
            String isAccepted = "accept";
            int correctAnswersCount = 0;

            Integer currentTime = 0;
            Integer currentMemory = 0;
            // 遍历答案，检查是否符合预期
            for (QuestionAnswerDTO answer : answerList) {
                boolean isAnswerCorrect = ProcessUtils.isAnswerCorrect(codeRespondTO.getCodeOutput(), answer.getAnswer());
                currentTime = Math.max(IOTypeConverter.timeConvertToInteger(codeRespondTO.getTime()), currentTime);
                currentMemory = Math.max(IOTypeConverter.memoryConvertToInteger(codeRespondTO.getMemory()), currentMemory);
//                System.out.println(isAnswerCorrect);
                if (!isAnswerCorrect) {
                    isCorrect = "error";  // 如果有一个答案不正确，设置为 "error"
                    isAccepted = "warning"; // 如果有任何错误，设置为 "warning"
                } else {
                    correctAnswersCount++;
                }
            }

            // 计算准确率
            float accuracyRate = calculateAccuracy(correctAnswersCount, answerList.size());

            // 设置返回结果
            CodeRespondTO codeRespondTOForAll = new CodeRespondTO();
            codeRespondTOForAll.setIsCorrect(isCorrect);
            codeRespondTOForAll.setTime(currentTime.toString() + "ms");
            codeRespondTOForAll.setMemory(currentMemory.toString() + "kb");
            codeRespondTOForAll.setCodeOutput(codeOutput);
            codeRespondTOForAll.setErrorOutput(errorOutput);
            codeRespondTOForAll.setLanguage(language);
            codeRespondTOForAll.setIsAccepted(isAccepted);
            codeRespondTOForAll.setAccuracyRate(String.format("%.2f", accuracyRate) + "%");

            return codeRespondTOForAll;
        }


        // 计算准确率
        private float calculateAccuracy(int correctAnswers, int totalAnswers) {
            if (totalAnswers == 0) {
                return 0.0f;
            }
            return (float) correctAnswers / totalAnswers * 100;  // 返回百分比准确率
        }
    }
}
