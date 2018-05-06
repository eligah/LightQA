package com.QASystem.LightQA.score.evidence;

import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.score.evidence.EvidenceScore;
import com.QASystem.LightQA.system.ScoreWeight;

import java.util.ArrayList;
import java.util.List;

public class CombinationEvidenceScore implements EvidenceScore {

    private final List<EvidenceScore> evidenceScores = new ArrayList<>();
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    public void addEvidenceScore(EvidenceScore evidenceScore) {
        evidenceScores.add(evidenceScore);
    }

    public void removeEvidenceScore(EvidenceScore evidenceScore) {
        evidenceScores.remove(evidenceScore);
    }

    public void clear() {
        evidenceScores.clear();
    }

    @Override
    public void score(Question question, Evidence evidence) {
        for (EvidenceScore evidenceScore : evidenceScores) {
            evidenceScore.score(question, evidence);
        }
    }

}
