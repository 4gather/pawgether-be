package com.example.pawgetherbe.domain;

import java.util.Map;

public class UserContext {
    private static final ThreadLocal<Map<String, String>> userHolder = ThreadLocal.withInitial(java.util.HashMap::new);

//    public static void setUserEmail(String userEmail) {
//        userHolder.get().put("userEmail", userEmail);
//    }

//    public static String getUserEmail() {
//        return userHolder.get().get("userEmail");
//    }

    public static void setUserId(String userId) {
        userHolder.get().put("userId", userId);
    }

    public static String getUserId() {
        return userHolder.get().get("userId");
    }

    public static void setUserRole(String userRole) {
        userHolder.get().put("userRole", userRole);
    }

    public static String getUserRole() {
        return userHolder.get().get("userRole");
    }

//    public static void setUserNickname(String userNickname) {
//        userHolder.get().put("userNickname", userNickname);
//    }
//
//    public static String getUserNickname() {
//        return userHolder.get().get("userNickname");
//    }

    public static void clear() {
        userHolder.remove();
    }
}
