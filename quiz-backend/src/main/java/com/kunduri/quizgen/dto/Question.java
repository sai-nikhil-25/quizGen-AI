package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Question {
    private String question;
    private List<String> options;
    private Integer correctAnswer;

}
