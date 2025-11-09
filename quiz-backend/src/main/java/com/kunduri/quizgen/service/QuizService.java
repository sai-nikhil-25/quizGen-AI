package com.kunduri.quizgen.service;

import com.kunduri.quizgen.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {


    private final AIService aiService;

    // Store quiz sessions in memory
    private Map<String, QuizSession> quizSessions = new HashMap<>();

    public QuizResponse generateQuiz(QuizRequest request) {
        // Call AI service to generate questions
        List<Question> questions = aiService.generateQuestions(request.getTopic(), request.getLevel());

        // Generate unique quiz ID
        String quizId = UUID.randomUUID().toString();

        // Store quiz session with correct answers
        QuizSession session = new QuizSession();
        session.setQuizId(quizId);
        session.setTopic(request.getTopic());
        session.setLevel(request.getLevel());
        session.setQuestions(questions);
        session.setCreatedAt(new Date());
        quizSessions.put(quizId, session);

        // Return questions without correct answers
        QuizResponse response = new QuizResponse();
        response.setQuizId(quizId);
        response.setQuestions(questions.stream()
                .map(q -> {
                    Question sanitized = new Question();
                    sanitized.setQuestion(q.getQuestion());
                    sanitized.setOptions(q.getOptions());
                    return sanitized;
                })
                .toList());

        return response;
    }

    public QuizResultResponse submitQuiz(QuizSubmissionRequest request) {
        QuizSession session = quizSessions.get(request.getQuizId());

        if (session == null) {
            throw new RuntimeException("Quiz session not found");
        }

        List<Question> questions = session.getQuestions();
        Map<Integer, Integer> userAnswers = request.getAnswers();

        int score = 0;
        List<QuizResultDetail> details = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Integer userAnswer = userAnswers.get(i);
            boolean isCorrect = userAnswer != null && userAnswer.equals(question.getCorrectAnswer());

            if (isCorrect) score++;

            QuizResultDetail detail = new QuizResultDetail();
            detail.setQuestion(question.getQuestion());
            detail.setUserAnswer(userAnswer != null ? question.getOptions().get(userAnswer) : "Not answered");
            detail.setCorrectAnswer(question.getOptions().get(question.getCorrectAnswer()));
            detail.setCorrect(isCorrect);
            details.add(detail);
        }

        QuizResultResponse response = new QuizResultResponse();
        response.setScore(score);
        response.setTotalQuestions(questions.size());
        response.setDetails(details);

        // Clean up session (optional)
        // quizSessions.remove(request.getQuizId());

        return response;
    }
}
