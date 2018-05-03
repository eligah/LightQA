package com.QASystem.LightQA.questionClassifier.patternbased;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestionStructure {
    private String question;
    private String mainPart;
    private String mainPartForTop = null;
    private String mainPartForRoot = null;

    private List<String> dependencies = new ArrayList<>();
    private Collection<TypedDependency> tdls;
    private Tree tree;

    public boolean perfect() {
        if (mainPartForTop != null && mainPartForTop.equals(mainPartForRoot)) {
            return true;
        } else {
            return false;
        }

    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMainPart() {
        if (mainPart != null) {
            return mainPart;
        } else if (mainPartForTop != null){
            return mainPartForTop;
        }
        else {
            return mainPartForRoot;
        }
    }

    public void setMainPart(String mainPart) {
        this.mainPart = mainPart;
    }


    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Collection<TypedDependency> getTdls() {
        return tdls;
    }

    public void setTdls(Collection<TypedDependency> tdls) {
        this.tdls = tdls;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public String getMainPartForTop() {
        return mainPartForTop;
    }

    public void setMainPartForTop(String mainPartForTop) {
        this.mainPartForTop = mainPartForTop;
    }

    public String getMainPartForRoot() {
        return mainPartForRoot;
    }

    public void setMainPartForRoot(String mainPartForRoot) {
        this.mainPartForRoot = mainPartForRoot;
    }
}
