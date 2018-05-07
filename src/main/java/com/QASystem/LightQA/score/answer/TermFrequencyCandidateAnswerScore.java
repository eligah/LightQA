package com.QASystem.LightQA.score.answer;
import com.QASystem.LightQA.model.*;

import com.QASystem.LightQA.parser.WordParser;
import com.QASystem.LightQA.system.ScoreWeight;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//对于每一个候选答案，每出现1次在evidence title 中 得分TITLE_WEIGHT分。
//每出现1次在snippet 中，得分1分。

public class TermFrequencyCandidateAnswerScore implements CandidateAnswerScore{
    private static final Logger LOG = LoggerFactory.getLogger(TermFrequencyCandidateAnswerScore.class);
    private static final int TITLE_WEIGHT = 2;
    private ScoreWeight scoreWeight = new ScoreWeight();
    private Question question;


    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Term frequency scoring start.");
        this.question = question;
        Map<String, Integer> map = getWordFrequency(evidence.getTitle(), evidence.getSnippet());
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            Integer wordFrequency = map.get(candidateAnswer.getAnswer());
            if (wordFrequency == null) {
                LOG.debug("Cannot find the candidate answer: " + candidateAnswer.getAnswer() + " word frequency information");
                continue;
            }
            double score = wordFrequency * scoreWeight.getTermFrequencyCandidateAnswerScoreWeight();
            LOG.debug(candidateAnswer.getAnswer() + " Score：" + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("Term frequency scoring have finished.");
        LOG.debug("*************************");
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    private Map<String, Integer> getWordFrequency(String title, String snippet) {
        List<String> titleNames = new ArrayList<>();
        List<String> snippetNames = new ArrayList<>();

        List<Word> words= WordParser.parse(title);
        for (Word word : words) {
            if(word.getPartOfSpeech().getPos().startsWith(question.getQuestionType().getPos())) {
                titleNames.add(word.getText());
            }
        }
        Map<String, Integer> map = new HashMap<>();
        for (String name : titleNames) {
            Integer count = map.get(name);
            if (count == null) {
                count = TITLE_WEIGHT;
            } else {
                count += TITLE_WEIGHT;
            }
            map.put(name, count);
        }
        for (String name : snippetNames) {
            Integer count = map.get(name);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(name, count);
        }

        return map;
    }
}
