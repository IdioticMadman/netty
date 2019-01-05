package com.robert.webserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebServer {

    public void bind() throws InterruptedException {
        EventLoopGroup acceptor = new NioEventLoopGroup();
        EventLoopGroup client = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(acceptor, client)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        /**
                         * http-request解码器
                         * http服务器端对request解码
                         */
                        pipeline.addLast("decoder", new HttpRequestDecoder());
                        /**
                         * http-response解码器
                         * http服务器端对response编码
                         */
                        pipeline.addLast("encoder", new HttpResponseEncoder());

                        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                        /**
                         * 压缩
                         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
                         * while respecting the "Accept-Encoding" header.
                         * If there is no matching encoding, no compression is done.
                         */
                        pipeline.addLast("deflater", new HttpContentCompressor());

                        pipeline.addLast("handler", new HttpDemoServerHandler());
                    }
                });
        Channel ch = bootstrap.bind(8080).sync().channel();
        ch.closeFuture().sync();
    }

}
