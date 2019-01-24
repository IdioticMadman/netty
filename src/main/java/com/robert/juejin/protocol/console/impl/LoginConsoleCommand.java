package com.robert.juejin.protocol.console.impl;

import com.robert.juejin.protocol.bean.LoginRequestPacket;
import com.robert.juejin.protocol.console.ConsoleCommand;
import io.netty.channel.Channel;

import java.util.Scanner;

public class LoginConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        System.out.println("请输入用户名登录: ");
        LoginRequestPacket packet = new LoginRequestPacket();
        String line = scanner.nextLine();
        packet.setUsername(line);
        packet.setPassword("pwd");
        channel.writeAndFlush(packet);
        waitForLogin();
    }

    private static void waitForLogin() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
