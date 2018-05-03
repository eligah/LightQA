package com.QASystem.LightQA.datasource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.QASystem.LightQA.files.FilesConfig;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.CommonQuestionAnsweringSystem;
import com.QASystem.LightQA.system.QuestionAnsweringSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class FileDataSource implements DataSource{
    private static final Logger LOG = LoggerFactory.getLogger(FileDataSource.class);

    private List<String> files = new ArrayList<>();

    public FileDataSource(List<String> files) {
        this.files.addAll(files);
    }

    public FileDataSource(String file) {
        this.files.add(file);
    }

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr,null);
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        for (Question question: getQuestions()) {
            String q = question.getQuestion().trim().replace("?", "").replace("？", "");
            questionStr.trim().replace("?", "").replace("？", "");
            if (q.equals(questionStr)) {
                if (questionAnsweringSystem != null) {
                    questionAnsweringSystem.answerQuestion(question);
                }
                return question;
            }
        }
        return null;
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {

        // TODO 问题不能全部载入
        List<Question> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader= null;
            try{
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));
                Question question = null;
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.trim().indexOf("#") == 1 || line.length() <3) {
                        line = reader.readLine();
                    }
                    if ( line.trim().startsWith("?") || line.trim().indexOf("？") == 1) {
                        if (questionAnsweringSystem != null && question != null){
                            questionAnsweringSystem.answerQuestion(question);
                        }
                        String qs = line.substring(line.indexOf(".") +1).trim();

                        String questionStr = null;
                        String expectAnswer = null;
                        String[] attrs = qs.split("[:|：]");

                        if (attrs == null) {
                            questionStr = qs;
                        }
                        if (attrs != null && attrs.length == 1) {
                            questionStr = attrs[0];
                        }
                        if (attrs != null && attrs.length == 2) {
                            questionStr = attrs[0];
                            expectAnswer = attrs[1];
                        }

                        LOG.info("Question: " + questionStr);
                        LOG.info("ExpectAnswer: " + expectAnswer);

                        question = new Question();
                        question.setQuestion(questionStr);
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);

                        line = reader.readLine();
                        continue;
                    }
                    Evidence answer = new Evidence();
                    if(line.startsWith("Title: ")) {
                        answer.setTitle(line.substring(6).trim());
                    }
                    line = reader.readLine();

                    if(line.startsWith("Snippet: ")) {
                        answer.setSnippet(line.substring(8).trim());
                    }
                    if (answer.getTitle() != null && answer.getSnippet() != null && question != null){
                        question.addEvidence(answer);
                    }
                    line = reader.readLine();
                }

                // answer the last question
                if (questionAnsweringSystem != null && question != null){
                    questionAnsweringSystem.answerQuestion(question);
                }
            } catch (FileNotFoundException e) {
                LOG.error("Cannot find the file", e);
            } catch (UnsupportedEncodingException e){
                LOG.error("Charset error", e);
            } catch (IOException e) {
                LOG.error("IO error", e);
            } finally {
                if (reader != null){
                    try{
                        reader.close();
                    } catch (IOException e){
                        LOG.error("closing file error", e);
                    }
                }
            }

        }
        return questions;
    }
    public static void main(String[] args) {
        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
        List<Question> questions = dataSource.getQuestions();
        for (Question question : questions) {
            LOG.info(question.toString());
        }
        Question question = dataSource.getQuestion("APDPlat的发起人是谁？");
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        questionAnsweringSystem.answerQuestion(question);
    }

}
