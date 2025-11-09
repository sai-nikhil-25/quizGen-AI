package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class    QuizSubmissionRequest {
    private String quizId;
    private Map<Integer, Integer> answers;

}
