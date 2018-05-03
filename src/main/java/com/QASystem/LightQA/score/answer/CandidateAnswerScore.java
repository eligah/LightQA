package com.QASystem.LightQA.score.answer;

import com.QASystem.LightQA.model.CandidateAnswerCollection;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;

public interface CandidateAnswerScore {

    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection);

    public void setScoreWeight(ScoreWeight scoreWeight);
}
