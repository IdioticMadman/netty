package com.robert.juejin.protocol.packet;

import com.robert.juejin.protocol.command.Command;
import com.robert.juejin.protocol.request.LoginRequestPacket;
import com.robert.juejin.protocol.request.MessageRequestPacket;
import com.robert.juejin.protocol.response.LoginResponsePacket;
import com.robert.juejin.protocol.response.MessageResponsePacket;
import com.robert.juejin.protocol.serialize.Serializer;
import com.robert.juejin.protocol.serialize.json.JSONSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class PacketCodeC {
    public static final int MAGIC_NUMBER = 0x12345678;
    //cmd-packet
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;
    //serializerAlgorithm-serializer
    private static final Map<Byte, Serializer> serializerMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(Command.LOGIN_RESPONSE, LoginResponsePacket.class);
        packetTypeMap.put(Command.MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(Command.MESSAGE_RESPONSE, MessageResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
    }

    public static final PacketCodeC INSTANCE = new PacketCodeC();

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {
        // 1. 序列化 java 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 2. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    /**
     * 获取序列化对象
     */
    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    /**
     * 获取要被反序列化的class类型
     */
    private Class<? extends Packet> getRequestType(byte command) {
        return packetTypeMap.get(command);
    }
}
