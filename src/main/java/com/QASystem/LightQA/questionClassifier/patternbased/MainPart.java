package com.QASystem.LightQA.questionClassifier.patternbased;

import edu.stanford.nlp.trees.TreeGraphNode;

public class MainPart {
    private TreeGraphNode subject;

    private TreeGraphNode predicate;

    private TreeGraphNode object;

    private String result;

    public MainPart(TreeGraphNode subject, TreeGraphNode predicate, TreeGraphNode object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.result ="";
    }

    public MainPart() { result = "";}

    public MainPart(TreeGraphNode predicate) { this(null, predicate, null); }

    public boolean isNotDone() {
        return result.equals("");
    }

    public void done() {
        result = predicate.toString("value");
        if (subject != null) {
            result = subject.toString("value") + " " + result;
        }
        if (object != null) {
            result += " " + object.toString("value");
        }
        result.trim();
    }

    public void setSubject(TreeGraphNode subject) {
        this.subject = subject;
    }

    public void setPredicate(TreeGraphNode predicate) {
        this.predicate = predicate;
    }

    public void setObject(TreeGraphNode object) {
        this.object = object;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TreeGraphNode getSubject() {
        return subject;
    }

    public TreeGraphNode getPredicate() {
        return predicate;
    }

    public TreeGraphNode getObject() {
        return object;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        if (!result.equals("")) {
            return result;
        }
        return "";
    }
}
