package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.model.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionTypeTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionTypeTransformer.class);

    public static QuestionType transform(String questionType) {
        LOG.debug("Question type transform: " + questionType);
        if(questionType.contains("Person")) {
            return QuestionType.PERSON_NAME;
        }
        if(questionType.contains("Location")) {
            return QuestionType.LOCATION_NAME;
        }
        if (questionType.contains("Organization")) {
            return QuestionType.ORGANIZATION_NAME;
        }
        if (questionType.contains("Number")) {
            return QuestionType.NUMBER;
        }
        if (questionType.contains("Time")) {
            return QuestionType.TIME;
        }

        LOG.error("Question type transformation failed, using PERSON_NAME as default: " + questionType);
        return QuestionType.PERSON_NAME;
    }

    public static void main(String[] args){
        System.out.println(transform("Person->Multi5"));
    }
}
