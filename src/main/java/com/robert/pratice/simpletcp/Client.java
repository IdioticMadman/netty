package com.robert.pratice.simpletcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(2000);
        socket.connect(new InetSocketAddress("127.0.0.1", 2000));
        System.out.println("已发起服务器连接，并进入后续流程");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " : " + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " : " + socket.getPort());
        try {
            chat(socket);
        } catch (Exception e) {
            System.out.println("客户端异常");
        }
    }

    private static void chat(Socket socket) throws IOException {
        InputStream in = System.in;
        BufferedReader cmdIn = new BufferedReader(new InputStreamReader(in));
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream socketOut = new PrintStream(socket.getOutputStream(), true);
        boolean flag = true;
        do {
            String req = cmdIn.readLine();
            socketOut.println(req);

            String resp = socketIn.readLine();
            System.out.println(resp);
            if ("bye".equals(req)) {
                flag = false;
            }

        } while (flag);
        //释放资源
        socketIn.close();
        socketOut.close();
        socket.close();
        System.out.println("客户端已退出~");
    }
}
