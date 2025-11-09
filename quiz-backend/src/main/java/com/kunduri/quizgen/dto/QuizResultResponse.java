package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuizResultResponse {
    private int score;
    private int totalQuestions;
    private List<QuizResultDetail> details;

}
