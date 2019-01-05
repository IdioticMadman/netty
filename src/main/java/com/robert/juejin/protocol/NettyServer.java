package com.robert.juejin.protocol;

import com.robert.juejin.protocol.handler.AuthHandler;
import com.robert.juejin.protocol.handler.Spliter;
import com.robert.juejin.protocol.handler.inbound.PacketDecoder;
import com.robert.juejin.protocol.handler.outbound.PacketEncoder;
import com.robert.juejin.protocol.request.handler.LoginRequestHandler;
import com.robert.juejin.protocol.request.handler.MessageRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

public class NettyServer {

    private static final int PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
//                        ch.pipeline().addLast(new ServerHandler());
//
//                        ch.pipeline().addLast(new InBoundHandlerA());
//                        ch.pipeline().addLast(new InBoundHandlerB());
//                        ch.pipeline().addLast(new InBoundHandlerC());
//
//                        ch.pipeline().addLast(new OutBoundHandlerA());
//                        ch.pipeline().addLast(new OutBoundHandlerB());
//                        ch.pipeline().addLast(new OutBoundHandlerC());

                        ChannelPipeline pipeline = ch.pipeline();
                        //粘包半包处理
                        pipeline.addLast("frameDecoder", new Spliter());
                        //解码
                        pipeline.addLast("packDecoder", new PacketDecoder());
                        pipeline.addLast("loginHandler", new LoginRequestHandler());
                        pipeline.addLast("authHandler", new AuthHandler());
                        pipeline.addLast("messageHandler", new MessageRequestHandler());
                        //编码
                        pipeline.addLast("packEncoder", new PacketEncoder());
                    }
                });

        bind(serverBootstrap, PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
