package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.questionClassifier.patternbased.MainPartExtracter;

import com.QASystem.LightQA.questionClassifier.patternbased.MainPartExtracterEX;
import com.QASystem.LightQA.questionClassifier.patternbased.AbstractMainPartExtracter;
import com.QASystem.LightQA.questionClassifier.patternbased.QuestionStructure;
import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ValidateMainPartExtracter {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateMainPartExtracter.class);

    public static void validate() {
        AbstractMainPartExtracter mainPartExtracter = new MainPartExtracterEX();
        String file = "/com/QAsystem/LightQA/questiontypeanalysis/PartOfTestQuestionWithMainPart.txt";
        Set<String> questionStr = Tools.getQuestions(file);
        LOG.info("Load " + questionStr.size() + " questions：" + file);
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

        int humanTags = 0;
        for (QuestionStructure question : questions) {
            QuestionStructure questionStructure = mainPartExtracter.getMainPart(question.getQuestion());
            if (questionStructure == null || questionStructure.getMainPart() == null) {
                //不能提取
                no.add(questionStructure);
            } else {
                //如果文件中问题有标注
                if (question.getMainPart() != null) {
                    humanTags++;
                    //判断提取是否正确
                    if (questionStructure.getMainPart().equals(question.getMainPart())) {
                        right.add(questionStructure);
                    } else {
                        String errorInfo;
                        String[] attrs1 = questionStructure.getMainPart().split("\\s+");
                        if( attrs1 == null || attrs1.length == 1  ) {
                            errorInfo = "SVO extraction error.";
                            wrong.put(questionStructure, errorInfo);
                            continue;
                        }

                        String[] attrs2 = question.getMainPart().split("\\s+");
                        if (attrs2 == null || attrs2.length == 1){
                            errorInfo = "SVO tagging error.";
                            wrong.put(questionStructure, errorInfo);
                            continue;
                        }
                        //TODO output different msg of SVO matching result
                        StringBuilder str = new StringBuilder();
//                        if(!attrs1[0].trim().equals(attrs2[0].trim())) {
//                            str.append("Subject extraction error ");
//                        }
//                        if(!attrs1[1].trim().equals(attrs2[1].trim())) {
//                            str.append(" predicate extraction error ");
//                        }
//                        if(!attrs1[2].trim().equals(attrs2[2].trim())) {
//                            str.append(" Object extraction error ");
//                        }
//                        str.append(" Right SVO: ").append(question.getMainPart());
                        wrong.put(questionStructure, str.toString().trim());
                    }
                } else {
                    // 提取未标注
                    yes.add(questionStructure);
                }
            }
        }


        int total = right.size() + wrong.size() + yes.size() + no.size();
        LOG.info("SVO extraction statistic: ");
        LOG.info("Question set size: " + total);
        LOG.info("Indentify : " + (total - no.size()));
        LOG.info("Indentify rate: " + (double) (total - no.size()) / total * 100 + "%");
        LOG.info("Human tagging set size: " + humanTags);
        LOG.info("Human tagging rate: " + (double) humanTags / total * 100 + "%");
        LOG.info("Recognition correct number : " + right.size());
        LOG.info("Recognition correct rate: " + (double) right.size() / humanTags * 100 + "%");
        LOG.info("Recognition error number: " + wrong.size());
        LOG.info("Recognition error rate: " + (double) wrong.size() / humanTags * 100 + "%");
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
                   String[] p = s.split("\\s+");
                   if(p == null || p.length != 3){
                       LOG.info("Cannot get the SVO: " + question);
                   } else {
                       QuestionStructure qs =new QuestionStructure();
                       qs.setQuestion(q);
                       qs.setMainPart(p[0].trim() + " " + p[1].trim() +" "+ p[2].trim());
                       result.add(qs);
                   }
                }
            } else{
                LOG.info("Cannot get the SVO: " + question);
            }
        }
        return result;
    }

    public static void main(String[] args) {
       validate();
    }
}
