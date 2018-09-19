package com.sunrun.utils;

public class JidGenerator {
    private static JidFactory jidFactory;
    public static String generate(String content, String suffix) {
        return build().createJid(content,suffix);
    }
    public static JidFactory build(){
        if (jidFactory == null) {
            jidFactory = new JidFactory();
        }
        return jidFactory;
    }
    private static class JidFactory{
       public String createJid(String content,String suffix){
           return  content + "@" + suffix;
       }
    }
}
