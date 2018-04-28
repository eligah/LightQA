package com.QASystem.LightQA.parser;

import java.util.List;
import java.io.File;

import com.QASystem.LightQA.util.Tools;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
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
        }else {
            LOG.info("The configure file of word parser not exist: " + confFile);
        }
    }

    public static List<Word> parse(String str) {
        List<Word> words = WordSegmenter.segWithStopWords(str, SegmentationAlgorithm.MaxNgramScore);
        PartOfSpeechTagging.process(words);
        return words;
    }

    public static List<Word> parseWithoutStopWords(String str){
        List<Word> words = WordSegmenter.seg(str, SegmentationAlgorithm.MaxNgramScore);
        PartOfSpeechTagging.process(words);
        return words;
    }
}
