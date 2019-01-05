package com.robert.juejin.protocol.request.handler;

import com.robert.juejin.protocol.bean.LoginRequestPacket;
import com.robert.juejin.protocol.bean.LoginResponsePacket;
import com.robert.juejin.protocol.bean.Session;
import com.robert.juejin.protocol.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;
import java.util.UUID;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
        // 登录流程
        LoginResponsePacket packet = login(msg);
        if (packet.isSuccess()) {
            SessionUtil.bindSession(new Session(packet.getUserId(), packet.getUserName()), ctx.channel());
        }
        // 登录响应
        ctx.channel().writeAndFlush(packet);
    }

    private String randomUserId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    private LoginResponsePacket login(LoginRequestPacket msg) {
        LoginResponsePacket response = new LoginResponsePacket();
        response.setVersion(msg.getVersion());
        if (valid(msg)) {
            response.setUserId(randomUserId());
            response.setUserName(msg.getUsername());
            response.setSuccess(true);
            System.out.println(new Date() + ": 登录成功!");
        } else {
            response.setReason("账号密码校验失败");
            response.setSuccess(false);
            System.out.println(new Date() + ": 登录失败!");
        }
        return response;
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
    }
}
