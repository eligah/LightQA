package com.QASystem.LightQA.model;


public class CandidateAnswer implements Comparable<CandidateAnswer> {

    private String answer;
    private double score = 1.0;

    public String getAnswer() { return this.answer; }

    public void setAnswer(String answer) { this.answer = answer; }

    public double getScore(){ return score; }

    public void setScore(double score) { this.score = score; }

    public void addScore(double score){ this.score += score; }

    @Override
    public int compareTo(CandidateAnswer o) {
        if (o != null && o instanceof CandidateAnswer) {
            CandidateAnswer a = (CandidateAnswer) o;
            if (this.score < a.score)
                return -1;
            else if (this.score > a.score)
                return 1;
            else
                return 0;
        }
        throw new RuntimeException("Cannot compare these answer");
    }

    @Override
    public int hashCode() { return this.answer.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CandidateAnswer)) {
            return false;
        }
        CandidateAnswer a = (CandidateAnswer) obj;
        return this.answer.equals(a.answer);
    }
}

