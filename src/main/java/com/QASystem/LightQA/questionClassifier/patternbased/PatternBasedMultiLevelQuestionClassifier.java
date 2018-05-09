package com.QASystem.LightQA.questionClassifier.patternbased;

import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.model.QuestionType;
import com.QASystem.LightQA.parser.WordParser;
import com.QASystem.LightQA.questionClassifier.AbstractQuestionClassifier;
import com.QASystem.LightQA.questionClassifier.QuestionClassifier;
import com.QASystem.LightQA.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternBasedMultiLevelQuestionClassifier extends AbstractQuestionClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(PatternBasedMultiLevelQuestionClassifier.class);
    private static final Map<String, String> questionPatternCache = new HashMap<>();
    private static final Map<String, QuestionTypePattern> questionTypePatternCache = new HashMap<>();

    private static final AbstractMainPartExtracter mainPartExtracter = new MainPartExtracterEX();
    private final List<QuestionTypePatternFile> questionTypePatternFiles = new ArrayList<>();

    public PatternBasedMultiLevelQuestionClassifier(final PatternMatchStrategy patternMatchStrategy, PatternMatchResultSelector patternMatchResultSelector) {
        super.setPatternMatchStrategy(patternMatchStrategy);
        super.setPatternMatchResultSelector(patternMatchResultSelector);

        String appPath = Tools.getAppPath(PatternBasedMultiLevelQuestionClassifier.class);
        String path = appPath + "/questionTypePatterns/";
        LOG.info("Pattern type directory: " + path);
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] files = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (patternMatchStrategy.enableQuestionTypePatternFile(name)){
                        LOG.info("Enable the pattern file: " + name);
                        return true;
                    } else {
                        LOG.info("Disable the pattern file: " + name);
                    }
                    return false;
                }
            });
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(files));
            Collections.sort(list);
            for (String item : list) {
                LOG.info("Pattern file: " + item);
                String[] attr = item.split("_");
                QuestionTypePatternFile file = new QuestionTypePatternFile();
                file.setFile(item);
                if(attr != null && attr.length == 2) {
                    String match = attr[1].split("\\.")[0];
                    boolean multiMatch = Boolean.parseBoolean(match);
                    LOG.info("Enable multiMatch: " + multiMatch);
                    file.setMultiMatch(multiMatch);
                }
                questionTypePatternFiles.add(file);
            }
        } else {
            LOG.error("Pattern file directory is not exist: " + path);
        }
    }

    @Override
    public Question classify(Question question) {
        String questionStr = question.getQuestion();
        LOG.info("pattern matching for question: " + questionStr);
        PatternMatchStrategy patternMatchStrategy = getPatternMatchStrategy();
        if (patternMatchStrategy == null) {
            LOG.error("No specific pattern match strategy.");
            return question;
        }
        List<String> questionPatterns = extraQuestionPatternFromQuestion(questionStr, patternMatchStrategy);
        if(questionPatterns.isEmpty()) {
            LOG.error("question pattern extraction failed: " + questionStr);
            return question;
        }
        PatternMatchResult patternMatchResult = new PatternMatchResult();
        for (QuestionTypePatternFile qtpfile : questionTypePatternFiles) {
            String questionTypePatternFile = "/questionTypePatterns/" + qtpfile.getFile();
            LOG.info("questionTypePatternFile: " + questionTypePatternFile);
            QuestionTypePattern questionTypePattern = extractQuestionTypePattern(questionTypePatternFile);
            if (questionTypePattern != null) {
                List<PatternMatchResultItem> patternMatchResultItems = getPatternMatchResultItems(questionPatterns, questionTypePattern);
                if (patternMatchResultItems.isEmpty()) {
                    LOG.info("Cannot find the matching type： " + questionTypePatternFile);
                } else {
                    patternMatchResult.addPatternMatchResult(qtpfile, patternMatchResultItems);
                    LOG.info("Find the matching type： " + questionTypePatternFile);
                }
            } else {
                LOG.info("Cannot process the question type: " + questionTypePatternFile);
            }
        }

        List<PatternMatchResultItem> patternMatchResultItems = patternMatchResult.getAllPatternMatchResult();
        if (patternMatchResultItems.isEmpty()) {
            LOG.info("Question[" + questionStr + "] have not matched any pattern：");
            return question;
        }
        if (patternMatchResultItems.size() > 1) {
            LOG.info("Question[" + questionStr + "] have matched multi-patterns：");
            int i = 1;
            for (PatternMatchResultItem item : patternMatchResultItems) {
                LOG.info("No. :" + i++);
                LOG.info("\tQuestion : " + item.getOrigin());
                LOG.info("\tPattern : " + item.getPattern());
                LOG.info("\tType : " + item.getType());
            }
        }

        for (QuestionTypePatternFile file : patternMatchResult.getQuestionTypePatternFilesFromCompactToLoose()) {
            LOG.info("Question[" + questionStr + "] have matched multi-patterns：");
            int i = 1;
            for (PatternMatchResultItem item : patternMatchResult.getPatternMatchResult(file)) {
                LOG.info("No. :" + i++);
                LOG.info("\tQuestion : " + item.getOrigin());
                LOG.info("\tPattern : " + item.getPattern());
                LOG.info("\tType : " + item.getType());
            }
        }
        for (QuestionTypePatternFile file : patternMatchResult.getQuestionTypePatternFilesFromCompactToLoose()) {
            LOG.info("Question pattern match file " + file.getFile() + "]，whether allow to match multiple types：" + file.isMultiMatch());
            int i = 1;
            for (PatternMatchResultItem item : patternMatchResult.getPatternMatchResult(file)) {
                LOG.info("No. ：" + i++);
                LOG.info("\tQuestion : " + item.getOrigin());
                LOG.info("\tPattern : " + item.getPattern());
                LOG.info("\tType : " + item.getType());
            }
        }
        return getPatternMatchResultSelector().select(question, patternMatchResult);
    }

    private List<PatternMatchResultItem> getPatternMatchResultItems(List<String> questionPatterns, QuestionTypePattern questionTypePattern) {
        if (questionPatterns == null || questionPatterns.isEmpty()) {
            LOG.error("Must get a question Pattern before pattern matching");
            return null;
        }
        if (questionTypePattern == null || questionTypePattern.getPatterns().isEmpty()) {
            LOG.error("Must get a question type pattern before pattern matching");
            return null;
        }
        List<PatternMatchResultItem> patternMatchResultItems = new ArrayList<PatternMatchResultItem>();
        List<Pattern> patterns = questionTypePattern.getPatterns();
        List<String> types= questionTypePattern.getTypes();
        int len = patterns.size();
        for (int i =0; i < len; i++) {
            Pattern pattern = patterns.get(i);
            for (String questionPattern : questionPatterns) {
                Matcher m = pattern.matcher(questionPattern);
                if(m.matches()) {
                    LOG.info("Matching success: " + questionPattern + " : " + m.pattern() + " : " + types.get(i));
                    PatternMatchResultItem item = new PatternMatchResultItem();
                    item.setOrigin(questionPattern);
                    item.setPattern(pattern.pattern());
                    item.setType(types.get(i));
                    patternMatchResultItems.add(item);
                }
            }
        }
        return patternMatchResultItems;
    }

    private QuestionTypePattern extractQuestionTypePattern(String questionTypePatternFile) {
        QuestionTypePattern value = questionTypePatternCache.get(questionTypePatternFile);
        if(value != null) {
            return value;
        }

        Set<String> questionTypesForSet = new HashSet<>();
        Set<Pattern> questionPatternsForSet = new HashSet<>();

        List<String> types = new ArrayList<>();
        List<Pattern> patterns = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = PatternBasedMultiLevelQuestionClassifier.class.getResourceAsStream(questionTypePatternFile);
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
//                LOG.debug("Pattern" + (i++) + "：" + line);
                String[] tokens = line.split("\\s+", 3);
                types.add(tokens[0]);
                questionTypesForSet.add(tokens[0]);
                patterns.add(Pattern.compile(tokens[1], Pattern.CASE_INSENSITIVE));
                questionPatternsForSet.add(Pattern.compile(tokens[1], Pattern.CASE_INSENSITIVE));
            }
        } catch (Exception e) {
            LOG.error("Question pattern file read failure: " + questionTypePatternFile);
            LOG.debug("Question pattern file read failure: " + questionTypePatternFile, e);
            return null;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                LOG.error("Question pattern file close failure: " + questionTypePatternFile);
                LOG.debug("Question pattern file close failure: " + questionTypePatternFile, e);
                return null;
            }
        }
        if(patterns.isEmpty() || patterns.size() != types.size()) {
            LOG.info("Question type file is empty" + questionTypePatternFile);
            return null;
        }
        LOG.debug("Question type file loaded success!");
        LOG.debug("Question type：");
        int i = 1;
        for (String type : questionTypesForSet) {
            LOG.debug("Type: " + (i++) + ": " + type);
        }
        LOG.debug("All question pattern：");
        i = 1;
        for (Pattern pattern : questionPatternsForSet) {
            LOG.debug("Pattern" + (i++) + ": " + pattern.pattern());
        }
        QuestionTypePattern questionTypePattern = new QuestionTypePattern();
        questionTypePattern.setPatterns(patterns);
        questionTypePattern.setTypes(types);

        questionTypePatternCache.put(questionTypePatternFile, questionTypePattern);

        return questionTypePattern;
    }

    private List<String> extraQuestionPatternFromQuestion(String question, PatternMatchStrategy patternMatchStrategy) {
        List<String> questionPatterns = new ArrayList<>();
        question = question.trim();
        LOG.info("Question: " + question);
        if(patternMatchStrategy.enableQuestionPattern(QuestionPattern.Question)) {
            questionPatterns.add(question);
        }
        if(patternMatchStrategy.enableQuestionPattern(QuestionPattern.TermWithNatures)
                || patternMatchStrategy.enableQuestionPattern(QuestionPattern.Natures)) {
            String termWithNature = questionPatternCache.get(question + "termWithNature");
            String nature = questionPatternCache.get(question + "nature");

            if (termWithNature == null || nature == null) {
                List<Word> words = WordParser.parse(question);
                //APDPlat的发起人是谁？
                //apdplat/en 的/uj 发起人/n 是/v 谁/RW.RWPersonSingle ？/w
                StringBuilder termWithNatureStrs = new StringBuilder();
                //APDPlat的发起人是谁？
                //en/uj/n/v/RW.RWPersonSingle/w
                StringBuilder natureStrs = new StringBuilder();
                int i = 0;
                for (Word word : words) {
                    termWithNatureStrs.append(word.getText()).append("/").append(word.getPartOfSpeech().getPos()).append(" ");
                    if ((i++) > 0) {
                        natureStrs.append("/");
                    }
                    natureStrs.append(word.getPartOfSpeech().getPos());
                }
                termWithNature = termWithNatureStrs.toString();
                nature = natureStrs.toString();
                questionPatternCache.put(question + "termWithNature", termWithNature);
                questionPatternCache.put(question + "nature", nature);
            }

            if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.TermWithNatures)) {
                questionPatterns.add(termWithNature);
                LOG.info("termWithNature： " + termWithNature);
            }
            if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.Natures)) {
                questionPatterns.add(nature);
                LOG.info("nature： " + nature);
            }
        }
        if(patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartPattern)
                || patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartNaturePattern)) {
            String mainPart = questionPatternCache.get(question+"mainpart");
            if(mainPart == null) {
                QuestionStructure questionStructure = mainPartExtracter.getMainPart(question);
                if(questionStructure != null) {
                    mainPart = questionStructure.getMainPart();
                    questionPatternCache.put(question + "mainPart", mainPart);
                }
            }

            if(mainPart != null) {
                if(patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartPattern)) {
                    String questionMainPartPattern = questionPatternCache.get(question+"questionMainPartPattern");
                    if(questionMainPartPattern == null) {
                        questionMainPartPattern = mainPartExtracter.getQuestionMainPartNaturePattern(question, mainPart);
                        questionPatternCache.put(question+"questionMainPartPattern", questionMainPartPattern);
                    }
                    questionPatterns.add(questionMainPartPattern);
                    LOG.info("questionMainPartPattern: " + questionMainPartPattern);
                }

                if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartNaturePattern)) {
                    String questionMainPartNaturePattern = questionPatternCache.get(question + "questionMainPartNaturePattern");
                    if (questionMainPartNaturePattern == null) {
                        questionMainPartNaturePattern = mainPartExtracter.getQuestionMainPartNaturePattern(question, mainPart);
                        questionPatternCache.put(question + "mainPartPattern", questionMainPartNaturePattern);
                    }
                    questionPatterns.add(questionMainPartNaturePattern);
                    LOG.info("questionMainPartNaturePattern：" + questionMainPartNaturePattern);
                }
            }
        }
       return questionPatterns;
    }

    class QuestionTypePattern {

        private List<String> types = new ArrayList<>();
        private List<Pattern> patterns = new ArrayList<>();

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public List<Pattern> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<Pattern> patterns) {
            this.patterns = patterns;
        }
    }

    public static void main(String[] args) {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
//        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
//        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");

        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();

        QuestionClassifier questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);

        Question question = questionClassifier.classify("勃学的创始人是谁?");

        if (question != null) {
            LOG.info(question.getQuestion() + "type is: " + question.getQuestionType() + " candidate question type: " + question.getCandidateQuestionTypes());
        }
    }
}

