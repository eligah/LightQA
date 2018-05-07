package com.QASystem.LightQA.model;

import java.util.*;

import com.QASystem.LightQA.filter.CandidateAnswerFilter;
import com.QASystem.LightQA.filter.CandidateAnswerFilterInQuestion;
import com.QASystem.LightQA.parser.WordParser;
import com.QASystem.LightQA.util.Tools;
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
        Map<String, Double> map = new HashMap<>();
        for(Evidence evidence : evidences) {
            for (CandidateAnswer candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                Double score = map.get(candidateAnswer.getAnswer());
                Double candidateAnswerFinalScore = candidateAnswer.getScore() + evidence.getScore();
                if(score == null) {
                    score = candidateAnswerFinalScore;
                } else {
                    score += candidateAnswerFinalScore;
                }
                map.put(candidateAnswer.getAnswer(), score);
            }
        }

        List<CandidateAnswer> candidateAnswers = new ArrayList<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String answer = entry.getKey();
            Double score = entry.getValue();
            if (answer != null && score != null && score >0 && score < Double.MAX_VALUE) {
                CandidateAnswer candidateAnswer= new CandidateAnswer();
                candidateAnswer.setAnswer(answer);
                candidateAnswer.setScore(score);
                candidateAnswers.add(candidateAnswer);
            }
        }
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);

        if (candidateAnswerFilter != null) {
            candidateAnswerFilter.filter(this,candidateAnswers);
        }

        //normalized
        if (candidateAnswers.size() >0 ) {
            double baseScore = candidateAnswers.get(0).getScore();
            for (CandidateAnswer candidateAnswer: candidateAnswers) {
                double score = candidateAnswer.getScore() / baseScore;
                candidateAnswer.setScore(score);
            }
        }
        return candidateAnswers;
    }

    public int getExpectAnswerRank(){
        if (expectAnswer == null){
            LOG.info("No expecting answer. ");
            return -2;
        }
        List<CandidateAnswer> candidateAnswers = this.getAllCandidateAnswer();
        for (int i = 0; i < candidateAnswers.size(); i++) {
            CandidateAnswer candidateAnswer = candidateAnswers.get(i);
            if ( candidateAnswer.getAnswer().trim().equals(expectAnswer.trim())){
                return i+1;
            }
        }
        return -1;
    }

    public List<CandidateAnswer> getTopNCandidnateAnswer(int topN) {
        List<CandidateAnswer> topNCandidateAnswer = new ArrayList<>();
        List<CandidateAnswer> allCandidateAnswer = getAllCandidateAnswer();
        if( topN > allCandidateAnswer.size()) {
            topN = allCandidateAnswer.size();
        }
        for (int i = 0; i < topN ; i++) {
            topNCandidateAnswer.add(allCandidateAnswer.get(i));
        }
        return topNCandidateAnswer;

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

    public String toString(int index) {
        StringBuilder result = new StringBuilder();
        result.append("?").append(index).append(". ").append(question).append("\n\n");
        for (Evidence evidence : this.evidences) {
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("Snippet: ").append(evidence.getSnippet()).append("\n\n");
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

    public Map.Entry<String,Integer> getHot() {
        List<String> questionWords = getWords();
        Map<String, Integer> map = new HashMap<>();
        List<Word> words = WordParser.parse(getText());
        for (Word word : words) {
            Integer count = map.get(word.getText());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(word.getText(), count);
        }
        Map<String, Integer> questionMap = new HashMap<>();
        for(String questionWord : questionWords) {
            Integer count = map.get(questionWord);
            if (questionWord.length() > 1 && count != null) {
                questionMap.put(questionWord, count);
                LOG.debug("Hot word statistic: " + questionWord + " " + map.get(questionWord));
            }
        }
        List<Map.Entry<String, Integer>> list = Tools.sortByIntegerValue(questionMap);
        Collections.reverse(list);
        if(! list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}