package com.QASystem.LightQA.score.answer;

import com.QASystem.LightQA.model.*;
import com.QASystem.LightQA.system.ScoreWeight;

import com.QASystem.LightQA.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextualAlignmentCandidateAnswerScore implements CandidateAnswerScore {
    private static final Logger LOG = LoggerFactory.getLogger(TextualAlignmentCandidateAnswerScore.class);

    protected List<String> getQuestionWords(Question q) {
        return q.getWords();
    }

    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("Text align candidate answer score start.");

        List<String> questionTerms = getQuestionWords(question);
        int questionTermsSize = questionTerms.size();
        String evidenceText = evidence.getTitle() + evidence.getSnippet();

        //将每一个候选答案都放到问题的每一个位置，查找在证据中是否有匹配文本
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            LOG.debug("Caculate text align of question: " + candidateAnswer.getAnswer());
            for (int i = 0; i < questionTermsSize; i++ ) {
                StringBuilder textAlignment = new StringBuilder();
                for (int j = 0; j < questionTermsSize; j++) {
                    if (j == i) {
                        textAlignment.append(candidateAnswer.getAnswer());
                    } else {
                        textAlignment.append(questionTerms.get(j));
                    }
                }
                String textualAlignmentPattern = textAlignment.toString();
                if(question.getQuestion().trim().equals(textualAlignmentPattern.trim())) {
                    LOG.debug("The pattern is the same as the origin question.");
                    continue;
                }

                List<Word> textualAlignmentPatternTerms = Tools.getWords(textualAlignmentPattern);
                List<String> patterns = new ArrayList<>();
                patterns.add(textualAlignmentPattern);
                StringBuilder str = new StringBuilder();
                int len = textualAlignmentPatternTerms.size();
                for (int t = 0; t < len; t++) {
                    str.append(textualAlignmentPatternTerms.get(t).getText());
                    if (t < len - 1) {
                        str.append(".{0,5}");
                    }
                }
                patterns.add(str.toString());

                // 检查文本对齐 matching.
                int count = 0;
                int length = 0;
                for(String pattern: patterns) {
                    Pattern p = Pattern.compile(pattern);
                    Matcher matcher = p.matcher(evidenceText);
                    while(matcher.find()) {
                        String text = matcher.group();
                        LOG.debug("The align text in evidence: "+text);
                        LOG.debug("The align pattern: " + pattern);
                        count++;
                        length += text.length();
                    }
                }
                //Scoring
                if(count > 0) {
                    double avgLen = (double) length / count;
                    int questionLen = question.getQuestion().length();
                    double score = questionLen / avgLen;
                    score *= scoreWeight.getTextualAlignmentCandidateAnswerScoreWeight();
                    candidateAnswer.addScore(score);
                    LOG.debug("Text align" + count + " time, score: " +score);
                }

            }
            LOG.debug("Textual alignment candidate answer scoring have finished.");
            LOG.debug("*************************");
        }
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
