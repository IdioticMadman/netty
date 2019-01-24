package com.robert.juejin.protocol;

import com.robert.juejin.protocol.bean.MessageRequestPacket;
import com.robert.juejin.protocol.console.ConsoleCommandManager;
import com.robert.juejin.protocol.console.impl.LoginConsoleCommand;
import com.robert.juejin.protocol.handler.Spliter;
import com.robert.juejin.protocol.handler.inbound.PacketDecoder;
import com.robert.juejin.protocol.handler.outbound.PacketEncoder;
import com.robert.juejin.protocol.response.handler.CreateGroupResponseHandler;
import com.robert.juejin.protocol.response.handler.LoginResponseHandler;
import com.robert.juejin.protocol.response.handler.MessageResponseHandler;
import com.robert.juejin.protocol.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;


    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    public void initChannel(NioSocketChannel ch) {
//                        ch.pipeline().addLast(new ClientHandler());
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("frameDecoder", new Spliter());
                        //解码
                        pipeline.addLast("packDecoder", new PacketDecoder());
                        //业务
                        pipeline.addLast("loginHandler", new LoginResponseHandler());
                        pipeline.addLast("messageHandler", new MessageResponseHandler());
                        pipeline.addLast("createGroupHandler", new CreateGroupResponseHandler());
                        //编码
                        pipeline.addLast("packEncoder", new PacketEncoder());
                    }
                });

        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 连接成功!");
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1),
                        delay, TimeUnit.SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        ConsoleCommandManager manager = new ConsoleCommandManager();
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();

        Scanner sc = new Scanner(System.in);
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!SessionUtil.hashSession(channel)) {
                    loginConsoleCommand.exec(sc, channel);
                } else {
                    String toUserId = sc.nextLine();
                    String message = sc.nextLine();
                    channel.writeAndFlush(new MessageRequestPacket(toUserId, message));
                }
            }
        }).start();
    }

}
