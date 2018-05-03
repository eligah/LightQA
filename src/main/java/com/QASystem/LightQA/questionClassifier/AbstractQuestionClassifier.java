package com.QASystem.LightQA.questionClassifier;

import com.QASystem.LightQA.questionClassifier.patternbased.PatternMatchResultSelector;
import com.QASystem.LightQA.questionClassifier.patternbased.PatternMatchStrategy;
import com.QASystem.LightQA.model.Question;

public abstract class AbstractQuestionClassifier implements QuestionClassifier {

    private PatternMatchStrategy patternMatchStrategy;
    private PatternMatchResultSelector patternMatchResultSelector;

    @Override
    public Question classify(String question) {
        Question q = new Question();
        q.setQuestion(question);
        return classify(q);
    }

    @Override
    public PatternMatchStrategy getPatternMatchStrategy() {
        return patternMatchStrategy;
    }

    @Override
    public PatternMatchResultSelector getPatternMatchResultSelector() {
        return patternMatchResultSelector;
    }

    @Override
    public void setPatternMatchStrategy(PatternMatchStrategy patternMatchStrategy) {
        this.patternMatchStrategy = patternMatchStrategy;
    }

    @Override
    public void setPatternMatchResultSelector(PatternMatchResultSelector patternMatchResultSelector) {
        this.patternMatchResultSelector = patternMatchResultSelector;
    }
}
