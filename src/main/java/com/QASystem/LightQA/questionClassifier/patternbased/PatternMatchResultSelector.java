package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.model.Question;

public interface PatternMatchResultSelector {

    public Question select(Question question, PatternMatchResult patternMatchResult);
}
