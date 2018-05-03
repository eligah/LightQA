package com.QASystem.LightQA.questionClassifier.patternbased;

import java.util.Objects;

public class QuestionTypePatternFile {

    private String file;
    private boolean multiMatch = true;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isMultiMatch() {
        return multiMatch;
    }

    public void setMultiMatch(boolean multiMatch) {
        this.multiMatch = multiMatch;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof QuestionTypePatternFile)) {
            return false;
        }
        final QuestionTypePatternFile temp = (QuestionTypePatternFile) o;
        if (! (this.file.equals(temp.file) && this.multiMatch == temp.multiMatch)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (file + multiMatch).hashCode();
    }
}
