package com.sunrun.utils.helper;

import com.sunrun.common.OpenfireSystemProperties;
import com.sunrun.support.iam.SystemPropertyInfo;
import com.sunrun.utils.JidGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArraysUtil {
    public static List<String> toListWithDomain(String[] str){
        return Arrays.asList(str).stream().map(u -> {
            if ( !u.contains("@")) {
                return JidGenerator.generate(u,SystemPropertyInfo.getProperties().get(OpenfireSystemProperties.XMPP_DOMAIN).getValue());
            } else {
                return u;
            }
        }).collect(Collectors.toList());
    }
    public static List<String> toListWithDomain(List<String> list){
        return list.stream().map(u -> {
            if ( !u.contains("@")) {
                return JidGenerator.generate(u,SystemPropertyInfo.getProperties().get(OpenfireSystemProperties.XMPP_DOMAIN).getValue());
            } else {
                return u;
            }
        }).collect(Collectors.toList());
    }
}
