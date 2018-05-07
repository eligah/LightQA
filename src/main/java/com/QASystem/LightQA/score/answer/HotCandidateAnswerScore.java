package com.QASystem.LightQA.score.answer;

import com.QASystem.LightQA.model.*;
import com.QASystem.LightQA.system.ScoreWeight;

import com.QASystem.LightQA.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HotCandidateAnswerScore implements CandidateAnswerScore {
    private static final Logger LOG = LoggerFactory.getLogger(HotCandidateAnswerScore.class);

    private ScoreWeight scoreWeight = new ScoreWeight();
    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Hot term score starts.");
        CandidateAnswer bestCandidateAnswer = null;
        int miniDistance = Integer.MAX_VALUE;
        List<Word> evidenceWords = Tools.getWords(evidence.getTitle() +"," + evidence.getSnippet());
        Map.Entry<String, Integer> hot = question.getHot();
        if (hot == null) {
            LOG.debug("Hot word not found.");
            return;
        }
        LOG.debug("Hotkey: " + hot.getKey() + " "+hot.getValue()+" times.");

        List<Integer> hotTermOffes = new ArrayList<>();
        for (int i = 0; i < evidenceWords.size(); i++) {
            Word evidenceWord = evidenceWords.get(i);
            if(evidenceWord.getText().equals(hot.getKey())) {
                hotTermOffes.add(i);
            }
        }
        for(CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            List<Integer> candidateAnswerOffes = new ArrayList<>();
            for (int i = 0; i < evidenceWords.size(); i++) {
                Word evidenceWord = evidenceWords.get(i);
                if(evidenceWord.getText().equals(candidateAnswer.getAnswer())) {
                    candidateAnswerOffes.add(i);
                }
            }

            for (int candidateAnswerOff : candidateAnswerOffes) {
                for (int hotTermOff : hotTermOffes) {
                    int abs = Math.abs(candidateAnswerOff - hotTermOff);
                    if (miniDistance > abs) {
                        miniDistance = abs;
                        bestCandidateAnswer = candidateAnswer;
                    }
                }
            }
        }
        if (bestCandidateAnswer != null && miniDistance > 0) {
            LOG.debug("Mini Distance: " + miniDistance);
            double score = bestCandidateAnswer.getScore();
            score *= scoreWeight.getHotCandidateAnswerScoreWeight();
            LOG.debug("Candidate answer: " + bestCandidateAnswer.getAnswer()+ " score: " + score);
            bestCandidateAnswer.addScore(score);
        } else {
            LOG.debug("No hot Candidate answer.");
        }
        LOG.debug("Hot term candidate answer scoring have finished.");
        LOG.debug("*************************");
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
