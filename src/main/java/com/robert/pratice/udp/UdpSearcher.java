package com.robert.pratice.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 1. 往指定端口发送消息
 * 2. 持续监听自己的端口，是否有回送消息
 */
public class UdpSearcher {

    private static final int LISTENER_PORT = 30000;

    public static void main(String[] args) throws InterruptedException {
        //发送广播
        int sendPort = 20000;
        //开启监听
        Listener listener = listen();

        //发送广播开始查找
        send(sendPort);
        try {
            int read = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            List<Device> devices = listener.getDevicesAndExit();
            for (Device device : devices) {
                System.out.println(device.toString());
            }
        }
        System.out.println("UdpSearcher 退出");
    }

    private static Listener listen() throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(1);
        Listener listener = new Listener(downLatch);
        //开始监听
        listener.start();
        //等待监听开始
        downLatch.await();
        return listener;
    }

    private static void send(int port) {
        System.out.println("UdpSearcher 开始发送广播");
        try (DatagramSocket ds = new DatagramSocket()) {
            String searchMsg = MessageFactory.buildMsgWithPort(LISTENER_PORT);
            byte[] bytes = searchMsg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            //广播地址
            packet.setAddress(InetAddress.getByName("255.255.255.255"));
            //指定端口
            System.out.println("UdpSearcher 广播端口：" + port);
            packet.setPort(port);
            ds.send(packet);
            ds.close();
            System.out.println("UdpSearcher 广播发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class Listener extends Thread {

        private final CountDownLatch downLatch;
        private List<Device> devices = new ArrayList<>();
        private boolean flag = true;
        private DatagramSocket ds;

        public Listener(CountDownLatch downLatch) {
            this.downLatch = downLatch;
        }

        @Override
        public void run() {
            downLatch.countDown();
            System.out.println("UdpSearcher 开始监听");
            try {
                ds = new DatagramSocket(LISTENER_PORT);
                while (flag) {
                    byte[] receiverBuf = new byte[512];
                    DatagramPacket receiverPacket = new DatagramPacket(receiverBuf, 0, receiverBuf.length);
                    //监听指定端口的消息
                    ds.receive(receiverPacket);
                    //解析消息
                    String receiveData = new String(receiverPacket.getData(), 0, receiverPacket.getLength());
                    System.out.println("UdpSearcher 收到消息：" + receiveData);
                    String sn = MessageFactory.parseSnByMsg(receiveData);
                    int port = receiverPacket.getPort();
                    String address = receiverPacket.getAddress().getHostAddress();
                    //添加到缓存
                    Device device = new Device(port, sn, address);
                    devices.add(device);
                    System.out.println("UdpSearcher 找到Device：" + device.toString());
                }
            } catch (IOException e) {
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

        public void exit() {
            flag = false;
            close();
        }

        public List<Device> getDevicesAndExit() {
            exit();
            return devices;
        }
    }
}
