package com.robert.bean;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;
@Message
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 3673450486987202229L;

    private String userName;
    private int userId;

    public UserInfo buildUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserInfo buildUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        return getBytes(buffer);
    }


    public byte[] codeC(ByteBuffer buffer) {
        buffer.clear();
        return getBytes(buffer);
    }

    private byte[] getBytes(ByteBuffer buffer) {
        buffer.putInt(this.userName.length());
        buffer.put(this.userName.getBytes());
        buffer.putInt(this.userId);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
