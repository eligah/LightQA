package com.QASystem.LightQA.util;


import java.net.URL;
import java.net.URLDecoder;

public class Tools {
    public static String getAppPath(Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The arguments cannot be empty.");
        }
        ClassLoader loader = cls.getClassLoader();
        String clsName = cls.getName();
        Package pack = cls.getPackage();
        String path = "";

        if (pack != null){
            String packName = pack.getName();
            if (packName.startsWith("java.") || packName.startsWith("javax.")) {
                throw new IllegalArgumentException("Don't pass the system java class");
            }
            clsName = clsName.substring(packName.length() + 1);
            if (packName.indexOf(".") < 0) {
                path = packName + "/";
            }else {
                int start = 0;
                int end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + '/';
            }
        }

        URL url = loader.getResource(path + clsName);
        String realPath = url.getPath();

        int pos = realPath.indexOf("file:");
        if (pos > -1){
            realPath = realPath.substring(pos + 5);
        }
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);

        if (realPath.endsWith("!")){
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }

        //when it contains chinese characters
        try{
            realPath = URLDecoder.decode(realPath, "utf-8");
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        if(realPath.endsWith("/lib")) {
            realPath = realPath.replace("/lib", "/classes");
        }

        return realPath;
    }
}
