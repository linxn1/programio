package com.example.programiocode.mapper.question;

import com.example.programiocommon.pojo.dto.QuestionInfoDTO;
import org.mapstruct.Mapper;

@Mapper
public interface QuestionInfoMapper {

    void insertQuestionInfo(QuestionInfoDTO questionInfoDTO);
}
