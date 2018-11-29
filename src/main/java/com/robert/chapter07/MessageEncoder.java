package com.robert.chapter07;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            MessagePack pack = new MessagePack();
            byte[] outBytes = pack.write(msg);
            out.writeBytes(outBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
