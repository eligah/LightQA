package com.QASystem.LightQA.system;

import com.QASystem.LightQA.datasource.DataSource;
import com.QASystem.LightQA.model.CandidateAnswer;
import com.QASystem.LightQA.model.CandidateAnswerCollection;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.questionClassifier.QuestionClassifier;
import com.QASystem.LightQA.score.answer.CandidateAnswerScore;
import com.QASystem.LightQA.score.evidence.EvidenceScore;
import com.QASystem.LightQA.select.CandidateAnswerSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnsweringSystemImpl implements QuestionAnsweringSystem{

    private static final Logger LOG = LoggerFactory.getLogger(QuestionAnsweringSystemImpl.class);

    private int questionIndex = 1;
    private double mrr;

    private final List<Question> perfectQuestions = new ArrayList<>();
    private final List<Question> notPerfectQuestions = new ArrayList<>();
    private final List<Question> wrongQuestions = new ArrayList<>();
    private final List<Question> unknownTypeQuestions = new ArrayList<>();

    private QuestionClassifier questionClassifier;
    private DataSource dataSource;
    private CandidateAnswerSelect candidateAnswerSelect;
    private EvidenceScore evidenceScore;
    private CandidateAnswerScore candidateAnswerScore;

    @Override
    public QuestionClassifier getQuestionClassifier() {
        return questionClassifier;
    }

    @Override
    public void setQuestionClassifier(QuestionClassifier questionClassifier) {
        this.questionClassifier = questionClassifier;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CandidateAnswerSelect getCandidateAnswerSelect() {
        return candidateAnswerSelect;
    }

    @Override
    public void setCandidateAnswerSelect(CandidateAnswerSelect candidateAnswerSelect) {
        this.candidateAnswerSelect = candidateAnswerSelect;
    }

    @Override
    public EvidenceScore getEvidenceScore() {
        return evidenceScore;
    }

    @Override
    public void setEvidenceScore(EvidenceScore evidenceScore) {
        this.evidenceScore = evidenceScore;
    }

    @Override
    public CandidateAnswerScore getCandidateAnswerScore() {
        return candidateAnswerScore;
    }

    @Override
    public void setCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore) {
        this.candidateAnswerScore = candidateAnswerScore;
    }

    @Override
    public List<Question> answerQuestions() {
        return dataSource.getAndAnswerQuestions(this);
    }

    @Override
    public Question answerQuestion(String questionStr) {
        Question question = dataSource.getQuestion(questionStr);
        if (question != null ){
            return answerQuestion(question);
        }
        return null;
    }

    @Override
    public Question answerQuestion(Question question) {
        if (question != null) {
            List<Question> questions = new ArrayList<>();
            questions.add(question);
            return answerQuestions(questions).get(0);
        }
        return null;
    }

    @Override
    public List<Question> answerQuestions(List<Question> questions) {
        for (Question question: questions) {
            question = questionClassifier.classify(question);
            LOG.info("Start to answer question " + (questionIndex++) + "：" + question.getQuestion() + " [ questionType： " + question.getQuestionType() + " ]");
            if(question.getQuestionType() == null) {
                unknownTypeQuestions.add(question);
                wrongQuestions.add(question);
                LOG.error("Unknown question type, refuse to answer. ");
                continue;
            }
            int i = 1;
            for (Evidence evidence: question.getEvidences()) {
                LOG.debug("Start to process evidence " + (i++));
                evidenceScore.score(question,evidence);

                LOG.debug("Evidence detail.");
                LOG.debug("Title: " +  evidence.getTitle());
                LOG.debug("Snippets: " +  evidence.getSnippet());
                LOG.debug("Scores: " +  evidence.getScore());
                LOG.debug("Words: " +  evidence.getWords());

                candidateAnswerSelect.select(question, evidence);

                CandidateAnswerCollection candidateAnswerCollection = evidence.getCandidateAnswerCollection();

                if (!candidateAnswerCollection.isEmpty()) {
                    LOG.debug("Candidate answers of evidence ( not scored ) ");
                    candidateAnswerCollection.showAll();
                    candidateAnswerScore.score(question, evidence, candidateAnswerCollection);
                    LOG.debug("Candidate answers of evidence ( has scored ) ");
                    candidateAnswerCollection.showAll();
                    LOG.debug("");
                } else {
                    LOG.debug("No candidate answer for this question");
                }
                LOG.debug("");
            }
            LOG.info("************************************");
            LOG.info("************************************");
            LOG.info("Question: " + question.getQuestion());
            LOG.info("Candidate Answer: ");
            for (CandidateAnswer candidateAnswer : question.getAllCandidateAnswer()){
                LOG.info(candidateAnswer.getAnswer() + " " + candidateAnswer.getScore());
            }
            int rank = question.getExpectAnswerRank();
            LOG.info("Expect answer rank: " + rank);
            LOG.info("");
            if (rank == 1){
                perfectQuestions.add(question);
            }
            if  (rank >= 1){
                notPerfectQuestions.add(question);
            }
            if (rank == -1) {
                wrongQuestions.add(question);
            }
            if (rank >= 0 ){
                mrr += (double) 1 / rank;
            }
            LOG.info("mrr: " + mrr);
            LOG.info("perfectCount: " + getPerfectCount());
            LOG.info("notPerfectCount: " + getNotPerfectCount());
            LOG.info("wrongCount: " + getWrongCount());
            LOG.info("unknownTypeCount: " + getUnknownTypeCount());
            LOG.info("questionCount: " + getQuestionCount());

        }
        LOG.info("");

        LOG.info("MRR：" + getMRR() * 100 + "%");
        LOG.info("回答完美率：" + (double) getPerfectCount() / getQuestionCount() * 100 + "%");
        LOG.info("回答不完美率：" + (double) getNotPerfectCount() / getQuestionCount() * 100 + "%");
        LOG.info("回答错误率：" + (double) getWrongCount() / getQuestionCount() * 100 + "%");
        LOG.info("未知类型率：" + (double) getUnknownTypeCount() / getQuestionCount() * 100 + "%");

        LOG.info("");

        return questions;
    }

    @Override
    public void showPerfectQuestions() {
        LOG.info("Show perfect questions");
        int i =1;
        for(Question question : perfectQuestions) {
            LOG.info((i++) + ": " + question.getQuestion() + " : " + question.getExpectAnswerRank());
        }
    }

    @Override
    public void showNotPerfectQuestions() {
        LOG.info("Show not perfect questions");
        int i =1;
        for(Question question : notPerfectQuestions) {
            LOG.info((i++) + ": " + question.getQuestion() + " : " + question.getExpectAnswerRank());
        }
    }

    @Override
    public void showWrongQuestions() {
        LOG.info("Show wrong questions");
        int i =1;
        for(Question question : wrongQuestions) {
            LOG.info((i++) + ": " + question.getQuestion() + " : " + question.getExpectAnswerRank());
        }
    }

    @Override
    public void showUnknownTypeQuestions() {
        LOG.info("Show unknown type questions");
        int i =1;
        for(Question question : unknownTypeQuestions) {
            LOG.info((i++) + ": " + question.getQuestion() + " : " + question.getExpectAnswerRank());
        }
    }

    @Override
    public List<Question> getPerfectQuestions() {
        return perfectQuestions;
    }

    @Override
    public List<Question> getNotPerfectQuestions() {
        return notPerfectQuestions;
    }

    @Override
    public List<Question> getWrongQuestions() {
        return wrongQuestions;
    }

    @Override
    public List<Question> getUnknownTypeQuestions() {
        return unknownTypeQuestions;
    }

    @Override
    public double getMRR() {
        return (double) mrr / getQuestionCount();
    }

    @Override
    public int getQuestionCount() {
        return getPerfectCount() + getNotPerfectCount() + getWrongCount();
    }

    @Override
    public int getPerfectCount() {
        return perfectQuestions.size();
    }

    @Override
    public int getNotPerfectCount() {
        return notPerfectQuestions.size();
    }

    @Override
    public int getWrongCount() {
        return wrongQuestions.size();
    }

    @Override
    public int getUnknownTypeCount() {
        return unknownTypeQuestions.size();
    }

//    public static void main(String[] args) {
//        //1、默认评分组件权重
//        ScoreWeight scoreWeight = new ScoreWeight();
//
//        //2、问答系统数据源（人名文件数据源）
//        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
//
//        //3、候选答案提取器(不可以同时使用多个提取器)
//        CandidateAnswerSelect candidateAnswerSelect = new CommonCandidateAnswerSelect();
//
//        //4、证据评分组件(可以同时使用多个组件)
//        //***********************
//        //4.1、TermMatch评分组件
//        EvidenceScore termMatchEvidenceScore = new TermMatchEvidenceScore();
//        termMatchEvidenceScore.setScoreWeight(scoreWeight);
//        //4.2、二元模型评分组件
//        EvidenceScore bigramEvidenceScore = new BigramEvidenceScore();
//        bigramEvidenceScore.setScoreWeight(scoreWeight);
//        //4.3、跳跃二元模型评分组件
//        EvidenceScore skipBigramEvidenceScore = new SkipBigramEvidenceScore();
//        skipBigramEvidenceScore.setScoreWeight(scoreWeight);
//        //4.4、组合证据评分组件
//        CombinationEvidenceScore combinationEvidenceScore = new CombinationEvidenceScore();
//        combinationEvidenceScore.addEvidenceScore(termMatchEvidenceScore);
//        combinationEvidenceScore.addEvidenceScore(bigramEvidenceScore);
//        combinationEvidenceScore.addEvidenceScore(skipBigramEvidenceScore);
//
//        //5、候选答案评分组件(可以同时使用多个组件)
//        //***********************
//        //5.1、词频评分组件
//        CandidateAnswerScore termFrequencyCandidateAnswerScore = new TermFrequencyCandidateAnswerScore();
//        termFrequencyCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.2、词距评分组件
//        CandidateAnswerScore termDistanceCandidateAnswerScore = new TermDistanceCandidateAnswerScore();
//        termDistanceCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.3、词距评分组件(只取候选词和问题词的最短距离)
//        CandidateAnswerScore termDistanceMiniCandidateAnswerScore = new TermDistanceMiniCandidateAnswerScore();
//        termDistanceMiniCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.4、文本对齐评分组件
//        CandidateAnswerScore textualAlignmentCandidateAnswerScore = new TextualAlignmentCandidateAnswerScore();
//        textualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.5、文本对齐评分组件
//        CandidateAnswerScore moreTextualAlignmentCandidateAnswerScore = new MoreTextualAlignmentCandidateAnswerScore();
//        moreTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.6、回带文本对齐评分组件
//        CandidateAnswerScore rewindTextualAlignmentCandidateAnswerScore = new RewindTextualAlignmentCandidateAnswerScore();
//        rewindTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.7、热词评分组件
//        CandidateAnswerScore hotCandidateAnswerScore = new HotCandidateAnswerScore();
//        hotCandidateAnswerScore.setScoreWeight(scoreWeight);
//        //5.8、组合候选答案评分组件
//        CombinationCandidateAnswerScore combinationCandidateAnswerScore = new CombinationCandidateAnswerScore();
//        combinationCandidateAnswerScore.addCandidateAnswerScore(termFrequencyCandidateAnswerScore);
//        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceCandidateAnswerScore);
//        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceMiniCandidateAnswerScore);
//        combinationCandidateAnswerScore.addCandidateAnswerScore(textualAlignmentCandidateAnswerScore);
//        combinationCandidateAnswerScore.addCandidateAnswerScore(moreTextualAlignmentCandidateAnswerScore);
//        //combinationCandidateAnswerScore.addCandidateAnswerScore(rewindTextualAlignmentCandidateAnswerScore);
//        combinationCandidateAnswerScore.addCandidateAnswerScore(hotCandidateAnswerScore);
//
//        QuestionAnsweringSystem questionAnsweringSystem = new QuestionAnsweringSystemImpl();
//        questionAnsweringSystem.setDataSource(dataSource);
//        questionAnsweringSystem.setCandidateAnswerSelect(candidateAnswerSelect);
//        questionAnsweringSystem.setEvidenceScore(combinationEvidenceScore);
//        questionAnsweringSystem.setCandidateAnswerScore(combinationCandidateAnswerScore);
//        questionAnsweringSystem.answerQuestions();
//    }
}

