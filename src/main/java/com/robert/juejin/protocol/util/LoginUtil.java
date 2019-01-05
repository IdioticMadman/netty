package com.robert.juejin.protocol.util;

import com.robert.juejin.protocol.attribute.Attributes;
import io.netty.channel.Channel;

public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.LOGIN);
    }
}