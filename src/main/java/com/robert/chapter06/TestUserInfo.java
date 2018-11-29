package com.robert.chapter06;

import com.robert.bean.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestUserInfo {
    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo().buildUserName("Welcome to Netty")
                .buildUserId(100);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(userInfo);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            System.out.println("The jdk serializable length is : " + bytes.length);
            baos.close();
            System.out.println("The byte array serializable length is : " + userInfo.codeC().length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
