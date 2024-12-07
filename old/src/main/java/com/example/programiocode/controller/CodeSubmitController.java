package com.example.programiocode.controller;

import com.example.programiocode.pojo.dto.CodeSubmitResponseDTO;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Api(tags = "代码上传")
public class CodeSubmitController {

    // 定义文件上传的目录
    private static final String UPLOAD_DIR = "/path/to/upload/dir/";

    @PostMapping("/codesubmit")
    public ResponseEntity<CodeSubmitResponseDTO> uploadCodeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("language") String language,
            @RequestParam("description") String user_id // 接收一个字符串参数
    ) {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CodeSubmitResponseDTO(null, null, null));
        }

        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 创建文件对象
            File dest = new File(UPLOAD_DIR + File.separator + fileName);
            // 保存文件到指定目录
            CodeSubmitResponseDTO codeSubmitResponseDTO = new CodeSubmitResponseDTO("100%", "10ms", "10kb");
            file.transferTo(dest);

            return ResponseEntity.ok().body(codeSubmitResponseDTO);
        } catch (
                IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CodeSubmitResponseDTO(null, null, null));
        }
    }
}