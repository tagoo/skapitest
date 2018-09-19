package com.sunrun.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ObjectUtil {
    public static  <T extends Object> void packData(T t1,T t2){
        Class<? extends Object> aClass = t1.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field: fields) {
            try {
                if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                    continue;
                }
                String paramName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                String getMethod;
                if (field.getType().getName().equals("java.lang.Boolean")){
                    getMethod = "is".concat(paramName);
                } else {
                    getMethod = "get".concat(paramName);
                }
                Method method = aClass.getMethod(getMethod);
                Object invoke = method.invoke(t1);
                if (invoke != null) {
                    Method bMethod = aClass.getMethod("set".concat(paramName), field.getType());
                    bMethod.invoke(t2,invoke);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
