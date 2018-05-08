package com.QASystem.LightQA.questionClassifier.patternbased;

import edu.stanford.nlp.ling.Word;

import java.util.List;

public interface AbstractMainPartExtracter {

    public QuestionStructure getMainPart(String question, String questionWords);

    public QuestionStructure getMainPart(String question, List<Word> words);

    public QuestionStructure getMainPart(String question);

    public String getQuestionMainPartNaturePattern(String question, String mainPart);

    public String getQuestionMainPartPattern(String question, String mainPart);

}
