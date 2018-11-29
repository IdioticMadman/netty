package com.robert.chapter07;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    public static void main(String[] args) {
        int port = 8080;
        try {
            new EchoServer().bind(port);
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
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //相当于Java NIO的handler：处理网络的IO事件，例如：记录日志，对消息进行编解码
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //解码的时候通过头部
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
                                    0, 2, 0, 2));
                            pipeline.addLast("msgpack decoder", new MessageDecoder());
                            //编码的时候加两个字节头，做标识
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast("msgpack encoder", new MessageEncoder());
                            pipeline.addLast(new EchoServerHandler());
                        }
                    });
            //异步绑定端口，同步阻塞，等待绑定成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    private static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        int counter = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("This is " + ++counter + " times receiver client : [ " + msg + " ]");
            ctx.writeAndFlush(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
