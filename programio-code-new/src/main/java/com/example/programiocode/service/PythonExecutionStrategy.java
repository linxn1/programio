package com.example.programiocode.service;

import com.example.programiocode.utils.ExecutionStrategy;
import com.example.programiocode.utils.ProcessUtils;
import com.example.programiocommon.pojo.to.CodeRespondTO;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class PythonExecutionStrategy implements ExecutionStrategy {
    private static final long MAX_MEMORY_LIMIT = 5;  // 设置最大内存限制为 50MB
    private static final long TIMEOUT = 5;  // 设置超时时间为 5 秒

    private CodeRespondTO codeRespondTO = new CodeRespondTO(); // 需要返回的值

    @Override
    public CodeRespondTO executeCode(String language,String pythonScript, String userInput) {
        codeRespondTO.setLanguage(language);

        Process process = null;
        try {
            // 创建执行 Python 脚本的进程
            ProcessBuilder processBuilder = new ProcessBuilder("python", "-c", pythonScript);
            processBuilder.redirectErrorStream(false);

            process = processBuilder.start();

            // 获取进程的标准输出和错误输出流
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "UTF-8");
            InputStreamReader errorStreamReader = new InputStreamReader(process.getErrorStream(), "UTF-8");

            // 使用BufferedReader一次性读取所有输出
            BufferedReader reader = new BufferedReader(inputStreamReader);
            BufferedReader errorReader = new BufferedReader(errorStreamReader);

            // 读取标准输出和错误输出到StringBuilder
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();


            long startTime = System.currentTimeMillis();  // 记录开始时间
            long currentMemoryUsage = 0L;
            String line;

            if (userInput != null) {
                // 创建 OutputStreamWriter 对象，向标准输入流写入用户输入
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
                // 将用户输入写入进程的标准输入流
                outputStreamWriter.write(userInput);
                outputStreamWriter.flush();  // 确保输入传递给 Python 脚本
                outputStreamWriter.close();
            }


            // 读取标准输出
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 读取错误输出
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // 计算已运行时间
            long elapsedTime = (System.currentTimeMillis() - startTime);  // 当前时间 - 启动时间
            codeRespondTO.setTime(elapsedTime + "ms");  // 更新运行时间

            // 获取进程 ID 和内存使用
            Long pid = ProcessUtils.getProcessId();  // 获取进程的 PID
            Long memoryUsage = ProcessUtils.getMemoryUsage(pid);  // 获取当前内存使用
            currentMemoryUsage = Math.max(currentMemoryUsage, memoryUsage);  // 更新最大内存使用量
            codeRespondTO.setMemory(currentMemoryUsage + "kb");  // 更新内存使用信息

            // 检查是否超过内存限制
            if (memoryUsage > MAX_MEMORY_LIMIT * 1024) {
                process.destroy();
                codeRespondTO.setCodeOutput("超出内存限制");
                codeRespondTO.setIsCorrect("error");
                return codeRespondTO;  // 超出内存限制后返回
            }

            // 检查是否超时
            if (elapsedTime >= TIMEOUT * 1000) {
                process.destroy();
                codeRespondTO.setCodeOutput("超出时间限制");
                codeRespondTO.setIsCorrect("error");
                return codeRespondTO;  // 超出时间限制后返回
            }

            // 获取标准输出和标准错误输出
            String outputStr = output.toString();
            String errorStr = errorOutput.toString();

            // 设置标准输出和错误输出
            codeRespondTO.setCodeOutput(outputStr);  // 设置执行结果
            codeRespondTO.setErrorOutput(errorStr);

            // 检查退出码
            int exitCode = process.exitValue();  // 获取进程退出码
            if (exitCode != 0) {
                // 如果退出码非0，表示程序有错误
                codeRespondTO.setErrorOutput(errorOutput.toString());
                codeRespondTO.setIsCorrect("error");
            } else {
                // 正常执行
                codeRespondTO.setCodeOutput(output.toString());
                codeRespondTO.setIsCorrect("correct");
            }


        } catch (IOException e) {
            codeRespondTO.setIsCorrect("error");
            codeRespondTO.setCodeOutput("执行异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();  // 确保进程被销毁
            }
        }
        return codeRespondTO;
    }
}

