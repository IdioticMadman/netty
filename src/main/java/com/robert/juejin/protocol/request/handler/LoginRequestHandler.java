package com.robert.juejin.protocol.request.handler;

import com.robert.juejin.protocol.request.LoginRequestPacket;
import com.robert.juejin.protocol.response.LoginResponsePacket;
import com.robert.juejin.protocol.util.LoginUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
        // 登录流程
        LoginResponsePacket loginResponsePacket = login(msg);
        if (loginResponsePacket.isSuccess()) {
            LoginUtil.markAsLogin(ctx.channel());
        }
        // 登录响应
        ctx.channel().writeAndFlush(loginResponsePacket);
    }

    private LoginResponsePacket login(LoginRequestPacket msg) {
        LoginResponsePacket response = new LoginResponsePacket();
        response.setVersion(msg.getVersion());
        if (valid(msg)) {
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
}
