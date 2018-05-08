package com.QASystem.LightQA.questionClassifier.patternbased;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import com.QASystem.LightQA.questionClassifier.patternbased.MainPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestionStructure {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionStructure.class);
    private String question;
    private MainPart mainPart;
    private String mainPartForTop = null;
    private String mainPartForRoot = null;

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
        if(mainPart!=null) {
            return mainPart.toString();
        } else if (!mainPart.getResult().equals("")) {
            return mainPart.getResult();
        } else {
            LOG.debug("MainPart have not extracted");
            return "";
        }
    }

    public void setMainPart(String mp) {
        this.mainPart = new MainPart();
        this.mainPart.setResult(mp);
    }

    public void setMainPart(MainPart mainPart) {
        this.mainPart = mainPart;
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
