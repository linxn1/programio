package com.example.programiocode.utils;

import com.example.programiocommon.pojo.to.CodeRespondTO;

public interface ExecutionStrategy {
    CodeRespondTO executeCode(String language, String code, String userInput);
}
