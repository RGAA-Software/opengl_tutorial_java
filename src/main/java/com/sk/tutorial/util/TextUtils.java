package com.sk.tutorial.util;

public class TextUtils {

    public static boolean isEmpty(String text) {
        if (text == null || text.isEmpty() || text.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean equals(String str1, String str2) {
        return str1 != null && str2 != null && !isEmpty(str1) && !isEmpty(str2) && str1.equals(str2);
    }

}
