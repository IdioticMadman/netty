package com.robert.juejin.protocol.bean;

import lombok.Data;

@Data
public class Session {
    String userId;
    String userName;

    public Session(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
