package com.robert.chapter06;

import com.robert.bean.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class PerformTestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo().buildUserName("Welcome to Netty")
                .buildUserId(100);
        int loop = 1000000;
        ByteArrayOutputStream baos;
        ObjectOutputStream oos;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(userInfo);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            oos.close();
        }
        System.out.println("The jdk serializable time is : " + (System.currentTimeMillis() - startTime) + " ms");
        startTime = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < loop; i++) {
            byte[] bytes = userInfo.codeC(buffer);
        }
        System.out.println("The byte array serializable time is : " + (System.currentTimeMillis() - startTime) + " ms");

    }
}
