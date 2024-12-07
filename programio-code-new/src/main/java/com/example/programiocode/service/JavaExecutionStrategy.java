package com.example.programiocode.service;


import com.example.programiocode.utils.ExecutionStrategy;
import com.example.programiocode.utils.ProcessUtils;
import com.example.programiocommon.pojo.to.CodeRespondTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class JavaExecutionStrategy implements ExecutionStrategy {
    private static final long MAX_MEMORY_LIMIT = 50;  // 最大内存限制为 50MB
    private static final long TIMEOUT = 5;  // 超时时间为 5 秒

    @Value("${codeOutputPath}")
    private String codeOutputPath;

    private CodeRespondTO codeRespondTO = new CodeRespondTO(); // 返回值对象

    @Override
    public CodeRespondTO executeCode(String language,String javaCode, String userInput) {
        codeRespondTO.setLanguage(language);

        Process process = null;
        String tempFileName = null;
        String classFileName = null;
        long startTime;  // 记录程序启动时间
        String uuid = null;

        try {
            if (javaCode == null || javaCode.trim().isEmpty()) {
                codeRespondTO.setIsCorrect("error");
                codeRespondTO.setCodeOutput("代码为空");
                return codeRespondTO;
            }

            // 使用 UUID 生成文件名，保证唯一性
            uuid = UUID.randomUUID().toString().replace("-", "_");
            tempFileName = "Solution.java";  // 类名保持为Solution

            // 创建以 UUID 为名称的子目录
            Path targetDir = Paths.get(codeOutputPath, uuid, "Solution");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);  // 如果目标文件夹不存在，则创建
//                System.out.println("目标文件夹创建成功: " + targetDir.toString());
            }

            // 创建临时文件路径，确保路径有效
            Path tempFilePath = targetDir.resolve(tempFileName);  // 将 Solution.java 放入指定目录
//            System.out.println("临时文件路径: " + tempFilePath.toString());  // 调试信息，打印路径


            // 创建临时的 Java 文件，保存用户上传的代码
            Files.write(tempFilePath, javaCode.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
//            System.out.println("Java 文件已创建: " + tempFilePath);

            // 使用 javac 编译 Java 文件
//            System.out.println("编译文件: " + tempFilePath);
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", tempFilePath.toString());
            process = compileProcessBuilder.start();
            process.waitFor();  // 等待编译完成

            // 编译失败的处理
            if (process.exitValue() != 0) {
//                System.out.println(process.exitValue());
                codeRespondTO.setIsCorrect("error");
                codeRespondTO.setCodeOutput("Java 编译失败");
                return codeRespondTO;
            }

            // 获取编译后的 class 文件名
            classFileName = tempFilePath.toString().replace(".java", ".class");
//            System.out.println("编译后的 class 文件: " + classFileName);

            // 执行 Java 程序
            String command = "java -cp " + targetDir.toString() + " Solution";  // 运行 Solution 类
//            System.out.println("执行命令: " + command);

            startTime = System.currentTimeMillis();  // 记录程序启动时间
            ProcessBuilder executeProcessBuilder = new ProcessBuilder(command.split(" "));
            executeProcessBuilder.redirectErrorStream(false);  // 不合并合并标准输出和错误输出

            process = executeProcessBuilder.start();

            // 获取进程的标准输出和错误输出流
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "UTF-8");
            InputStreamReader errorStreamReader = new InputStreamReader(process.getErrorStream(), "UTF-8");


            // 使用BufferedReader一次性读取所有输出
            BufferedReader reader = new BufferedReader(inputStreamReader);
            BufferedReader errorReader = new BufferedReader(errorStreamReader);

            // 读取标准输出
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            String line;


            if (userInput != null) {
                // 创建 OutputStreamWriter 对象，向标准输入流写入用户输入
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
                // 将用户输入写入进程的标准输入流
                outputStreamWriter.write(userInput);
                outputStreamWriter.flush();  // 确保输入传递给 Python 脚本
                outputStreamWriter.close();
            }

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // 读取错误输出
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // 计算已运行时间
            long elapsedTime = (System.currentTimeMillis() - startTime);  // 当前时间 - 启动时间
            long currentMemoryUsage = 0L;

            codeRespondTO.setTime(elapsedTime + "ms");  // 更新运行时间

            // 获取进程 ID 和内存使用
            Long pid = ProcessUtils.getProcessId();  // 获取进程的 PID
            Long memoryUsage = ProcessUtils.getMemoryUsage(pid);  // 获取当前内存使用

            currentMemoryUsage = Math.max(currentMemoryUsage, memoryUsage);  // 更新最大内存使用量
            codeRespondTO.setMemory(memoryUsage + "KB");  // 更新内存使用信息

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
                codeRespondTO.setErrorOutput(errorOutput.toString());
                codeRespondTO.setIsCorrect("error");
            } else {
                codeRespondTO.setCodeOutput(output.toString());
                codeRespondTO.setIsCorrect("correct");
            }

        } catch (IOException | InterruptedException e) {
            codeRespondTO.setIsCorrect("error");
            codeRespondTO.setCodeOutput("执行异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();  // 确保进程被销毁
            }// 删除 UUID 对应的文件夹及其中所有文件
            if (uuid != null) {
                try {
                    // 创建目标文件夹路径
                    Path targetDir = Paths.get(codeOutputPath, uuid);

                    // 如果文件夹存在，直接删除该文件夹及其所有内容
                    if (Files.exists(targetDir)) {
                        // 删除文件夹及其中所有文件
                        Files.walk(targetDir)
                                .sorted((path1, path2) -> path2.compareTo(path1))  // 先删除子文件，再删除父目录
                                .forEach(path -> {
                                    try {
                                        Files.deleteIfExists(path);  // 删除文件或文件夹
                                    } catch (IOException e) {
                                        e.printStackTrace();  // 打印删除文件时的异常信息
                                    }
                                });
                    }
                } catch (IOException e) {
                    e.printStackTrace();  // 打印删除文件时的异常信息
                }
            }
        }
        return codeRespondTO;
    }
}

