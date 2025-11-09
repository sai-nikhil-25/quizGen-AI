package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuizResultDetail {
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private boolean correct;

}
