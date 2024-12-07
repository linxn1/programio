import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;

public class DockerExecExample {
    public static void main(String[] args) {
        // Docker 客户端初始化
        DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build();

        // 已存在的容器 ID
        String containerId = "373eb1b31f44bad717af325a4c3039dfe854829878c812869220762c88513ee8"; // 替换为你实际的容器 ID

        try {
            // 创建命令
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)  // 获取容器标准输出
                    .withAttachStderr(true)  // 获取容器标准错误输出
                    .withCmd("echo", "Hello from inside the container!") // 替换为你想执行的命令
                    .exec();

            // 确认 execCreateCmdResponse 是否为空，并打印日志
            if (execCreateCmdResponse != null) {
                System.out.println("Exec ID: " + execCreateCmdResponse.getId());
            } else {
                System.err.println("Failed to create exec command.");
            }

            // 启动命令并获取输出
            ExecStartCmd execStartCmd = dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .withDetach(false)  // 不以分离模式执行
                    .withTty(true);     // 启用 TTY（终端）
            // 获取输出并打印
            execStartCmd.exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
