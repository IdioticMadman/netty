package com.robert.pratice.simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDP提供者 -> 监听某个端口，当有人询问时，则回给一个回复
 * 1. 等待searcher往指定端口发包
 * 2. 读取后，回送一个数据包
 */
public class UdpProvider {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider 已经启动，等待接受消息");
        //1. 监听端口
        DatagramSocket ds = new DatagramSocket(20000);
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
        //2. 等待接受消息
        ds.receive(packet);
        System.out.println("收到消息");
        //3. 解析数据
        String address = packet.getAddress().getHostAddress();
        int port = packet.getPort();
        int length = packet.getLength();
        System.out.println("packetLength: " + length + " ,packetData'length: " + packet.getData().length);
        String data = new String(packet.getData(), 0, length);
        System.out.println("Receiver Address: [ " + address + " ], port: [ " + port + " ], data: [ " + data + " ]");
        //4. 构建回应数据
        String response = "Receiver data with len: " + length;
        byte[] resp = response.getBytes();
        //5. 构建回应packet
        DatagramPacket respPacket = new DatagramPacket(resp, resp.length, packet.getAddress(), packet.getPort());
        ds.send(respPacket);
        //6. 释放资源
        System.out.println("UDPProvider 结束");
        ds.close();
    }
}
