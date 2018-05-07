package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.questionClassifier.patternbased.MainPartExtracter;
import com.QASystem.LightQA.questionClassifier.patternbased.QuestionStructure;
import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ValidateMainPartExtracter {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateMainPartExtracter.class);

//    public static void validate() {
//        //TODO finsh validate mainpart.
//        MainPartExtracter mainPartExtracter = new MainPartExtracter();
//        String file = "/com/QAsystem/questiontypeanalysis/AllTestQuestionWithMainPart.txt";
//        Set<String> questionStr = Tools.getQuestions(file);
//        LOG.info("Load" + questionStr.size() + " questions：" + file);
//        List<QuestionStructure> questions = parseQuestions(questionStr);
//        LOG.info("Add " + questionStr.size() + " items.");
//        LOG.info("Parse " + questions.size() + " question successfully.");
//
//        //提取失败
//        List<QuestionStructure> no = new ArrayList<>();
//        //提取成功，未标注
//        List<QuestionStructure> yes = new ArrayList<>();
//        //提取成功，标注不一致
//        Map<QuestionStructure, String> wrong = new HashMap<>();
//        //提取成功，标注一致
//        List<QuestionStructure> right = new ArrayList<>();
//
//        for (QuestionStructure question : questions) {
//            QuestionStructure questionStructure = mainPartExtracter.getMainPart(question.getQuestion());
//            if (questionStructure == null || questionStructure.getMainPart() == null) {
//                //不能提取
//                no.add(questionStructure);
//            } else {
//                if (question.getMainPart() != null) {
//                    if (questionStructure.getMainPart().equals(question.getMainPart())) {
//                        right.add(questionStructure);
//                    } else {
//                        String errorInfo;
//                        if (question.getMainPart())
//                    }
//                }
//            }
//        }
//        //两种提取模式结果一致的情况
//        int perfect = 0;
//        //不能提取
//        //能提取主谓宾但未标注
//        //能提取主谓宾但和标注不一致
//        //能提取主谓宾且和标注一致
//        LOG.info("");
//        LOG.info("能提取主谓宾但未标注（" + yes.size() + "）：");
//        int b = 1;
//        for (QuestionStructure item : yes) {
//            if (item.perfect()) {
//                perfect++;
//            }
//            LOG.info((b++) + " " + item.getQuestion() + " : " + item.getMainPart());
//            for (String den : item.getDependencies()) {
//                LOG.info("\t" + den);
//            }
//        }
//        LOG.info("");
//        LOG.info("不能提取主谓宾数（" + no.size() + "）：");
//        int a = 1;
//        for (QuestionStructure item : no) {
//            LOG.info((a++) + " " + item.getQuestion());
//            for (String den : item.getDependencies()) {
//                LOG.info("\t" + den);
//            }
//        }
//        LOG.info("");
//        LOG.info("能提取主谓宾但和标注【不一致】数（" + wrong.size() + "）：");
//        int c = 1;
//        for (QuestionStructure item : wrong.keySet()) {
//            if (item.perfect()) {
//                perfect++;
//            }
//            LOG.info((c++) + " " + item.getQuestion() + " " + item.getMainPart());
//            for (String den : item.getDependencies()) {
//                LOG.info("\t" + den);
//            }
//            LOG.info("\t" + wrong.get(item));
//        }
//        LOG.info("");
//        LOG.info("能提取主谓宾且和标注【一致】数（" + right.size() + "）：");
//        int d = 1;
//        for (QuestionStructure item : right) {
//            if (item.perfect()) {
//                perfect++;
//            }
//            LOG.info((d++) + " " + item.getQuestion() + " : " + item.getMainPart());
//            for (String den : item.getDependencies()) {
//                LOG.info("\t" + den);
//            }
//        }
//        int total = right.size() + wrong.size() + yes.size() + no.size();
//        LOG.info("主谓宾提取统计");
//        LOG.info("两种提取模式结果一致数: " + perfect);
//        LOG.info("两种提取模式结果一致率: " + (double) perfect / total * 100 + "%");
//        LOG.info("问题总数: " + total);
//        LOG.info("识别数: " + (total - no.size()));
//        LOG.info("识别率: " + (double) (total - no.size()) / total * 100 + "%");
//        LOG.info("未识别数: " + no.size());
//        LOG.info("未识别率: " + (double) no.size() / total * 100 + "%");
//        LOG.info("人工标注数: " + human);
//        LOG.info("人工标注率: " + (double) human / total * 100 + "%");
//        LOG.info("识别准确数(人工标注): " + right.size());
//        LOG.info("识别准确率(人工标注): " + (double) right.size() / human * 100 + "%");
//        LOG.info("识别不准确数(人工标注): " + wrong.size());
//        LOG.info("识别不准确率(人工标注): " + (double) wrong.size() / human * 100 + "%");
//    }



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
                   }
                }
            } else{
                LOG.info("Cannot get the SVO: " + question);
            }
        }
        return result;
    }

    public static void main(String[] args) {
       // validate();
    }
}
