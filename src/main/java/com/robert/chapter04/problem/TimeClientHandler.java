package com.robert.chapter04.problem;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

    private final byte[] mReq;

    public TimeClientHandler() {
        mReq = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    //TCP链路连接成功
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送消息
        for (int i = 0; i < 100; i++) {
            ByteBuf msg = Unpooled.buffer(mReq.length);
            msg.writeBytes(mReq);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] resp = new byte[buf.readableBytes()];
        buf.readBytes(resp);
        String body = new String(resp, StandardCharsets.UTF_8);
        System.out.println("Now is :" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
