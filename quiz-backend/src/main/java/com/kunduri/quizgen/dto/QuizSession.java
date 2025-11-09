package com.kunduri.quizgen.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class QuizSession {
    private String quizId;
    private String topic;
    private String level;
    private List<Question> questions;
    private Date createdAt;

}
