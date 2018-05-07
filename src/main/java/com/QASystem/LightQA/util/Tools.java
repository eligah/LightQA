package com.QASystem.LightQA.util;


import com.QASystem.LightQA.model.Evidence;
import com.QASystem.LightQA.model.Question;
import com.QASystem.LightQA.model.QuestionType;
import com.QASystem.LightQA.parser.WordParser;
import org.apdplat.word.segmentation.Word;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    private static final Logger LOG = LoggerFactory.getLogger(Tools.class);
    private static Map<String, Integer> map = new HashMap<>();


    public static String getAppPath(Class cls) {
        // 检查用户传入的参数是否为空
        if (cls == null) {
            throw new IllegalArgumentException("The arguments cannot be empty.");
        }
        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        // 获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        // 如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax.")) {
                throw new IllegalArgumentException("Don't pass the system java class");
            }
            // 在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            // 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(".") < 0) {
                path = packName + "/";
            } else {
                // 否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + "/";
            }
        }
        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        URL url = loader.getResource(path + clsName);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        /*------------------------------------------------------------
         ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
         中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
         的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
         中文及空格路径
         -------------------------------------------------------------*/
        try {
            realPath = URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //统一转换到类路径下
        if(realPath.endsWith("/lib")){
            realPath = realPath.replace("/lib", "/classes");
        }
        //处理maven中的依赖JAR
//        if(realPath.contains("/org/apdplat/deep-qa/")){
//            int index = realPath.lastIndexOf("/");
//            String version = realPath.substring(index+1);
//            String jar = realPath+"/deep-qa-"+version+".jar";
//            LOG.info("maven jar："+jar);
//            ZipUtils.unZip(jar, "dic", "deep-qa/dic", true);
//            ZipUtils.unZip(jar, "questionTypePatterns", "deep-qa/questionTypePatterns", true);
//            realPath = "deep-qa";
//        }
        return realPath;
    }

    public static Set<String> getQuestions(String file) {
        Set<String> result = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Tools.class.getResourceAsStream(file), "utf-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                //去除空格和？号
                line = line.trim().replace("?", "").replace("？", "");
                if (line.equals("") || line.startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                    continue;
                }
                result.add(line);
            }
        } catch (Exception e) {
            LOG.error("读文件错误", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("关闭文件错误", e);
                }
            }
        }
        return result;
    }

    public static <K> List<Map.Entry<K,Integer>> sortByIntegerValue(Map<K,Integer> map) {
        List<Map.Entry<K, Integer>> orderList = new ArrayList<>(map.entrySet());
        Collections.sort(orderList, new Comparator<Map.Entry<K, Integer>>() {
            @Override
            public int compare(Map.Entry<K, Integer> o1, Map.Entry<K, Integer> o2) {
                Integer t = o1.getValue() - o2.getValue();
                if (t > 0 ){
                    return 1;
                } else if (t == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return orderList;
    }

    public static <K> List<Map.Entry<K,Double>> sortByDoubleValue(Map<K, Double> map) {
        List<Map.Entry<K, Double>> orderList = new ArrayList<>(map.entrySet());
        Collections.sort(orderList, new Comparator<Map.Entry<K, Double>>() {
            @Override
            public int compare(Map.Entry<K, Double> o1, Map.Entry<K, Double> o2) {
                double abs = o1.getValue() - o2.getValue();
                if (abs > 1e-10) {
                    return 1;
                } else if (abs < -1e-10) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return orderList;
    }

    public static int getIDF(String term) {
        Integer idf = map.get(term);
        if (idf == null) {
            return 0;
        }
        LOG.info("idf " + term + ":" + idf);
        return idf;
    }

    public static List<Map.Entry<String, Integer>> initIDF(List<Question> questions) {
        map = new HashMap<>();
        for (Question question : questions) {
            List<Evidence> evidences = question.getEvidences();
            for (Evidence evidence : evidences) {
                Set<String> set = new HashSet<>();
                List<Word> words = WordParser.parse(evidence.getTitle() + evidence.getSnippet());
                for (Word word : words) {
                    set.add(word.getText());
                }
                for (String item :set) {
                    Integer doc = map.get(item);
                    if(doc == null) {
                        doc =1;
                    }else {
                        doc++;
                    }
                    map.put(item,doc);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = Tools.sortByIntegerValue(map);
        for (Map.Entry<String, Integer> entry : list) {
            LOG.debug(entry.getKey() + " " + entry.getValue());
        }
        return list;
    }

    public static int countsForbigram(String text, String pattern) {
        int count = 0;
        int index = -1;
        while(true) {
            index = text.indexOf(pattern, index + 1);
            if (index > -1) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static int countsForSkipbigram(String text, String pattern) {
        int count = 0;
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            LOG.debug("Regular express match: " + matcher.group());
            count++;
        }
        return count;
    }

    public static List<Word> getWords(String s) {
        List<Word> words = WordParser.parse(s);
        return words;
    }
}
