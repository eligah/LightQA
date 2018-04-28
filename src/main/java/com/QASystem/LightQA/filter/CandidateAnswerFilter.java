package com.QASystem.LightQA.filter;

import java.util.List;

import com.QASystem.LightQA.model.CandidateAnswer;
import com.QASystem.LightQA.model.Question;

public interface CandidateAnswerFilter {
    public void filter(Question question, List<CandidateAnswer> candidateAnswers);
}
