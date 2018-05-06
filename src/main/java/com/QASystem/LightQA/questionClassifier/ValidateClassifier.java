package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.model.QuestionType;
import com.QASystem.LightQA.questionClassifier.patternbased.*;

import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        String file = "/com/QAsystem/LightQA/questiontypeanalysis/" + fileprefix + "_name_questions.txt";
        LOG.info(file);
        Set<String> questions = Tools.getQuestions(file);//
        LOG.info("From " + fileprefix + " get " + questions.size() + " questions.");

        for (String q : questions) {
            q = q.split(":")[0];
            Question question = questionClassifier.classify(q);//
            if (question == null) {
                no.add(rightQuestionType + " cannot identify: " + q);
            } else if (question.getQuestionType() != rightQuestionType) {
                wrong.add(rightQuestionType + "wrong : " + q + "type: " + question.getQuestionType().name() + " candidate types: " + question.getCandidateQuestionTypes());
            } else if (question.getQuestionType() == rightQuestionType) {
                right.add(rightQuestionType + "right classification: " + q + " type: " + question.getQuestionType().name());
            } else {
                unknown.add(rightQuestionType + " unknown situation for question: " + q);
            }
        }
    }
            
    private static void validate() {
        validate("person", QuestionType.PERSON_NAME);
        validate("location", QuestionType.LOCATION_NAME);
        validate("organization", QuestionType.ORGANIZATION_NAME);
        validate("number", QuestionType.NUMBER);
        validate("time", QuestionType.TIME);
        
        int all = no.size() + right.size() + unknown.size() + wrong.size();
        LOG.info("Size of questions: " + all);
        LOG.info("Right Classification: " + right.size());
        for (String item : right) {
            LOG.info(item);
        }
        LOG.info("Wrong classification: " + wrong.size());
        for (String item : wrong) {
            LOG.info(item);
        }
        LOG.info("Cannot classify: " + no.size());
        for (String item : no) {
            LOG.info(item);
        }
        if (unknown.size() > 0) {
            LOG.info("Unknown situation: " + unknown.size());
            for (String item : unknown) {
                LOG.info(item);
            }
        }
        LOG.info("Question classification statistics");
        LOG.info("Size of questions: " + all);
        LOG.info("Right Classification: " + right.size());
        LOG.info("Wrong Classification: " + wrong.size());
        LOG.info("Cannot classify: " + no.size());
        LOG.info("Positive rate " + (double) right.size() / all * 100 + "%");
        LOG.info("Negative rate: " + (double) wrong.size() / all * 100 + "%");
        LOG.info("Not work rate: " + (double) no.size() / all * 100 + "%");
    }
    public static void main(String[] args) {
        validate();
    }
}

