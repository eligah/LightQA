package com.QASystem.LightQA.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.QASystem.LightQA.filter.CandidateAnswerFilter;
import com.QASystem.LightQA.filter.CandidateAnswerFilterInQuestion;
import com.QASystem.LightQA.parser.WordParser;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Question {

    private static final Logger LOG = LoggerFactory.getLogger(Question.class);
    private String question;
    private List<Evidence> evidences = new ArrayList<>();

    private QuestionType questionType = QuestionType.PERSON_NAME;
    private String expectAnswer;
    private CandidateAnswerFilter candidateAnswerFilter = new CandidateAnswerFilterInQuestion();

    private Set<QuestionType> candidateQuestionTypes = new HashSet<>();

    public String getQuestion() { return question; }

    public void setQuestion(String question) { this.question = question; }

    public List<Evidence> getEvidences() { return this.evidences; }

    public void addEvidence(Evidence evidence) { this.evidences.add(evidence); }

    public void addEvidences(List<Evidence> evidences) { this.evidences.addAll(evidences); }

    public void removeEvidence(Evidence evidence) {this.evidences.remove(evidence); }

    public void clearCandidateQuestionType() { candidateQuestionTypes.clear(); }

    public void addCandidateQuestionType(QuestionType questionType) { candidateQuestionTypes.add(questionType); }

    public void removeCandidateQuestionType(QuestionType questionType) {candidateQuestionTypes.remove(questionType); }

    public Set<QuestionType> getCandidateQuestionTypes() {
        return candidateQuestionTypes;
    }

    public List<CandidateAnswer> getAllCandidateAnswer() {
        //TODO
        return null;
    }

    public int getExpectAnswerRank(){
        if (expectAnswer == null){
            LOG.info("No expecting answer. ");
            return -2;
        }
        //TODO
        return 1;
    }

    public List<String> getWords(){
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(question.replace("?","").replace("？", ""));
        for(Word word : words){
            result.add(word.getText());
        }
        return result;
    }

    public String getText(){
        StringBuilder text = new StringBuilder();
        for(Evidence evidence : evidences){
            text.append(evidence.getTitle()).append(evidence.getSnippet());
        }
        return text.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("?. ").append(question).append("\n\n");
        for (Evidence evidence : evidences){
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("snippet: ").append(evidence.getSnippet()).append("\n\n");
        }
        return result.toString();
    }


    public String getExpectAnswer() { return expectAnswer; }

    public void setExpectAnswer(String expectAnswer) {
        this.expectAnswer = expectAnswer;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public CandidateAnswerFilter getCandidateAnswerFilter() {
        return candidateAnswerFilter;
    }

    public void setCandidateAnswerFilter(CandidateAnswerFilter candidateAnswerFilter) {
        this.candidateAnswerFilter = candidateAnswerFilter;
    }

}