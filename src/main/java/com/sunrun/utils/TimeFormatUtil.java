package com.sunrun.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeFormatUtil {
    public static String getCurrentTimestampStr(){
        return "00"+ LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
