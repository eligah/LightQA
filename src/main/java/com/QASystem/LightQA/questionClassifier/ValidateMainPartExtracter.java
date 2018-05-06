package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.questionClassifier.patternbased.MainPartExtracter;
import com.QASystem.LightQA.questionClassifier.patternbased.QuestionStructure;
import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ValidateMainPartExtracter {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateMainPartExtracter.class);

    public static void validate() {
        MainPartExtracter mainPartExtracter = new MainPartExtracter();
        String file = "/com/QAsystem/questiontypeanalysis/AllTestQuestionWithMainPart.txt";
        Set<String> questionStr = Tools.getQuestions(file);
        LOG.info("Load" + questionStr.size() + " questions：" + file);
        List<QuestionStructure> questions = parseQuestions(questionStr);
        LOG.info("Add " + questionStr.size() + " items.");
        LOG.info("Parse " + questions.size() + " question successfully.");

        //提取失败
        List<QuestionStructure> no = new ArrayList<>();
        //提取成功，未标注
        List<QuestionStructure> yes = new ArrayList<>();
        //提取成功，标注不一致
        Map<QuestionStructure, String> wrong = new HashMap<>();
        //提取成功，标注一致
        List<QuestionStructure> right = new ArrayList<>();

        for (QuestionStructure question : questions) {
            QuestionStructure questionStructure = mainPartExtracter.getMainPart(question.getQuestion());
            if (questionStructure == null || questionStructure.getMainPart() == null) {
                //不能提取
                no.add(questionStructure);
            } else {
                if(question.getMainPart() != null) {
                    if (questionStructure.getMainPart().equals(question.getMainPart())) {
                        right.add(questionStructure);
                    }else {
                        String errorInfo;
                        if (question.getMainPart())
                    }
                }
            }
        }
    }

    private static List<QuestionStructure> parseQuestions(Set<String> questions) {
        LOG.info("Parse the SVO of materials");
        List<QuestionStructure> result = new ArrayList<>();
        for(String question: questions) {
            question = question.trim();
            String[] attrs = question.split(":");
            if (attrs == null) {
                LOG.info("Question do not have SVO label: " + question);
                QuestionStructure qs = new QuestionStructure();
                qs.setQuestion(question);
                result.add(qs);
            } else if (attrs.length == 1) {
                LOG.info("Question do not have SVO label: " + question);
                QuestionStructure qs = new QuestionStructure();
                qs.setQuestion(attrs[0].trim());
                result.add(qs);
            } else if (attrs.length == 2) {
                String q = attrs[0];
                String s = attrs[1];
                if(s == null || "".equals(s.trim())) {
                    LOG.info("Question do not have SVO label: " + question);
                } else {
                   String[] p = s.split("\\s+")
                }
            }
        }
    }

    public static void main(String[] args) {
        validate();
    }
}
