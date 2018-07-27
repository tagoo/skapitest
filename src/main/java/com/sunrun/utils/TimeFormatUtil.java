package com.sunrun.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeFormatUtil {
    public static String getCurrentTimestampStr(){
        return "00"+ LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static Date toDate(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant());
    }
}
