package com.robert.juejin.protocol.bean;

import com.robert.juejin.protocol.command.Command;
import com.robert.juejin.protocol.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String userId;
    private String userName;
    private String reason;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}