package com.example.programiocode.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@ConfigurationProperties(prefix = "docker")
@Component
@Data
public class DockerContainerManager {

    private DockerClient dockerClient; // 类成员变量
    private String containerId;

    private String image;
    private String containerName;
    private int exposedPort;
    private int hostPort;
    private String host;
    private String dockerVolume;

    // 构造函数中正确初始化 dockerClient
    public DockerContainerManager() {
    }

    @PostConstruct // 初始化之后执行,直接在构造方法利做会出现null
    public void init() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host)  // 使用配置中的 Docker host
                .build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build(); // 初始化类级别的成员变量
    }


    public String createAndStartContainer() {
        if (containerId != null) {
            return containerId;  // 如果容器已经创建，直接返回容器 ID
        }

        try {
            // 直接使用本地镜像
            if (dockerClient.inspectImageCmd(image).exec() == null) {
                throw new RuntimeException("Image " + image + " does not exist locally.");
            }

            // 创建端口暴露配置
            PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(hostPort), ExposedPort.tcp(exposedPort));

            // 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                    .withName(containerName)
                    .withVolumes(new Volume(dockerVolume))
                    .withExposedPorts(ExposedPort.tcp(exposedPort))
                    .withHostConfig(HostConfig.newHostConfig()
                            .withPortBindings(portBinding))
                    .exec();

            this.containerId = container.getId();

            // 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            System.out.println("创建成功!!!");
            return containerId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create and start container", e);
        }
    }

    @PreDestroy //钩子方法,在服务关闭前销毁容器
    public void cleanup() {
        if (containerId != null) {
            try {
                // 检查容器是否正在运行
                if (dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning()) {
                    // 停止容器
                    StopContainerCmd stopCmd = dockerClient.stopContainerCmd(containerId);
                    stopCmd.exec();
                    System.out.println("容器 " + containerId + " 已停止。");
                }

                // 删除容器
                RemoveContainerCmd removeCmd = dockerClient.removeContainerCmd(containerId).withForce(true); // 强制删除
                removeCmd.exec();
                System.out.println("容器 " + containerId + " 已被删除。");
            } catch (Exception e) {
                System.err.println("停止和删除容器失败: " + e.getMessage());
            }
        }
    }


    public String getContainerId() {
        return containerId;
    }
}
