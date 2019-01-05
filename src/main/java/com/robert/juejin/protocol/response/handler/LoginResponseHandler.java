package com.robert.juejin.protocol.response.handler;

import com.robert.juejin.protocol.bean.LoginResponsePacket;
import com.robert.juejin.protocol.bean.Session;
import com.robert.juejin.protocol.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket msg) throws Exception {
        String userId = msg.getUserId();
        String userName = msg.getUserName();
        if (msg.isSuccess()) {
            System.out.println("[" + userName + "]登录成功，userId 为: " + msg.getUserId());
            SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
        } else {
            System.out.println("[" + userName + "]登录失败，原因：" + msg.getReason());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("客户端连接被关闭");
    }
}
