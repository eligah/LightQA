package com.QASystem.LightQA.score.answer;

import com.QASystem.LightQA.model.CandidateAnswerCollection;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CombinationCandidateAnswerScore implements CandidateAnswerScore{

    private static final Logger LOG = LoggerFactory.getLogger(CombinationCandidateAnswerScore.class);
    private final List<CandidateAnswerScore> candidateAnswerScores = new ArrayList<>();
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    public void addCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore) {
        candidateAnswerScores.add(candidateAnswerScore);
    }

    public void removeCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore) {
        candidateAnswerScores.remove(candidateAnswerScore);
    }

    public void clear() {
        candidateAnswerScores.clear();
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        for (CandidateAnswerScore candidateAnswerScore : candidateAnswerScores) {
            candidateAnswerScore.score(question, evidence, candidateAnswerCollection);
        }
    }
}
