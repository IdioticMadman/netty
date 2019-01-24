package com.robert.juejin.protocol.request.handler;

import com.robert.juejin.protocol.bean.CreateGroupRequestPacket;
import com.robert.juejin.protocol.bean.CreateGroupResponsePacket;
import com.robert.juejin.protocol.util.IDUtil;
import com.robert.juejin.protocol.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupRequestPacket msg) throws Exception {
        List<String> userIds = msg.getUserIds();

        List<String> userNames = new ArrayList<>();
        //1. 创建一个channelGroup
        ChannelGroup group = new DefaultChannelGroup(ctx.executor());
        //2. 筛选触待加入群聊的用户的channel和username
        for (String userId : userIds) {
            Channel channel = SessionUtil.getChannel(userId);
            if (channel != null) {
                group.add(channel);
                userNames.add(SessionUtil.getSession(channel).getUserName());
            }
        }
        //3. 创建群聊创建结果响应
        CreateGroupResponsePacket createGroupResponsePacket = new CreateGroupResponsePacket();
        createGroupResponsePacket.setGroupId(IDUtil.randomUserId());
        createGroupResponsePacket.setSuccess(true);
        createGroupResponsePacket.setUserNames(userNames);
        //4. 给每个客户发送拉群通知
        group.writeAndFlush(createGroupResponsePacket);
        System.out.println("群创建成功，id为[ " + createGroupResponsePacket.getGroupId() + " ]");
        System.out.println("群里面有" + createGroupResponsePacket.getUserNames());
    }
}
