package com.robert.juejin.protocol.request;

import com.robert.juejin.protocol.command.Command;
import com.robert.juejin.protocol.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class MessageRequestPacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST;
    }
}