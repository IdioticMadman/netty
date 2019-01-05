package com.robert.juejin.protocol.packet;

import lombok.Data;

@Data
public abstract class Packet {
    private Byte version = 1;

    /**
     * 获取指令
     *
     * @return 指令
     */
    public abstract Byte getCommand();
}
