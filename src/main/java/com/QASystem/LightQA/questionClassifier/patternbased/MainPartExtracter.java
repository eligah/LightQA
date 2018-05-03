package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.parser.WordParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;

import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import org.apdplat.word.segmentation.Word;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPartExtracter {
    private static final Logger LOG = LoggerFactory.getLogger(MainPartExtracter.class);
    private static final LexicalizedParser LP;
    private static final GrammaticalStructureFactory GSF;

    static {
        String models = "models/chineseFactored.ser.gz";
        LOG.info("Model: " + models);
        LP = LexicalizedParser.loadModel();

        TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
        GSF = tlp.grammaticalStructureFactory();
    }

    public String getMainPart(String question, String questionWords) {

    }
    public String getMainPart(String question) {
        question = question.replace("\\s+", "");
        String questionWords = questionParse(question);
        return getMainPart(question, questionWords);
    }

    private String questionParse(String question) {
        LOG.info("Start to word-segementation: " + question);
        List<Word> words = WordParser.parse(question);
        StringBuilder wordStr = new StringBuilder();
        for (Word word : words) {
            wordStr.append(word.getText()).append(" ");
        }
        LOG.info("")
    }

    public String getQuestionMainPartNaturePattern(String question, String mainPart) {
        Map<String, String> map = new HashMap<>();

        List<Word> words = WordParser.parse(question);
        for (Word word: words) {
            map.put(word.getText(), word.getPartOfSpeech().getPos()); // TODO getPartOfSpeech().getPos meaning.
        }
        StringBuilder patterns = new StringBuilder();
        String[] items = mainPart.split(" ");
        int i = 0;
        for (String item : items) {
            if ((i++) > 0) {
                patterns.append("/");
            }
            patterns.append(map.get(item));
        }
        return patterns.toString().trim();
    }
}
