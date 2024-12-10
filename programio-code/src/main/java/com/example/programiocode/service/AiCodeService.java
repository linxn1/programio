package com.example.programiocode.service;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.core.builder.ChatBuilder;
import com.baidubce.qianfan.model.chat.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AiCodeService {

    private static final String APIKey = "your_api_key";
    private static final String SecretKey = "your_secret_key";

    private static Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, APIKey, SecretKey);

    public String chat(String problem) {
        ChatBuilder builder = qianfan.chatCompletion().model("ERNIE-Speed-8K");
        builder.addMessage("user", problem);
        ChatResponse response = builder.execute();
        return response.getResult();
    }
}
