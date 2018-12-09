package com.robert.pratice.simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 1. 对指定端口发送一个广播包
 * 2. 等待provider回应消息
 */

public class UdpSearcher {

    public static void main(String[] args) throws IOException {
        System.out.println("UdpSearcher 启动");
        //1. 搜索方由系统指定端口
        DatagramSocket ds = new DatagramSocket();
        //2. 准备发送请求
        byte[] req = "Hello World".getBytes();
        DatagramPacket reqPacket = new DatagramPacket(req, req.length);
        reqPacket.setAddress(InetAddress.getLocalHost());
        reqPacket.setPort(20000);
        //3. 发布广播包
        ds.send(reqPacket);
        //4. 构建等待接受的packet
        byte[] buffer = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length);
        //5. 阻塞等待接受包
        ds.receive(receivePack);
        //6. 解析packet
        String address = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int length = receivePack.getLength();
        String data = new String(receivePack.getData(), 0, length);
        System.out.println("Receiver Address: [ " + address + " ], port: [ " + port + " ], data: [ " + data + " ]");
        //7. 释放资源
        ds.close();
        System.out.println("UdpSearcher 结束");

    }
}
