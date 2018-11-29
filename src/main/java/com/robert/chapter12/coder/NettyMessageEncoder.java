package com.robert.chapter12.coder;

import com.robert.chapter12.bean.NettyMessage;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    public NettyMessageEncoder() {

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {

    }
}
