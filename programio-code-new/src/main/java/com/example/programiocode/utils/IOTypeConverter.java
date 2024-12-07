package com.example.programiocode.utils;

public class IOTypeConverter {
    public static Integer timeConvertToInteger(String input) {
        // 检查输入是否为null或空字符串
        if (input == null || input.isEmpty()) {
            return null;
        }

        // 检查输入是否以"ms"结尾
        if (input.endsWith("ms")) {
            // 截取去掉"ms"后的字符串部分
            String numberStr = input.substring(0, input.length() - 2);
            return Integer.parseInt(numberStr);
        }
        // 如果输入不是以"ms"结尾的字符串，则直接返回null（根据你的需求）
        return null;
    }

    public static Integer memoryConvertToInteger(String input) {
        // 检查输入是否为null或空字符串
        if (input == null || input.isEmpty()) {
            return null;
        }

        // 检查输入是否以"ms"结尾
        if (input.endsWith("kb")) {
            // 截取去掉"ms"后的字符串部分
            String numberStr = input.substring(0, input.length() - 2);
            return Integer.parseInt(numberStr);
        }
        // 如果输入不是以"ms"结尾的字符串，则直接返回null（根据你的需求）
        return null;
    }

    // 标准化输出的方法
    public static String normalizeOutput(String output) {
        if (output == null || output.trim().isEmpty()) {
            return null;  // 处理空值或空字符串
        }

        String outputStr = output.trim().toLowerCase(); // 去除前后空格并转换为小写

        if ("true".equals(outputStr) || "false".equals(outputStr)) {
            return outputStr;  // 返回小写的布尔字符串
        } else if ("none".equals(outputStr) || "null".equals(outputStr)) {
            return null;  // 返回 null 字符串
        }

        return outputStr;  // 其他情况直接返回原始输出
    }
}
