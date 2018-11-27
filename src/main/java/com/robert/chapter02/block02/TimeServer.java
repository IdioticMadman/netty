package com.robert.chapter02.block02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.CloseableUtils;

public class TimeServer {
    public static void main(String[] args) {
        ServerSocket server = null;
        TimeServerHandlerExecutePool pool = new TimeServerHandlerExecutePool(50, 10000);
        try {
            server = new ServerSocket(8081);
            System.out.println("the time server is start in port 8081");
            Socket client;
            while (true) {
                client = server.accept();
                //开新线程 ，处理
                pool.execute(new TimerServerHandler(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("the time server close");
            CloseableUtils.close(server);
        }
    }
}
