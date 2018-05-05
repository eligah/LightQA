package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.model.QuestionType;
import com.QASystem.LightQA.questionClassifier.patternbased.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

;

public class ValidateClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateClassifier.class);

    private static PatternBasedMultiLevelQuestionClassifier questionClassifier = null;
    private static final List<String> no = new ArrayList<>();
    private static final List<String> wrong = new ArrayList<>();
    private static final List<String> right = new ArrayList<>();
    private static final List<String> unknown = new ArrayList<>();

    static {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");

        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();
        questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);
    }

    private static void validate(String fileprefix, QuestionType rightQuestionType) {
        String file = "/"
    }
}
