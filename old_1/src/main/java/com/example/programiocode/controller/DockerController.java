package com.example.programiocode.controller;

import com.example.programiocode.service.DockerContainerManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docker")
@Api(tags = "docker容器")
public class DockerController {

    @Autowired  // 自动注入 DockerContainerManager 实例
    private DockerContainerManager dockerContainerManager;


    @PostMapping("/create") //后续可能删除这个
    public String createContainer() {
        return dockerContainerManager.createAndStartContainer();
    }
}

