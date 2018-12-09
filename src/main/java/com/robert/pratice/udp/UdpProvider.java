package com.robert.pratice.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * UDP的提供者，持续监听某个端口，接收到信息的时候，从信息中读取到指定端口，进行回送消息
 */
public class UdpProvider {

    public static void main(String[] args) {
        int port = 20000;
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(port, sn);
        provider.start();
        try {
            int read = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //读取到键盘操作退出
            provider.exit();
        }
        System.out.println("UdpProvider " + sn + " 停止");
    }


    public static class Provider extends Thread {
        //监听广播的端口
        private final int listenerPort;
        //当前提供者的sn
        private final String sn;
        private DatagramSocket ds;
        private boolean flag = true;

        public Provider(int listenerPort, String sn) {
            this.listenerPort = listenerPort;
            this.sn = sn;

        }

        @Override
        public void run() {
            System.out.println("UdpProvider " + sn + " 启动");
            try {
                ds = new DatagramSocket(listenerPort);
                while (flag) {
                    byte[] receiverBuf = new byte[512];
                    DatagramPacket receiverPack = new DatagramPacket(receiverBuf, receiverBuf.length);
                    System.out.println("UdpProvider 监听：" + listenerPort);
                    //接受指定端口的消息
                    ds.receive(receiverPack);
                    //获取消息
                    int reqLength = receiverPack.getLength();
                    String req = new String(receiverPack.getData(), 0, reqLength, StandardCharsets.UTF_8);
                    //解析需要发送到的端口
                    int respPort = MessageFactory.parsePortByMsg(req);
                    System.out.println("UdpProvider receive msg: [ " + req + " ]" + "，回送端口：" + respPort);
                    //解析出来端口
                    if (respPort > 0) {
                        String resp = MessageFactory.buildMsgWithSn(sn);
                        //构建回送消息
                        DatagramPacket respPacket = new DatagramPacket(resp.getBytes(), resp.length());
                        respPacket.setPort(respPort);
                        respPacket.setAddress(receiverPack.getAddress());
                        //发送回送消息
                        ds.send(respPacket);
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void close() {
            if (ds != null) {
                ds.close();
            }
        }


        void exit() {
            flag = false;
            //就算可能退出了循环，但是socket的receiver一个广播的时候会阻塞当前线程，所以需要关闭当前socket通道，才会退出
            close();
        }
    }

}
