package com.QASystem.LightQA.model;

import java.util.ArrayList;
import java.util.List;

import com.QASystem.LightQA.parser.WordParser;
import org.apdplat.word.segmentation.Word;

public class Evidence {
    private String title;
    private String snippet;
    private double score = 1.0;
    private CandidateAnswerCollection candidateAnswerCollection;

    public List<String> getTitleWords(){
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(title);

        for (Word word : words){
            result.add(word.getText());
        }
        return result;
    }

    public List<String> getSnippetWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(snippet);

        for(Word word: words){
            result.add(word.getText());
        }
        return result;
    }

    public List<String> getWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(title + snippet);
        for (Word word : words){
            result.add(word.getText());
        }
        return result;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getSnippet() { return snippet; }

    public void setSnippet(String snippet) { this.snippet = snippet; }

    public double getScore() { return score; }

    public void addScore(double inc) { score += inc; }

    public CandidateAnswerCollection getCandidateAnswerCollection() { return candidateAnswerCollection; }

    public void setCandidateAnswerCollection(CandidateAnswerCollection canAnsw) { this.candidateAnswerCollection = canAnsw; }
}
