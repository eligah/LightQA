package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.parser.WordParser;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

public class MainPartExtracterEX implements AbstractMainPartExtracter{
    private static Logger LOG = LoggerFactory.getLogger(MainPartExtracterEX.class);
    private static final LexicalizedParser LP;
    private static final GrammaticalStructureFactory GSF;

    static{
        String models = "models/chineseFactored.ser.gz";
        LOG.info("Model: " + models);
        LP = LexicalizedParser.loadModel(models);

        TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
        GSF = tlp.grammaticalStructureFactory();
    }

    @Override
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

    @Override
    public QuestionStructure getMainPart(String question, List<Word> words) {
        QuestionStructure questionStructure = new QuestionStructure();
        questionStructure.setQuestion(question);

        MainPart mainPart = new MainPart();
        if (words == null || words.size() == 0) {
            questionStructure.setMainPart(mainPart);
            return questionStructure;
        }
        Tree tree = LP.apply(words);
        questionStructure.setTree(tree);
//        LOG.info("Lexcial tree: ");
//        tree.pennPrint();

        switch (tree.firstChild().label().toString()) {
            case "NP":
                //处理名词短语 视为只有主语的句子
                mainPart = getNPPhraseMainPart(tree);
                questionStructure.setMainPart(mainPart);
                break;
            default:
                 GrammaticalStructure gs = GSF.newGrammaticalStructure(tree);
                 Collection<TypedDependency> tdls = gs.typedDependenciesCCprocessed(true);
                 questionStructure.setTdls(tdls);
                 TreeGraphNode rootNode = getRootNode(tdls);
                 LOG.info("Dependency: {}", tdls);
                 if (rootNode == null) {
                     mainPart =  getNPPhraseMainPart(tree);
                     break;
                 }
//                 LOG.info("Predicate: {}" , rootNode);
                 mainPart = new MainPart(rootNode);
                 for( TypedDependency td : tdls) {
                     TreeGraphNode gov = td.gov();
                     GrammaticalRelation reln = td.reln();
                     String shortName = reln.getShortName();
                     TreeGraphNode dep = td.dep();
                     if (gov == rootNode) {
                         switch (shortName) {
                             case "nsubjpass":
                             case "dobj":
                             case "attr":
                                 mainPart.setObject(dep);
                                 break;
                             case "nsubj":
                             case "top":
                                 mainPart.setSubject(dep);
                                 break;
                         }
                     }
                     if (mainPart.getObject() != null && mainPart.getSubject() != null) {
                         break;
                     }
                 }

                 combineNN(tdls, mainPart.getSubject());
                 combineNN(tdls, mainPart.getObject());
                 if(mainPart.isNotDone()){
                     mainPart.done();
                 }
                questionStructure.setMainPart(mainPart);
        }
        LOG.info("MainPart: {}", questionStructure.getMainPart());
        return questionStructure;
    }


    //TODO figure out.
    private void combineNN(Collection<TypedDependency> tdls, TreeGraphNode target) {
        if (target  == null) return;
        for (TypedDependency td : tdls) {
            TreeGraphNode gov = td.gov();
            GrammaticalRelation reln = td.reln();
            String shortName = reln.getShortName();
            TreeGraphNode dep = td.dep();
            if (gov == target) {
                switch (shortName) {
                    case "nn":
                        target.setValue(dep.toString("value") + target.value());
                        return;
                }
            }
        }

    }

    private MainPart getNPPhraseMainPart(Tree tree) {
        MainPart mainPart = new MainPart();
        StringBuilder result= new StringBuilder();
        List<String> phraseList = getPhraseList("NP", tree);
        for (String phrase : phraseList) {
            result.append(phrase);
        }
        mainPart.setResult(result.toString());
        return mainPart;
    }

    private List<String> getPhraseList(String type, Tree tree) {
        List<String> phraseList = new LinkedList<String>();
        for (Tree subtree : tree) {
            if(subtree.isPrePreTerminal()) {
                StringBuilder result = new StringBuilder();
                for (Tree leaf : subtree.getLeaves()) {
                    result.append(leaf.value());
                }
                phraseList.add(result.toString());
            }
        }
        return phraseList;
    }

    private static TreeGraphNode getRootNode(Collection<TypedDependency> tdls) {
        for (TypedDependency td: tdls) {
            if (td.reln() == GrammaticalRelation.ROOT) {
                return td.dep();
            }
        }
        return null;
    }

    @Override
    public QuestionStructure getMainPart(String question) {
        question = question.replace("\\s+", "");
        String questionWords = questionParse(question);
        return getMainPart(question, questionWords);
    }

    private String questionParse(String question) {
        LOG.info("Start to word-segementation: " + question);
        List<org.apdplat.word.segmentation.Word> words = WordParser.parse(question);
        StringBuilder wordStr = new StringBuilder();
        for (org.apdplat.word.segmentation.Word word : words) {
            wordStr.append(word.getText()).append(" ");
        }
        LOG.info("Segmentation results: " + wordStr.toString().trim());
        return wordStr.toString().trim();
    }

    @Override
    public String getQuestionMainPartNaturePattern(String question, String mainPart) {
        return null;
    }

    public static void main(String[] args) {
        AbstractMainPartExtracter mainPartExtracter = new MainPartExtracterEX();

        String[] testCaseArray = {
                "勇敢伟大的中华人民", //名词短语 -- 缺谓语 宾语
                "勃学的创始人是", // 主谓 -- 缺宾语
                "勃学的创始人是谁", // 主谓宾
                "你被我喜欢", // nsubj 被动句
                "美丽又善良的你被卑微的我深深的喜欢着……", // 去除主语, 宾语 修饰部分
                "只有自信的程序员才能把握未来",
                "主干识别可以提高检索系统的智能",
                "这个项目的作者是hankcs",
                "hankcs是一个无门无派的浪人",
                "搜索hankcs可以找到我的博客",
                "静安区体育局2013年部门决算情况说明",
                "这类算法在有限的一段时间内终止",
        };
        for (String testCase : testCaseArray)
        {
            QuestionStructure qs = mainPartExtracter.getMainPart(testCase);
            LOG.info(qs.getMainPart());
        }
    }
}
