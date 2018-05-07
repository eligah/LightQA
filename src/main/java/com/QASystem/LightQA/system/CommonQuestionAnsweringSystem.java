package com.QASystem.LightQA.system;

import com.QASystem.LightQA.datasource.DataSource;
import com.QASystem.LightQA.datasource.FileDataSource;
import com.QASystem.LightQA.files.FilesConfig;
import com.QASystem.LightQA.model.CandidateAnswer;
import com.QASystem.LightQA.questionClassifier.QuestionClassifier;
import com.QASystem.LightQA.questionClassifier.patternbased.*;
import com.QASystem.LightQA.score.evidence.EvidenceScore;
import com.QASystem.LightQA.select.CandidateAnswerSelect;
import com.QASystem.LightQA.select.CommonCandidateAnswerSelect;
import com.QASystem.LightQA.system.QuestionAnsweringSystemImpl;
import com.QASystem.LightQA.score.evidence.*;
import com.QASystem.LightQA.score.answer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommonQuestionAnsweringSystem extends QuestionAnsweringSystemImpl {

    private static final Logger LOG = LoggerFactory.getLogger(CommonQuestionAnsweringSystem.class);

    public CommonQuestionAnsweringSystem() {
        LOG.info("Start to build lightQA");

        //设置问题分类器
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");
        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();
        QuestionClassifier questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);
        super.setQuestionClassifier(questionClassifier);

        //设置数据源
        List<String> files = new ArrayList<>();
        files.add(FilesConfig.personNameMaterial);
        files.add(FilesConfig.locationNameMaterial);
        files.add(FilesConfig.organizationNameMaterial);
        files.add(FilesConfig.numberMaterial);
        files.add(FilesConfig.timeMaterial);

        DataSource dataSource = new FileDataSource(files);
        super.setDataSource(dataSource);

        //证据评分组件组合
        ScoreWeight scoreWeight = new ScoreWeight();
        EvidenceScore termMatchEvidenceScore = new TermMatchEvidenceScore();
        termMatchEvidenceScore.setScoreWeight(scoreWeight);

        EvidenceScore bigramEvidenceScore = new BigramEvidenceScore();
        bigramEvidenceScore.setScoreWeight(scoreWeight);

        EvidenceScore skipBigramEvidenceScore = new SkipBigramEvidenceScore();
        skipBigramEvidenceScore.setScoreWeight(scoreWeight);

        CombinationEvidenceScore combinationEvidenceScore = new CombinationEvidenceScore();
        combinationEvidenceScore.addEvidenceScore(termMatchEvidenceScore);
        combinationEvidenceScore.addEvidenceScore(bigramEvidenceScore);
        combinationEvidenceScore.addEvidenceScore(skipBigramEvidenceScore);
        super.setEvidenceScore(combinationEvidenceScore);

        //候选答案提取器
        CandidateAnswerSelect candidateAnswerSelect = new CommonCandidateAnswerSelect();
        super.setCandidateAnswerSelect(candidateAnswerSelect);

        //候选答案评分组件组合
        CandidateAnswerScore termFrequencyCandidateAnswerScore = new TermFrequencyCandidateAnswerScore();
        termFrequencyCandidateAnswerScore.setScoreWeight(scoreWeight);

        CandidateAnswerScore termDistanceCandidateAnswerScore = new TermDistanceCandidateAnswerScore();
        termDistanceCandidateAnswerScore.setScoreWeight(scoreWeight);

        CandidateAnswerScore termDistanceMiniCandidateAnswerScore = new TermDistanceMiniCandidateAnswerScore();
        termDistanceMiniCandidateAnswerScore.setScoreWeight(scoreWeight);

        CandidateAnswerScore textualAlignmentCandidateAnswerScore = new TextualAlignmentCandidateAnswerScore();
        textualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);

        CandidateAnswerScore moreTextualAlignmentCandidateAnswerScore = new MoreTextualAlignmentCandidateAnswerScore();
        moreTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);

        CandidateAnswerScore hotCandidateAnswerScore = new HotCandidateAnswerScore();
        hotCandidateAnswerScore.setScoreWeight(scoreWeight);

        CombinationCandidateAnswerScore combinationCandidateAnswerScore = new CombinationCandidateAnswerScore();
        combinationCandidateAnswerScore.addCandidateAnswerScore(termFrequencyCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceMiniCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(textualAlignmentCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(moreTextualAlignmentCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(hotCandidateAnswerScore);

        super.setCandidateAnswerScore(combinationCandidateAnswerScore);


        LOG.info("LightQA has constructed.");
    }
    public static void main(String[] args) {
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        questionAnsweringSystem.answerQuestions();
    }
}
