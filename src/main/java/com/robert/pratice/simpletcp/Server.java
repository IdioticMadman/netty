package com.robert.pratice.simpletcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(2000);
            System.out.println("服务端准备就绪");
            System.out.println("服务器信息：" + server.getInetAddress() + " p : " + server.getLocalPort());
            while (true) {
                Socket client = server.accept();
                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            System.out.println("服务创建失败");
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {

        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("新客户端连接：" + socket.getInetAddress() + " : " + socket.getPort());
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream socketOutput = new PrintStream(socket.getOutputStream(), true);
                boolean flag = true;
                do {
                    String req = socketInput.readLine();
                    if (req.equals("bye")) {
                        socketOutput.println("bye");
                        flag = false;
                    } else {
                        System.out.println(req);
                        String resp = "回送：" + req.length();
                        socketOutput.println(resp);
                    }
                } while (flag);

                socketInput.close();
                socketOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("客户端已退出：" + socket.getInetAddress() + " : " + socket.getPort());
            }

        }
    }
}
