# 使用一个轻量级基础镜像（可以使用 debian、alpine 等）
FROM openjdk:8-jdk-slim
# 设置工作目录
WORKDIR /app

# 安装 Python、C++ 编译器和 procps（包括 ps）
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    g++ \
    procps \
    && rm -rf /var/lib/apt/lists/*

# 将本地的 JAR 文件复制到容器中
COPY out/artifacts/programio_code_jar/programio-code.jar /app/programio-code.jar


# 暴露容器的端口
EXPOSE 15000

# 启动容器时自动运行 Java 服务
CMD ["java", "-jar", "/app/programio-code.jar", "--server.port=15000"]
