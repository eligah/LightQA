package com.QASystem.LightQA.score.evidence;

import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;


public interface EvidenceScore {
    public void score(Question question, Evidence evidence);

    public void setScoreWeight(ScoreWeight scoreWeight);
}
