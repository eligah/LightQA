package com.QASystem.LightQA.score.answer;

import java.util.ArrayList;
import java.util.List;

import com.QASystem.LightQA.model.*;

import com.QASystem.LightQA.system.ScoreWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreTextualAlignmentCandidateAnswerScore extends TextualAlignmentCandidateAnswerScore {
    private static final Logger LOG = LoggerFactory.getLogger(MoreTextualAlignmentCandidateAnswerScore.class);


    @Override
    protected List<String> getQuestionWords(Question q) {
        List<String> list = q.getWords();
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if( item.length() > 1 ){
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Loose textual alignment candidate answer score start.");
        super.score(question, evidence, candidateAnswerCollection);
        LOG.debug("Loose textual alignment candidate answer score have finished.");
        LOG.debug("*************************");
    }

}
