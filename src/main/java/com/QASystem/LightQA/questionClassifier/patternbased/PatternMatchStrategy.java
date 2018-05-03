package com.QASystem.LightQA.questionClassifier.patternbased;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PatternMatchStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(PatternMatchStrategy.class);

    private final List<String> questionTypePatternFiles = new ArrayList<>();
    private final List<QuestionPattern> questionPatterns = new ArrayList<>();

    public boolean vaildate() {
        return questionTypePatternFiles.isEmpty() || questionPatterns.isEmpty();
    }

    private void addQuestionPattern(QuestionPattern questionPattern) {
        questionPatterns.add(questionPattern);
    }

    private void addQuestionTypePatternFile(String questionTypePatternFile) {
        questionTypePatternFiles.add(questionTypePatternFile);
    }

    public boolean enableQuestionTypePatternFile(String questionTypePatternFile) {
        return questionTypePatternFiles.contains(questionTypePatternFile);
    }

    public boolean enableQuestionPattern(QuestionPattern questionPattern) {
        return questionPatterns.contains(questionPattern);
    }

    public String getStrategyDes() {
        StringBuilder str = new StringBuilder();
        for (String questionTypePatternFile: questionTypePatternFiles) {
            str.append(questionTypePatternFile).append(":");
        }
        for (QuestionPattern questionPattern: questionPatterns) {
            str.append(questionPattern).append(":");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);

        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");
        System.out.println(patternMatchStrategy.getStrategyDes());
    }

}
