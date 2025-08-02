package com.example.pawgetherbe.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣_]{3,20}$");

    public static boolean isValidId(String nickName) {
        return nickName != null && ID_PATTERN.matcher(nickName).matches();
    }
}
