package de.rose53.marvin.utils;

public class StringUtils {

    static public boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.length() == 0;
    }

    static public boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    static public boolean isBlank(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().length() == 0;
    }

    static public boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}
