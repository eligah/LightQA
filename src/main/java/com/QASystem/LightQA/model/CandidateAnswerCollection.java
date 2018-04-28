package com.QASystem.LightQA.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandidateAnswerCollection {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateAnswerCollection.class);
    private List<CandidateAnswer> candidateAnswers = new ArrayList<>();

    public void addAnswer(CandidateAnswer candidateAnswer) {
        if (!candidateAnswers.contains(candidateAnswer)) {
            candidateAnswers.add(candidateAnswer);
        }
    }

    public void removeAnswer(CandidateAnswer candidateAnswer) {
        candidateAnswers.remove(candidateAnswer);
    }

    public boolean isEmpty() {
        return candidateAnswers.isEmpty();
    }

    public List<CandidateAnswer> getAllCandidateAnswer() {
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        return candidateAnswers;
    }

    public List<CandidateAnswer> getTopNCandidateAnswer(int n) {
        List<CandidateAnswer> result = new ArrayList<>();
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        int len = candidateAnswers.size();
        if (n > len) {
            n = len;
        }
        for (int i = 0; i < len; i++) {
            result.add(candidateAnswers.get(i));
        }

        return result;
    }

    public void showAll() {
        for (CandidateAnswer candidateAnswer : getAllCandidateAnswer()) {
            LOG.debug(candidateAnswer.getAnswer() + " " + candidateAnswer.getScore());
        }
    }

    public void showTopN(int topN) {
        for (CandidateAnswer candidateAnswer : getTopNCandidateAnswer(topN)) {
            LOG.debug(candidateAnswer.getAnswer() + " " + candidateAnswer.getScore());
        }
    }
}
