package com.QASystem.LightQA.datasource;

import java.util.List;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.QuestionAnsweringSystem;

public interface DataSource {
    public List<Question> getQuestions();

    public Question getQuestion(String questionStr);

    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem);

    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem);
}
