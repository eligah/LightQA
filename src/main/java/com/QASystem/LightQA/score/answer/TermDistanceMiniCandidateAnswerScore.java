package com.QASystem.LightQA.score.answer;

import com.QASystem.LightQA.model.CandidateAnswer;
import com.QASystem.LightQA.model.CandidateAnswerCollection;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;
import com.QASystem.LightQA.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TermDistanceMiniCandidateAnswerScore implements CandidateAnswerScore {
    private static final Logger LOG = LoggerFactory.getLogger(TermDistanceMiniCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Term Minimize distance candidate answer score starts.");
        //1、对问题进行分词
        List<String> questionTerms = question.getWords();
        //2、对证据进行分词
        List<Word> evidenceWords = Tools.getWords(evidence.getTitle() + "," + evidence.getSnippet());
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            int distance = 0;
            LOG.debug("Get candidate answer: " + candidateAnswer.getAnswer() + " term mini distance.");
            List<Integer> candidateAnswerOffes = new ArrayList<>();
            for (int i = 0; i < evidenceWords.size(); i++) {
                Word evidenceWord = evidenceWords.get(i);
                if (evidenceWord.getText().equals(candidateAnswer.getAnswer())) {
                    candidateAnswerOffes.add(i);
                }
            }
            for (String questionTerm : questionTerms) {
                List<Integer> questionTermOffes = new ArrayList<>();
                for (int i =0; i < evidenceWords.size(); i++) {
                    Word evidenceWord = evidenceWords.get(i);
                    if (evidenceWord.getText().equals(questionTerm)) {
                        questionTermOffes.add(i);
                    }
                }
                int miniDistance = Integer.MAX_VALUE;
                for (int candidateAnswerOff : candidateAnswerOffes) {
                    for (int questionTermOff : questionTermOffes) {
                        int abs = Math.abs(candidateAnswerOff - questionTermOff);
                        if (miniDistance > abs) {
                            miniDistance = abs;
                        }
                    }
                }
                if (miniDistance != Integer.MAX_VALUE) {
                    distance += miniDistance;
                }
            }
            double score = candidateAnswer.getScore() / distance;
            score *= scoreWeight.getTermDistanceCandidateAnswerScoreWeight();
            LOG.debug("Word mini-distance: " + distance + " Score: " + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("Term mini-distance candidate answer score have finished");
        LOG.debug("*************************");
    }
}
