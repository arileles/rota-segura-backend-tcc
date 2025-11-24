package br.com.furb.rotasegura.domain.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

}
