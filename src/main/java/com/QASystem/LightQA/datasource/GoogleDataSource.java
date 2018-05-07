package com.QASystem.LightQA.datasource;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.QuestionAnsweringSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
//TODO Using java httpcliemt access google.
public class GoogleDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleDataSource.class);
    private String file;

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr,null);
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        return null;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        return null;
    }
}
