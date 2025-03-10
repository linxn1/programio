package com.example.programiocode.service;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.core.builder.ChatBuilder;
import com.baidubce.qianfan.model.chat.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiCodeService {

    private static final String APIKey = "hbJ56QLCFlN3cY1V1P3NiXGH";
    private static final String SecretKey = "MSVYRVpj6Qq5IrmMPbR3rS3T1tOur3Op";

    private static Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, APIKey, SecretKey);

    public String chat(String problem) {
        try {
            ChatBuilder builder = qianfan.chatCompletion().model("ERNIE-Speed-8K");
            builder.addMessage("user", problem);
            ChatResponse response = builder.execute();
            return response.getResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "功能暂不可用";
    }
}

