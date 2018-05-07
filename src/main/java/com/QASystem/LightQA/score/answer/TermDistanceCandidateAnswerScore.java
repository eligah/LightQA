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

public class TermDistanceCandidateAnswerScore implements CandidateAnswerScore {
    private static final Logger LOG = LoggerFactory.getLogger(TermDistanceCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Term distance candidate answer score starts.");
        //1、对问题进行分词
        List<String> questionTerms = question.getWords();
        //2、对证据进行分词
        List<Word> evidenceWords = Tools.getWords(evidence.getTitle() + "," + evidence.getSnippet());
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            int distance = 0;
            LOG.debug("Get candidate answer: " + candidateAnswer.getAnswer() + " word distance.");
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
                for (int candidateAnswerOff : candidateAnswerOffes) {
                    for (int questionTermOff : questionTermOffes) {
                        distance += Math.abs(candidateAnswerOff - questionTermOff);
                    }
                }
            }
            double score = candidateAnswer.getScore() / distance;
            score *= scoreWeight.getTermDistanceCandidateAnswerScoreWeight();
            LOG.debug("Word distance: " + distance + " Score: " + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("Term distance candidate answer score have finished");
        LOG.debug("*************************");
    }
}
