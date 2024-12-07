package com.example.programiocode.controller;

import com.example.programiocode.mapper.question.QuestionAnswerMapper;
import com.example.programiocode.mapper.question.QuestionInfoMapper;
import com.example.programiocode.service.QuestionInfoService;
import com.example.programiocommon.pojo.dto.QuestionAnswerDTO;
import com.example.programiocommon.pojo.to.QuestionRequestTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question-answer")
@Api(tags = "答案查询")
public class QuestionAnswerController {

    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;

    @Autowired
    private QuestionInfoService questionInfoService;

    @GetMapping("/id/{id}")
    public ResponseEntity<QuestionAnswerDTO> getQuestionAnswerById(@PathVariable Integer id) {
        QuestionAnswerDTO questionAnswer = questionAnswerMapper.getQuestionAnswerById(id);
        if (questionAnswer != null) {
            return ResponseEntity.ok(questionAnswer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/questionId/{id}")
    public ResponseEntity<List<QuestionAnswerDTO>> getQuestionAnswerByQuestionId(@PathVariable Integer id) {
        List<QuestionAnswerDTO> questionAnswer = questionAnswerMapper.getQuestionAnswerByQuestionId(id);
        if (questionAnswer != null) {
            return ResponseEntity.ok(questionAnswer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/questionInfo")
    public ResponseEntity<Void> postQuestionAnswerByQuestionId(@RequestBody QuestionRequestTO questionRequestTO) {
//        String difficulty = questionRequestTO.getDifficulty();
//        String question = questionRequestTO.getQuestion();
//        Integer timeRequest = questionRequestTO.getTimeRequest();
//        Integer memoryRequest = questionRequestTO.getMemoryRequest();
        questionInfoService.insertQuestion(questionRequestTO);

        return ResponseEntity.ok().build();
    }
}