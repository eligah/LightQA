package com.QASystem.LightQA.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.QASystem.LightQA.model.*;

public class CandidateAnswerFilterInQuestion implements CandidateAnswerFilter{
    private static final Logger LOG = LoggerFactory.getLogger(CandidateAnswerFilterInQuestion.class);

    @Override
    public void filter(Question question, List<CandidateAnswer> candidateAnswers){
        List<String> questionWords = question.getWords();
        StringBuilder str = new StringBuilder();
        str.append("Segment for question: ");
    for (String questionWord : questionWords){
        str.append(questionWord).append(" ");
    }
    LOG.debug(str.toString());



    }
}
