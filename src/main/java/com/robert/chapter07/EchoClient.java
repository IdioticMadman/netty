package com.robert.chapter07;

import com.robert.bean.UserInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient {

    private static final String ECHO_REQ = "Hi, Robert. Welcome to Netty.$_";


    public static void main(String[] args) {
        int port = 8080;
        String host = "127.0.0.1";
        try {
            new EchoClient().connect(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect(String host, int port) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //设置线程池group，channel，响应handler
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast("msgpack decoder", new MessageDecoder());
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast("msgpack encoder", new MessageEncoder());
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });
            //异步connect连接，sync阻塞，等待连接成功
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //close，sync阻塞，等待连接成功
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static class EchoClientHandler extends ChannelInboundHandlerAdapter {

        private int counter;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //发送消息
//            for (int i = 0; i < 10; i++) {
//                ByteBuf msg = Unpooled.copiedBuffer(ECHO_REQ.getBytes());
//                ctx.writeAndFlush(msg);
//            }
            for (UserInfo user : users()) {
                ctx.write(user);
            }
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("This is " + ++counter + " times receive server : [" + msg + " ]");
            ctx.write(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        UserInfo[] users() {
            UserInfo[] users = new UserInfo[4];
            for (int i = 0; i < 4; i++) {
                users[i] = new UserInfo().buildUserId(i)
                        .buildUserName("robert-" + i);
            }
            return users;
        }
    }
}
