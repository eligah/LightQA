package com.QASystem.LightQA.datasource;

import com.QASystem.LightQA.files.FilesConfig;
import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.system.QuestionAnsweringSystem;
import com.QASystem.LightQA.files.FilesConfig;


import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class BaiduDataSource implements DataSource {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduDataSource.class);

    private static final String ENCODING = "gzip, deflate";
    private static final String ACCEPT = "text/html, */*; q=0.01";
    private static final String LANGUAGE = "zh-CN,zh;q=0.9";
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36 ";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.baidu.com";


    private  static final int PAGES = 1;
    private static final int PAGESIZE = 10;

    private static final boolean SUMMARY = true;

    private final List<String> files = new ArrayList<>();

    public BaiduDataSource(){

    }

    public BaiduDataSource(List<String> files) {
        this.files.addAll(files);
    }

    public BaiduDataSource(String file) {
        this.files.add(file);
    }

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr, null);
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        List<Question> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                        line = reader.readLine();
                        continue;
                    }
                    LOG.info("Load question from " + file + line.trim());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String questionStr = null;
                    String expectAnswer= null;
                    String[] attrs = line.trim().split("[:|：]");

                    if (attrs == null) {
                        questionStr = line.trim();
                    }
                    if (attrs.length == 1) {
                        questionStr = attrs[0];
                    }
                    if (attrs.length == 2) {
                        questionStr = attrs[0];
                        expectAnswer = attrs[1];
                    }
                    LOG.info("Question:" + questionStr);
                    LOG.info("ExpectAnswer:" + expectAnswer);
                    Question question = getQuestion(questionStr);
                    if (question != null) {
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);
                    }

                    if (questionAnsweringSystem != null && question != null) {
                        questionAnsweringSystem.answerQuestion(question);
                    }

                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("Loaded question from " + file + "and get " + questions.size() + " question");
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        Question question = new Question();
        question.setQuestion(questionStr);

        String query = "";

        try {
            query = URLEncoder.encode(question.getQuestion(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("URL construction failed", e);
            return null;
        }
        String referer = "https://www.baidu.com/";

        for ( int i = 0; i < PAGES; i++) {
            query = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + query+"&oq="+query+"&usm=3&f=8&bs="+query+"&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * PAGESIZE;
            LOG.debug(query);
            List<Evidence> evidences = searchBaidu(query, referer);
            referer = query;
            if (evidences != null && evidences.size() > 0) {
                question.addEvidences(evidences);
            } else {
                LOG.error("Page " + (i + 1) + " cannot get any evidence");
                break;
            }
        }

        LOG.info("Question: " + question.getQuestion() + "Searching result (evidence): " + question.getEvidences().size());
        if (question.getEvidences().isEmpty()) {
            return null;
        }

        if (questionAnsweringSystem != null){
            questionAnsweringSystem.answerQuestion(question);
        }
        return question;
    }

    private List<Evidence> searchBaidu(String url, String referer) {
        List<Evidence> evidences = new ArrayList<>();
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("User-Agent", USER_AGENT)
                    .header("Host", HOST)
                    .header("referer",referer).get();
            String resultCssQuery = "html > body > div > div > div > div > div";
            Elements elements = document.select(resultCssQuery);
            for (Element element : elements) {
                Elements subElements = element.select("h3 > a");
                if (subElements.size() != 1) {
                    LOG.debug("Title not found");
                    continue;
                }
                String title = subElements.get(0).text();
                if (title == null || "".equals(title.trim())) {
                    LOG.debug("Title is empty");
                    continue;
                }
                subElements = element.select("div.c-abstract");
                if (subElements.size() != 1) {
                    LOG.debug("Summary not found");
                    continue;
                }
                String snippet = subElements.get(0).text();
                if (snippet == null || "".equals(snippet.trim())) {
                    LOG.debug("Summary is empty");
                    continue;
                }
                Evidence evidence = new Evidence();
                evidence.setTitle(title);
                evidence.setSnippet(snippet);

                evidences.add(evidence);
            }
        } catch (Exception e) {
            LOG.error("Baidu search engine error", e);
        }
        return evidences;

    }

    public static void main(String args[]) {
        Question question = new BaiduDataSource(FilesConfig.personNameQuestions).getQuestion("勃学的创始人是谁？");
        // LOG.info(question.toString());
    }
}
