package com.qqcommon;

import java.io.Serializable;

/**
 * @Yanx
 * @Create 2022-04-17-11:53
 */
public class User implements Serializable {
    private static  final long serialVersionUID = 1L;
    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User() {
    }
}
