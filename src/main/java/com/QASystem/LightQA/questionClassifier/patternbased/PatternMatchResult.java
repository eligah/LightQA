package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.model.QuestionType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import java.util.*;

import com.QASystem.LightQA.questionClassifier.patternbased.PatternMatchResultItem;
public class PatternMatchResult {

    private static final Logger LOG = LoggerFactory.getLogger(PatternMatchResult.class);

    private final Map<QuestionTypePatternFile, List<PatternMatchResultItem>> map= new HashMap<>();

    public List<QuestionTypePatternFile> getQuestionTypePatternFilesFromLooseToCompact() {
        LOG.info("From loose to compact: ");
        return fromCompactToLoose(false);
    }

    public List<QuestionTypePatternFile> getQuestionTypePatternFilesFromCompactToLoose() {
        LOG.info("From compact to loose");
        return fromCompactToLoose(true);
    }

    private List<QuestionTypePatternFile> fromCompactToLoose(boolean b) {
        Map<String, QuestionTypePatternFile> tempMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (QuestionTypePatternFile file: map.keySet()) {
            list.add(file.getFile());
            tempMap.put(file.getFile(), file);
        }
        Collections.sort(list);
        if(b == false) {
            Collections.reverse(list);
        }
        List<QuestionTypePatternFile> result = new ArrayList<>();
        for (String item: list) {
            result.add(tempMap.get(item));
        }
        return result;
    }

    public List<PatternMatchResultItem> getPatternMatchResult(QuestionTypePatternFile file) {
        return map.get(file);
    }

    public void addPatternMatchResult(QuestionTypePatternFile file, List<PatternMatchResultItem> items) {
        List<PatternMatchResultItem> value = map.get(file);
        if(value == null) {
            value = items;
        } else {
            value.addAll(items);
        }
        map.put(file,value);
    }


    public List<PatternMatchResultItem> getAllPatternMatchResult() {
        List<PatternMatchResultItem> value = new ArrayList<>();
        for (List<PatternMatchResultItem> v : map.values()) {
            value.addAll(v);
        }
        return value;
    }
}
