package com.QASystem.LightQA.score.evidence;

import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;
import com.QASystem.LightQA.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SkipBigramEvidenceScore implements EvidenceScore{
    private static final Logger LOG = LoggerFactory.getLogger(TermMatchEvidenceScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void score(Question question, Evidence evidence) {
        LOG.debug("*************************");
        LOG.debug("Evidence skip-1-Bi-gram is scoring. ");

        List<String> questionTerms = question.getWords();

        List<String> patterns = new ArrayList<>();
        for (int i = 0; i < questionTerms.size() -2; i++) {
            String pattern = questionTerms.get(i) +"." +questionTerms.get(i+2);
            patterns.add(pattern);
        }

        String text = evidence.getTitle()+ evidence.getSnippet();
        double score = 0;
        for (String pattern : patterns) {
            int count = Tools.countsForSkipbigram(text, pattern);
            if(count > 0 ) {
                LOG.debug("pattern: " + pattern + " finded in evidence " + count + " times.");
                score += count*2;
            }
        }
        score *=scoreWeight.getSkipBigramEvidenceScoreWeight();
        LOG.debug("Evidence skip-1-Bi-gram score:" + score);
        evidence.addScore(score);
        LOG.debug("Evidence skip-1-Bi-gram scoring have finished");
        LOG.debug("*************************");
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
