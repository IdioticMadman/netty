package com.robert.juejin.protocol.handler;

import com.robert.juejin.protocol.util.LoginUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (LoginUtil.hasLogin(ctx.channel())) {
            //当前已登录则移除这个认证
            ctx.channel().pipeline().remove(this);
            super.channelRead(ctx, msg);
        } else {
            //没登录直接关闭
            ctx.channel().close();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        if (LoginUtil.hasLogin(ctx.channel())) {
            System.out.println("当前连接登录验证完毕，无需再次验证, AuthHandler 被移除");
        } else {
            System.out.println("无登录验证，强制关闭连接!");
        }
    }
}
