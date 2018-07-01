package com.sunrun.common.notice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoticeFactory {

    private static final Logger Log = LoggerFactory.getLogger(NoticeFactory.class);
    public static ReturnCode createNotice(NoticeMessage noticeMessage, String lang){
        if ("zh".equalsIgnoreCase(lang)) {
            return new ReturnCode(noticeMessage.getCode(),noticeMessage.getCnMessage());
        } else if ("en".equalsIgnoreCase(lang)) {
            return new ReturnCode(noticeMessage.getCode(),noticeMessage.getEnMessage());
        }else {
            if(Log.isInfoEnabled())
                Log.info(String.format("The language \"%s\" is not supported, use Chinese by default!",lang));
            return new ReturnCode(noticeMessage.getCode(),noticeMessage.getCnMessage());
        }
    }
    public static ReturnData createNoticeWithFlag(NoticeMessage noticeMessage, String lang, Object data){
        if ("zh".equalsIgnoreCase(lang)) {
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getCnMessage(),NoticeMessage.SUCCESS == noticeMessage,data);
        } else if ("en".equalsIgnoreCase(lang)) {
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getEnMessage(),NoticeMessage.SUCCESS == noticeMessage,data);
        }else {
            if(Log.isInfoEnabled())
                Log.info(String.format("The language \"%s\" is not supported, use Chinese by default!",lang));
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getCnMessage());
        }
    }
    public static ReturnData createNoticeWithFlag(NoticeMessage noticeMessage, String lang){
        if ("zh".equalsIgnoreCase(lang)) {
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getCnMessage());
        } else if ("en".equalsIgnoreCase(lang)) {
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getEnMessage());
        }else {
            if(Log.isInfoEnabled())
                Log.info(String.format("The language \"%s\" is not supported, use Chinese by default!",lang));
            return new ReturnData(noticeMessage.getCode(),noticeMessage.getCnMessage());
        }
    }
}
