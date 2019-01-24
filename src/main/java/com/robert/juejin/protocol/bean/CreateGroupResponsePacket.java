package com.robert.juejin.protocol.bean;

import com.robert.juejin.protocol.command.Command;
import com.robert.juejin.protocol.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateGroupResponsePacket extends Packet {

    boolean success;

    String groupId;

    List<String> userNames;

    @Override
    public Byte getCommand() {
        return Command.CREATE_GROUP_REQUEST;
    }


}
