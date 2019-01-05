package com.robert.juejin.protocol.request.handler;

import com.robert.juejin.protocol.request.MessageRequestPacket;
import com.robert.juejin.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket msg) throws Exception {
        // 客户端发来消息
        MessageResponsePacket messageResponsePacket = receiverMsg(msg);
        ctx.channel().writeAndFlush(messageResponsePacket);
    }

    private MessageResponsePacket receiverMsg(MessageRequestPacket msg) {
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        System.out.println(new Date() + ": 收到客户端消息: " + msg.getMessage());
        messageResponsePacket.setMessage("服务端回复【" + msg.getMessage() + "】");
        return messageResponsePacket;
    }
}
