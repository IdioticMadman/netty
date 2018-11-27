package com.robert.chapter02.aio;

public class TimeClient {
    public static void main(String[] args) {
        //启动client
        new Thread(new AsyncTimeClientHandler("127.0.0.1", 8080),
                "AIO-AsyncTimeClientHandler-001").start();
    }
}
