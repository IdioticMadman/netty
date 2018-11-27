package com.robert.chapter02.block01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import utils.CloseableUtils;

public class TimerServerHandler implements Runnable {

    private final Socket mSocket;

    public TimerServerHandler(Socket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()), true);
            String currentTime;
            String body;
            while (true) {
                //读入指令
                body = in.readLine();
                if (body == null) {
                    System.out.println("The TimeServer receiver break ");
                    break;
                }
                System.out.println("The TimeServer receiver order: " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                        new Date(System.currentTimeMillis()).toString() :
                        "BAD ORDER";
                //响应指令
                out.println(currentTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.close(in);
            CloseableUtils.close(out);
            CloseableUtils.close(mSocket);
        }
    }
}
