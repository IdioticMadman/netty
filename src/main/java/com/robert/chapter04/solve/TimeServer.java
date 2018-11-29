package com.robert.chapter04.solve;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        try {
            new TimeServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bind(int port) throws Exception {
        //配置NIO的线程组, 实际上就是Reactor线程组
        //
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //启动辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //将两个线程组设置到bootStrap，一个是acceptor一个是client
            //acceptor用于接受客户端的连接，client用于socketChannel的读写
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)//对应Java中的NIO
                    .option(ChannelOption.SO_BACKLOG, 1024)//TCP最大连接数
                    //相当于Java NIO的handler：处理网络的IO事件，例如：记录日志，对消息进行编解码
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel sc) throws Exception {
            ChannelPipeline pipeline = sc.pipeline();
            pipeline.addLast(new LineBasedFrameDecoder(1024));
            pipeline.addLast(new StringDecoder());
            pipeline.addLast(new TimeServerHandler());

        }
    }
}
