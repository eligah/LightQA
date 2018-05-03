package com.QASystem.LightQA.select;

import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;

public interface CandidateAnswerSelect {
    public void select(Question question, Evidence evidence);
}
