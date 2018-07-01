package com.sunrun.utils;

public class JidGenerator {
    private static JidFactory jidFactory;
    public static String generate(String prefix, String content) {
        return build().createJid(prefix,content);
    }
    public static JidFactory build(){
        if (jidFactory == null) {
            jidFactory = new JidFactory();
        }
        return jidFactory;
    }
    private static class JidFactory{
       public String createJid(String prefix,String content){
           return  prefix + "@" + content;
       }
    }
}
