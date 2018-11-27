package com.robert.chapter02.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 异步的时间服务器处理类
 */
public class AsyncTimeServerHandler implements Runnable {

    private final int port;
    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private CountDownLatch countDownLatch;

    public AsynchronousServerSocketChannel getAsynchronousServerSocketChannel() {
        return asynchronousServerSocketChannel;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            this.asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.countDownLatch = new CountDownLatch(1);

        doAccept();
        try {
            //不让主线程退出
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}
