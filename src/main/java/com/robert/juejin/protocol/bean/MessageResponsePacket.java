package com.robert.juejin.protocol.bean;

import com.robert.juejin.protocol.command.Command;
import com.robert.juejin.protocol.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class MessageResponsePacket extends Packet {
    private String fromUserId;
    private String fromUserName;
    private String message;

    public MessageResponsePacket() {
    }

    public MessageResponsePacket(String fromUserId, String fromUserName, String message) {
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE;
    }
}