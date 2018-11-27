package com.robert.chapter02.nio01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import utils.CloseableUtils;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            //初始化Selector、ServerSocketChannel
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            //配置tcp参数
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            //注册到selector，监听OP_ACCEPT位
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
            //初始化失败，退出进程
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //每隔一秒唤醒一次. 也有无参的，那样系统会自动回调进行遍历
                selector.select(1000);
                //遍历selector中的迭代器
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CloseableUtils.close(selector);
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理客户端新接入的请求，注册到Selector上
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                //还可以对当前channel进行额外设置
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                //读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                //初始化一个缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //读取channel中的数据
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {  //读到了字节，进行编解码
                    //flip操作，将缓冲区当前的limit设置为position，position设置为0，用于后续对缓冲的读取操作
                    readBuffer.flip();
                    //readBuffer.remaining() limit-position,也就是buffer里面数据长度
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //读取到数组中
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receive order: " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                            new Date(System.currentTimeMillis()).toString() :
                            "BAD ORDER";
                    //返回数据
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    //链路已经关闭，需要关闭socketChannel,释放资源
                    key.cancel();
                    sc.close();
                } else {
                    ;//读到0字节忽略，正常情况
                }
            }

        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            //初始化数据大小的缓冲区
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //写入缓冲区
            writeBuffer.put(bytes);
            //准备好缓存区，以被读取
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
