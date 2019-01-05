package com.robert.juejin.protocol.handler;

import com.robert.juejin.protocol.packet.Packet;
import com.robert.juejin.protocol.packet.PacketCodeC;
import com.robert.juejin.protocol.bean.LoginRequestPacket;
import com.robert.juejin.protocol.bean.MessageRequestPacket;
import com.robert.juejin.protocol.bean.LoginResponsePacket;
import com.robert.juejin.protocol.bean.MessageResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new Date() + ": 客户端开始登录……");
        ByteBuf requestByteBuf = (ByteBuf) msg;

        Packet packet = PacketCodeC.INSTANCE.decode(requestByteBuf);

        if (packet instanceof LoginRequestPacket) {
            // 登录流程
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;

            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(packet.getVersion());
            if (valid(loginRequestPacket)) {
                loginResponsePacket.setSuccess(true);
                System.out.println(new Date() + ": 登录成功!");
            } else {
                loginResponsePacket.setReason("账号密码校验失败");
                loginResponsePacket.setSuccess(false);
                System.out.println(new Date() + ": 登录失败!");
            }
            // 登录响应
            ctx.channel().writeAndFlush(loginRequestPacket);
        } else if (packet instanceof MessageRequestPacket) {
            // 客户端发来消息
            MessageRequestPacket messageRequestPacket = ((MessageRequestPacket) packet);
            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());
            messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
            ctx.channel().writeAndFlush(packet);
        }
//        super.channelRead(ctx, msg);
    }

    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }
}
