package com.QASystem.LightQA.datasource;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.CommonQuestionAnsweringSystem;
import com.QASystem.LightQA.system.QuestionAnsweringSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConsoleDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleDataSource.class);
    private static final int QUESTION_MINI_LENGTH = 3;
    private final DataSource dataSource;

    public ConsoleDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return null;
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        List<Question> questions = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
            Question question = null;
            LOG.info("Please input your question, end with "+ " exit command");
            String line = reader.readLine();
            while(line !=null && line.trim().length() > QUESTION_MINI_LENGTH) {
                if (line.startsWith("exit")) {
                    break;
                }
                if (!line.startsWith("#")) {
                   String questionStr = null;
                   String expectAnswer = null;
                   String[] attrs = line. trim().split("[:|：]");
                   if (attrs == null) {
                       questionStr = line.trim();
                   }
                   if (attrs != null && attrs.length == 1 ) {
                       questionStr = attrs[0];
                   }
                   if (attrs != null && attrs.length == 2 ) {
                       questionStr = attrs[0];
                       expectAnswer = attrs[1];
                   }
                   LOG.info("Question: " + questionStr);
                   LOG.info("ExpectAnswer: " + expectAnswer);

                   question = dataSource.getQuestion(questionStr);
                   if(question == null) {
                       LOG.error("Have not find any evidence from datasource.");
                   } else {
                       question.setExpectAnswer(expectAnswer);
                       LOG.info(question.toString());
                       if(questionAnsweringSystem != null && question != null) {
                           questionAnsweringSystem.answerQuestion(question);
                       }
                       questions.add(question);
                   }
                }
                LOG.info("Please input question and press \" ENTER \" end with exit command");
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader !=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        return null;
    }

    public static void main(String[] args) {
        DataSource dataSource = new BaiduDataSource();
        dataSource = new ConsoleDataSource(dataSource);
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        questionAnsweringSystem.setDataSource(dataSource);
        questionAnsweringSystem.answerQuestions();

        questionAnsweringSystem.showPerfectQuestions();
        questionAnsweringSystem.showNotPerfectQuestions();
        questionAnsweringSystem.showWrongQuestions();
    }
}
