package com.QASystem.LightQA.score.evidence;

import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.ScoreWeight;
import com.QASystem.LightQA.util.Tools;
import com.sun.corba.se.impl.oa.toa.TOA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//对于问题和证据的词进行直接匹配，title 出现一次记 2/idf
//snippet 出现一次记 1/idf 分
public class TermMatchEvidenceScore implements EvidenceScore {

    private static final Logger LOG = LoggerFactory.getLogger(TermMatchEvidenceScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void score(Question question, Evidence evidence) {
        LOG.debug("*************************");
        LOG.debug("Evidence TermMatch is scoring.");

        List<String> questionTerms = question.getWords();
        LOG.debug("questionTerms:" + questionTerms);

        List<String> titleTerms = evidence.getTitleWords();
        List<String> snippetTerms = evidence.getSnippetWords();
        LOG.debug("titleTerms:" + titleTerms);
        LOG.debug("snippetTerms:" + snippetTerms);
        double score = 0;
        for (String questionTerm : questionTerms) {
            if (questionTerm.length() < 2) {
                //ignore word which length < 2
                continue;
            }
            int idf = Tools.getIDF(questionTerm);
            if (idf > 0) {
                idf = 1 / idf;
            } else {
                idf = 1;
            }

            for (String titleTerm : titleTerms) {
                if (questionTerm.equals(titleTerm)) {
                    score += 2 * idf;
                }
            }
            for (String snippetTerm : snippetTerms) {
                if (questionTerm.equals(snippetTerm)) {
                    score += idf;
                }
            }
        }
        score *= scoreWeight.getTermMatchEvidenceScoreWeight();
        LOG.debug("Evidence TermMatch score:" + score);
        evidence.addScore(score);
        LOG.debug("Evidence TermMatch scoring have finished");
        LOG.debug("*************************");
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
