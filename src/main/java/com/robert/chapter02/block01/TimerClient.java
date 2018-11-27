package com.robert.chapter02.block01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import utils.CloseableUtils;

public class TimerClient {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("192.168.10.182", 8081);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("QUERY TIME ORDER");
            System.out.println("send order to server succeed");
            String time = in.readLine();
            System.out.println("Now is :" + time);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.close(in);
            CloseableUtils.close(out);
            CloseableUtils.close(socket);
        }
    }
}
