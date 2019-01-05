package com.robert.juejin.protocol.request.handler;

import com.robert.juejin.protocol.bean.MessageRequestPacket;
import com.robert.juejin.protocol.bean.MessageResponsePacket;
import com.robert.juejin.protocol.bean.Session;
import com.robert.juejin.protocol.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket msg) throws Exception {
        System.out.println(new Date() + ": 收到客户端消息: " + msg.getMessage());
        //1. 获取当前channel对应的session
        Session session = SessionUtil.getSession(ctx.channel());
        //2. 构建回送消息
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket(session.getUserId(),
                session.getUserName(), msg.getMessage());
        //3. 查找对方的channel
        Channel toUserChannel = SessionUtil.getChannel(msg.getToUserId());
        //4. 判断对应的channel是否合法
        if (toUserChannel != null && SessionUtil.hashSession(toUserChannel)) {
            //5. 发出
            toUserChannel.writeAndFlush(messageResponsePacket);
        } else {
            System.err.println("[" + msg.getToUserId() + "] 不在线，发送失败!");
        }
    }
}
