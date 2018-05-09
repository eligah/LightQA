package com.QASystem.LightQA.searcher;

import com.QASystem.LightQA.model.*;
import com.QASystem.LightQA.questionClassifier.patternbased.QuestionPattern;

import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//TODO
public class BestClassifierSearcher {
    private static final Logger LOG = LoggerFactory.getLogger(BestClassifierSearcher.class);

    private static final Map<String, Double> map = new HashMap<>();
    private static final Map<String, Integer> map2 = new HashMap<>();

//    private static void classify() {
//        List<QuestionPattern> allQuestionPatterns = new ArrayList<>();
//        allQuestionPatterns.add(QuestionPattern.Question);
//        allQuestionPatterns.add(QuestionPattern.TermWithNatures);
//        allQuestionPatterns.add(QuestionPattern.Natures);
//        allQuestionPatterns.add(QuestionPattern.MainPartPattern);
//        allQuestionPatterns.add(QuestionPattern.MainPartNaturePattern);
//
//        List<String> allQuestionTypePatternFiles = new ArrayList<>();
//        //allQuestionTypePatternFiles.add("QuestionTypePatternsLevel1_true.txt");
//        allQuestionTypePatternFiles.add("QuestionTypePatternsLevel2_true.txt");
//        allQuestionTypePatternFiles.add("QuestionTypePatternsLevel3_true.txt");
//
//        List<List<QuestionPattern>> allQuestionPatternCom = Tools.getCom(allQuestionPatterns);
//        LOG.info("问题模式组合种类：" + allQuestionPatternCom.size());
//        List<List<String>> allQuestionTypePatternFileCom = Tools.getCom(allQuestionTypePatternFiles);
//        LOG.info("问题类型模式组合种类：" + allQuestionTypePatternFileCom.size());
//        LOG.info("需要计算" + allQuestionPatternCom.size() * allQuestionTypePatternFileCom.size() + "种组合");
//        classify(allQuestionPatternCom, allQuestionTypePatternFileCom);
//    }
}
