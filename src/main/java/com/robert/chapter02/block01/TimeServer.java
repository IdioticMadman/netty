package com.robert.chapter02.block01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.CloseableUtils;

public class TimeServer {
    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(8081);
            System.out.println("the time server is start in port 8081");
            Socket client;
            while (true) {
                client = server.accept();
                //开新线程 ，处理
                new Thread(new TimerServerHandler(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("the time server close");
            CloseableUtils.close(server);
        }
    }
}
