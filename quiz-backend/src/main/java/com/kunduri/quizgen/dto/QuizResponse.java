package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuizResponse {
    private String quizId;
    private List<Question> questions;

}
