package com.robert.juejin.protocol.console.impl;

import com.robert.juejin.protocol.bean.CreateGroupRequestPacket;
import com.robert.juejin.protocol.console.ConsoleCommand;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CreateGroupConsoleCommand implements ConsoleCommand {

    private static final String USER_ID_SPLIT = ",";

    @Override
    public void exec(Scanner scanner, Channel channel) {
        CreateGroupRequestPacket packet = new CreateGroupRequestPacket();

        System.out.println("【拉人群聊】输入 userId 列表，userId 之间英文逗号隔开：");
        String userIds = scanner.nextLine();
        List<String> userIdList = Arrays.asList(userIds.split(USER_ID_SPLIT));
        packet.setUserIds(userIdList);
        channel.writeAndFlush(userIdList);
    }
}
