package com.kunduri.quizgen.controller;

import com.kunduri.quizgen.dto.*;
import com.kunduri.quizgen.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @PostMapping("/generate")
    public ResponseEntity<QuizResponse> generateQuiz(@RequestBody QuizRequest request) {
        QuizResponse response = quizService.generateQuiz(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(@RequestBody QuizSubmissionRequest request) {
        QuizResultResponse response = quizService.submitQuiz(request);
        return ResponseEntity.ok(response);
    }
}
