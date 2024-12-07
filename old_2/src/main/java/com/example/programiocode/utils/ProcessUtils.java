package com.example.programiocode.utils;

import java.io.*;

public class ProcessUtils {
    // 获取指定 PID 进程的内存使用（单位：KB）
    public static long getMemoryUsage(long pid) {
        String os = System.getProperty("os.name").toLowerCase();
        long memoryUsage = 0;

        try {
            Process process;
            if (os.contains("win")) {
                // Windows 系统使用 tasklist 命令
                process = Runtime.getRuntime().exec("tasklist /FI \"PID eq " + pid + "\" /FO CSV");
            } else {
                // Linux/MacOS 系统使用 ps 命令
                process = Runtime.getRuntime().exec("ps -o rss= -p " + pid);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // 如果是 Windows 系统，跳过表头
            if (os.contains("win")) {
                // 跳过表头
                reader.readLine(); // 第一行是表头
            }

            // 读取每一行数据
            while ((line = reader.readLine()) != null) {
                // 对于 Windows 的 CSV 格式，去除双引号并按逗号分隔
                String[] columns = line.replace("\"", "").split(",");

                // 如果是 Windows，内存字段通常是倒数第二个字段（注意：根据实际情况调整列索引）
                if (os.contains("win") && columns.length > 4) {
                    String memoryStr = columns[4].trim();
                    try {
                        memoryUsage = Long.parseLong(memoryStr);
                    } catch (NumberFormatException e) {
                        System.out.println("无法解析内存使用量: " + memoryStr);
                    }
                }
                // 如果是 Unix-like 系统（Linux/MacOS），内存字段就在第一列
                else if (os.contains("nix") && columns.length > 0) {
                    String memoryStr = columns[0].trim();
                    try {
                        memoryUsage = Long.parseLong(memoryStr);
                    } catch (NumberFormatException e) {
                        System.out.println("无法解析内存使用量: " + memoryStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return memoryUsage;
    }

    // 获取当前进程的 PID
    public static long getProcessId() {
        String os = System.getProperty("os.name").toLowerCase();
        long pid = -1;

        try {
            if (os.contains("win")) {
                // Windows 系统
                pid = getWindowsProcessId();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Linux/macOS 系统
                pid = getUnixProcessId();
            } else {
                System.out.println("Unsupported OS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pid;
    }

    // 获取 Windows 系统的进程 PID
    private static long getWindowsProcessId() throws IOException {
        // 执行 tasklist 命令，筛选出 java.exe 的进程
        Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq java.exe\" /FO CSV");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.replace("\"", "").split(",");
                // 确保行数据正确且包含 PID 信息
                if (columns.length > 1) {
                    try {
                        // CSV 格式中的第二列是 PID，转换为 long 类型
                        return Long.parseLong(columns[1].trim());
                    } catch (NumberFormatException e) {
                        // 捕获转换异常
                        System.out.println("无法解析 PID: " + columns[1]);
                    }
                }
            }
        }

        // 如果没有找到进程，返回 -1
        return -1;
    }


    // 获取 Linux/macOS 系统的进程 PID
    private static long getUnixProcessId() throws IOException {
        // 使用 jps 命令获取当前 Java 进程的 PID
        Process process = Runtime.getRuntime().exec("jps -l");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // jps 输出的每一行是：pid main_class_name
                String[] parts = line.split(" ");
                if (parts.length > 0) {
                    // 获取当前进程的 PID
                    long pid = Long.parseLong(parts[0].trim());
                    return pid;
                }
            }
        }
        return -1;  // 如果未找到进程，返回 -1
    }

    public static boolean isAnswerCorrect(String output, String expectedAnswer) {
        output = IOTypeConverter.normalizeOutput(output);
        expectedAnswer = IOTypeConverter.normalizeOutput(expectedAnswer);
        System.out.println("rouput" + output);
        System.out.println("expect" + expectedAnswer);

        // 去除output和expectedAnswer两端的换行符和空白字符
        String trimmedOutput = output != null ? output.trim().replace("\n", "") : null;
        String trimmedExpectedAnswer = expectedAnswer != null ? expectedAnswer.trim().replace("\n", "") : null;

        // 现在比较处理后的字符串
        return trimmedOutput != null && trimmedExpectedAnswer != null && trimmedOutput.equals(trimmedExpectedAnswer);
    }
}
