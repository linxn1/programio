package com.example.programiocode.service;

import com.example.programiocode.mapper.question.QuestionInfoMapper;
import com.example.programiocommon.pojo.dto.QuestionInfoDTO;
import com.example.programiocommon.pojo.to.QuestionRequestTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionInfoService {
    @Autowired
    private QuestionInfoMapper questionInfoMapper;

    public void insertQuestion(QuestionRequestTO questionRequestTO) {
        QuestionInfoDTO questionInfoDTO = new QuestionInfoDTO();
        BeanUtils.copyProperties(questionRequestTO,questionInfoDTO);
        questionInfoMapper.insertQuestionInfo(questionInfoDTO);
    }
}
