package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.questionClassifier.patternbased.PatternMatchResultSelector;
import com.QASystem.LightQA.questionClassifier.patternbased.PatternMatchStrategy;
import com.QASystem.LightQA.model.Question;

public interface QuestionClassifier {
    public void setPatternMatchStrategy(PatternMatchStrategy patternMatchStrategy);

    public PatternMatchStrategy getPatternMatchStrategy();

    public void setPatternMatchResultSelector(PatternMatchResultSelector patternMatchResultSelector);

    public PatternMatchResultSelector getPatternMatchResultSelector();

    public Question classify(String question);

    public Question classify(Question question);
}
