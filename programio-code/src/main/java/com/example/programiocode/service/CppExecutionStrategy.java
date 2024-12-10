package com.example.programiocode.service;

import com.example.programiocode.utils.ExecutionStrategy;
import com.example.programiocode.utils.ProcessUtils;
import com.example.programiocommon.pojo.to.CodeRespondTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class CppExecutionStrategy implements ExecutionStrategy {
    private static final long MAX_MEMORY_LIMIT = 5000;  // 最大内存限制为 50MB
    private static final long TIMEOUT = 5;  // 超时时间为 5 秒

    //    @Value("${app.code-output-path}")
//    private String codeOutputPath;
    private String codeOutputPath = "src/main/resources/tempfiles";

    @Autowired
    private AiCodeService aiCodeService;


    private CodeRespondTO codeRespondTO = new CodeRespondTO(); // 需要返回的值

    @Override
    public CodeRespondTO executeCode(String language, String cppCode, String userInput, Boolean aiDebug) {
        codeRespondTO.setLanguage(language);

        Process process = null;
        String tempFileName = null;
        String uuid = UUID.randomUUID().toString().replace("-", "_"); // 使用 UUID 保证唯一性
        Path targetDir = Paths.get(codeOutputPath, uuid);  // 使用 UUID 创建文件夹

        try {
            // 创建目标文件夹
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 创建 C++ 文件并保存用户上传的代码
            tempFileName = uuid + ".cpp";
            Path cppFilePath = targetDir.resolve(tempFileName);
            Files.write(cppFilePath, cppCode.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
//            System.out.println("临时 C++ 文件已创建: " + cppFilePath);

            // 获取操作系统类型
            String os = System.getProperty("os.name").toLowerCase();
            String outputFileName;

            // 根据操作系统设置输出文件名
            if (os.contains("win")) {
                outputFileName = cppFilePath.toString().replace(".cpp", ".exe");
            } else {
                outputFileName = cppFilePath.toString().replace(".cpp", "");
            }

            // 使用 g++ 编译 C++ 代码
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("g++", cppFilePath.toString(), "-o", outputFileName);
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();  // 等待编译完成

            // 编译失败的处理
            if (compileProcess.exitValue() != 0) {
                codeRespondTO.setIsCorrect("error");
                codeRespondTO.setCodeOutput("C++ 编译失败");
                return codeRespondTO;
            }

            // 编译成功后执行生成的可执行文件
            ProcessBuilder executeProcessBuilder = new ProcessBuilder(outputFileName);
            executeProcessBuilder.redirectErrorStream(false);  // 不合并错误流


            // 获取进程 ID 和内存使用
            Long pid = ProcessUtils.getProcessId();  // 获取进程的 PID
            Long memoryUsage = ProcessUtils.getMemoryUsage(pid);  // 获取当前内存使用
            process = executeProcessBuilder.start();

            // 获取进程的标准输出和错误输出流
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            InputStreamReader errorStreamReader = new InputStreamReader(process.getErrorStream());

            // 使用BufferedReader一次性读取所有输出
            BufferedReader reader = new BufferedReader(inputStreamReader);
            BufferedReader errorReader = new BufferedReader(errorStreamReader);

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


            currentMemoryUsage = Math.max(currentMemoryUsage, memoryUsage);  // 更新最大内存使用量
            codeRespondTO.setMemory(currentMemoryUsage / 1024 + "mb");  // 更新内存使用信息

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


        } catch (IOException | InterruptedException e) {
            codeRespondTO.setIsCorrect("error");
            codeRespondTO.setCodeOutput("执行异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 删除临时文件和文件夹
            try {
                // 删除 C++ 源文件
                Files.deleteIfExists(Paths.get(targetDir.toString(), tempFileName));

                // 删除编译后的可执行文件（Windows 和类 Unix 系统处理不同）
                String outputFileName = tempFileName.replace(".cpp", (System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : ""));
                Files.deleteIfExists(Paths.get(targetDir.toString(), outputFileName));

                // 删除整个 UUID 目录
                Files.deleteIfExists(targetDir);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (aiDebug) {
            String problem = aiCodeService.chat(codeRespondTO.getErrorOutput());
            codeRespondTO.setAiString(problem);
        }
        return codeRespondTO;
    }
}
