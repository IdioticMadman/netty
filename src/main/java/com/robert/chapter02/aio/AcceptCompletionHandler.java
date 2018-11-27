package com.robert.chapter02.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        //继续异步监听接下来的客户端
        attachment.getAsynchronousServerSocketChannel().accept(attachment, this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /**
         * dst, 接收缓存区，用于从异步channel中读取数据包
         * attachment, 异步channel携带的附件，通知回调的时候作为入参
         *
         */
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        attachment.getCountDownLatch().countDown();
    }
}
