package com.QASystem.LightQA.parser;

import java.util.List;
import java.io.File;

import com.QASystem.LightQA.util.Tools;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class WordParser {
    private static final Logger LOG = LoggerFactory.getLogger(WordParser.class);

    static {
        String appPath = Tools.getAppPath(WordParser.class);
        String confFile = appPath + "/web/dic/word_v_1_3/word.local.conf";
        if(!new File(confFile).exists()){
            confFile = appPath + "/jar/dic/word_v_1_3/word.local.conf";
        }
        if(new File(confFile).exists()){
            LOG.info("Find the configure file of word parser: " + confFile);
            WordConfTools.forceOverride(confFile);
        }else {
            LOG.info("The configure file of word parser not exist: " + confFile);
        }
    }

    public static List<Word> parse(String str) {
        str = str.replace(".", " ");
        List<Word> words = WordSegmenter.segWithStopWords(str, SegmentationAlgorithm.MaxNgramScore);
        PartOfSpeechTagging.process(words);
        return words;
    }

    public static List<Word> parseWithoutStopWords(String str){
        List<Word> words = WordSegmenter.seg(str, SegmentationAlgorithm.MaxNgramScore);
        PartOfSpeechTagging.process(words);
        return words;
    }

    public static void main(String[] args) {
        List<Word> parse = parse("2017年7月9日 - 伟大的勃学创始人曾勃..许老师:清华能把我这种人招进来,便从一本降到了三本,同理我上的美西私立学校和公立学校也是三本,然而MIT不是三本,因为它在两个...");
        System.out.println(parse);
//        parse = parseWithoutStopWords("布什是个什么样的人呀");
//        System.out.println(parse);
//        parse = parse("张三和是谁");
//        System.out.println(parse);
//        parse = parse("哈雷彗星的发现者是六小龄童和伦琴,专访微软亚洲研究院院长洪小文");
//        System.out.println(parse);
//        String str = " 《创业邦》杂志记者对微软亚洲研究院院长洪小文进行了专访。 《创业邦》：微软亚洲  研究院 ... 从研发的角度来说，研究院是一个战略性的部门。因为一家公司最后成功与   ...";
//        parse = parse(str);
//        System.out.println(parse);
    }
}
