package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.parser.WordParser;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import org.apdplat.word.segmentation.Word;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

public class MainPartExtracter {
    private static final Logger LOG = LoggerFactory.getLogger(MainPartExtracter.class);
    private static final LexicalizedParser LP;
    private static final GrammaticalStructureFactory GSF;

    static {
        String models = "models/chineseFactored.ser.gz";
        LOG.info("Model: " + models);
        LP = LexicalizedParser.loadModel(models);

        TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
        GSF = tlp.grammaticalStructureFactory();
    }

    public QuestionStructure getMainPart(String question, String questionWords) {
        List<edu.stanford.nlp.ling.Word> words = new ArrayList<>();
        String[] qw = questionWords.split("\\s+");
        for (String item : qw) {
            item = item.trim();
            if (item.equals("")){
                continue;
            }
            words.add(new edu.stanford.nlp.ling.Word (item));
        }
        return getMainPart(question, words);
    }

    public QuestionStructure getMainPart(String question, List<edu.stanford.nlp.ling.Word> words) {
        QuestionStructure questionStructure = new QuestionStructure();
        questionStructure.setQuestion(question);

        Tree tree = LP.apply(words);
        LOG.info("Sentiment tree: ");
        tree.pennPrint();
        questionStructure.setTree(tree);

        GrammaticalStructure gs = GSF.newGrammaticalStructure(tree);
        if (gs == null) {
            return null;
        }
        Collection<TypedDependency> tdls = gs.typedDependenciesCCprocessed(true);
        questionStructure.setTdls(tdls);
        Map<String, String> map = new HashMap<>();
        String top = null;
        String root = null;

        List<String> dependencies = new ArrayList<>();
        for(TypedDependency tdl : tdls) {
            //TODO 处理依赖 转为string
            String item = tdl.toString();
            dependencies.add(item);
            LOG.info("\t" + item);
            if (item.startsWith("top")) {
                top = item;
            }
            if (item.startsWith("root")) {
                root = item;
            }
            int start = item.indexOf("(");
            int end = item.lastIndexOf(")");
            item = item.substring(start + 1, end);
            String[] attr = item.split(",");
            String k = attr[0].trim();
            String v = attr[1].trim();
            String value = map.get(k);
            if (value == null) {
                map.put(k, v);
            } else {
                //有值n
                value += ":";
                value += v;
                map.put(k, value);
            }
        }

        questionStructure.setDependencies(dependencies);
        String mainPartForTop = null;
        String mainPartForRoot = null;
        if (top != null) {
            mainPartForTop = topPattern(top, map);
        }
        if(root != null) {
            mainPartForRoot = rootPattern(root, map);
        }
        questionStructure.setMainPartForRoot(mainPartForRoot);
        questionStructure.setMainPartForTop(mainPartForTop);

        if(questionStructure.getMainPart() == null) {
            LOG.error("Cannot find the SVO:" + question);
        } else {
            LOG.info("SVO: " + questionStructure.getMainPart());
        }
        return questionStructure;
    }

    private String rootPattern(String pattern, Map<String,String> map) {
        int start = pattern.indexOf("(");
        int end = pattern.indexOf(")");
        pattern = pattern.substring(start + 1, end);
        String[] attr = pattern.split(",");
        String v = attr[1].trim();
        String first = null;
        // 临时谓语
        String second = v.split("-")[0];
        int secondIndex = Integer.parseInt(v.split("-")[1]);
        String third = "";

        String value = map.get(v);
        if(value == null) {
            return null;
        }
        String[] values = value.split(":");
        if (values != null && values.length > 0) { //TODO 特殊情况处理 主谓结构
            if (values.length > 1) {
                first = values[0].split("-")[0];
                third = values[values.length - 1].split("-")[0];
            } else {
                String k = values[0];
                String t = k.split("-")[0];
                int tIndex = Integer.parseInt(k.split("-")[1]);
                if (secondIndex < tIndex) {
                    //谓语 调整为 主语
                    first = second;
                    second = t;
                } else {
                    first = t;
                }
                //没有宾语，再次查找
                String val = map.get(k);
                if (val != null) {
                    //找到宾语
                    String[] vals = val.split(":");
                    if (vals != null && vals.length > 0) {
                        third = vals[vals.length - 1].split("-")[0];
                    } else {
                        LOG.info("宾语获取失败: " + first + " " + second);
                    }
                } else {
                    //找不到宾语，降级为主谓结构
                    third = "";
                }
            }
        } else {
            LOG.error("root模式未找到主语和宾语, " + v + " 只有依赖：" + value);
        }

        if(first!= null && second != null) {
            String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
            mainPart = mainPart.trim();
            return mainPart;
        }
        return null;
    }

    private String topPattern(String pattern, Map<String,String> map) {
        int start = pattern.indexOf("(");
        int end = pattern.indexOf(")");
        pattern = pattern.substring(start + 1, end);
        String[] attr = pattern.split(",");
        String k = attr[0].trim();
        String v = attr[1].trim();
        String value = map.get(k);
        String first = v.split("-")[0];
        String second = k.split("-")[0];
        String[] values = value.split(":");
        String candidate;
        // 是否多值
        if (values != null && values.length > 0) {
            candidate = values[values.length-1];
        } else {
            candidate = value;
        }
        String third = candidate.split("-")[0];
        String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
        mainPart = mainPart.trim();
        return mainPart;
    }

    public QuestionStructure getMainPart(String question) {
        question = question.replace("\\s+", "");
        String questionWords = questionParse(question);
        return getMainPart(question, questionWords);
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

    private String questionParse(String question) {
        LOG.info("Start to word-segementation: " + question);
        List<Word> words = WordParser.parse(question);
        StringBuilder wordStr = new StringBuilder();
        for (Word word : words) {
            wordStr.append(word.getText()).append(" ");
        }
        LOG.info("Segmentation results: " + wordStr.toString().trim());
        return wordStr.toString().trim();
    }

    public static void main(String[] args) {
        MainPartExtracter mainPartExtracter = new MainPartExtracter();
        QuestionStructure questionStructure = mainPartExtracter.getMainPart("勃学的创始人是谁");
        LOG.info(questionStructure.getQuestion());
        LOG.info(questionStructure.getMainPart());
        for (String d : questionStructure.getDependencies()) {
            LOG.info("\t" + d);
        }
    }
}
