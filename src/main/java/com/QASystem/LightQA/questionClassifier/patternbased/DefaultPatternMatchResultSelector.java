package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.model.QuestionType;
import com.QASystem.LightQA.questionClassifier.QuestionTypeTransformer;
import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//选择最多的匹配种类
public class DefaultPatternMatchResultSelector implements PatternMatchResultSelector {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPatternMatchResultSelector.class);

    @Override
    public Question select(Question question, PatternMatchResult patternMatchResult) {
        List<PatternMatchResultItem> allPatternMatchResultItems = patternMatchResult.getAllPatternMatchResult();
        if (allPatternMatchResultItems == null || allPatternMatchResultItems.isEmpty()) {
            LOG.info("Cannot get any question pattern matching result.");
            return null;
        }
        for (QuestionTypePatternFile file : patternMatchResult.getQuestionTypePatternFilesFromCompactToLoose()) {
            List<PatternMatchResultItem> patternMatchResultItems = patternMatchResult.getPatternMatchResult(file);
            if (patternMatchResultItems == null || patternMatchResultItems.isEmpty()) {
                LOG.info("Question type pattern" + file + "match result is empty.");
                continue;
            }
            LOG.info("Process question type pattern: " + file.getFile() + ". Allow mulitple match: "+ file.isMultiMatch());

            Map<QuestionType, Integer> map = new HashMap<>();
            for(PatternMatchResultItem patternMatchResultItem : patternMatchResultItems) {
                String type = patternMatchResultItem.getType();
                QuestionType key = QuestionTypeTransformer.transform(type);
                Integer value = map.get(key);
                if (value == null) {
                    value = 1;
                } else {
                    value++;
                }
                map.put(key,value);
            }

            List<Map.Entry<QuestionType, Integer>> entrys = Tools.sortByIntegerValue(map);
            Collections.reverse(entrys);

            if(entrys.size() > 1) {
                LOG.info("\tType\tTimes");
                for (Map.Entry<QuestionType,Integer> entry : entrys) {
                    LOG.info("\t" + entry.getKey() + "\t" + entry.getValue());
                    question.addCandidateQuestionType(entry.getKey());
                }
                if (!file.isMultiMatch()) {
                    LOG.info("Do not allow multiple match.");
                    question.setQuestionType(QuestionType.NULL);
                    continue;
                }
                LOG.info("For default question type selector, choose the most frequent matching type.");
                QuestionType selectedType = entrys.get(0).getKey();
                question.setQuestionType(selectedType);

                if(question.getCandidateQuestionTypes().contains(selectedType)) {
                    question.removeCandidateQuestionType(selectedType);
                }
                return question;
            }else {
                LOG.info("Only one match result.");
                LOG.info("\tType\tTimes");
                for (Map.Entry<QuestionType, Integer> entry : entrys) {
                    LOG.info("\t" + entry.getKey() + "\t" + entry.getValue());
                }
                QuestionType selectedType = entrys.get(0).getKey();
                question.setQuestionType(selectedType);
                LOG.info("Question type pattern " + file.getFile() + "have 1 match." + question.getQuestionType().name());
                return question;
            }
        }
        LOG.info("Matching failure, cannot identify the question type. Assign the question type to : " + QuestionType.PERSON_NAME);
        question.setQuestionType(QuestionType.PERSON_NAME);
        return question;

    }
}
