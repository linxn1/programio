package com.example.programiocode.mapper.question;


import com.example.programiocommon.pojo.dto.QuestionAnswerDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface QuestionAnswerMapper {

    QuestionAnswerDTO getQuestionAnswerById(Integer id);

    List<QuestionAnswerDTO> getQuestionAnswerByQuestionId(Integer questionId);
}
