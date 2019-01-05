package com.robert.juejin.protocol.util;

import com.robert.juejin.protocol.attribute.Attributes;
import com.robert.juejin.protocol.bean.Session;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SessionUtil {

    private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    /**
     * 绑定session
     *
     * @param session
     * @param channel
     */
    public static void bindSession(Session session, Channel channel) {
        userIdChannelMap.put(session.getUserId(), channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    /**
     * 解绑session
     *
     * @param channel
     */
    public static void unBindSession(Channel channel) {
        if (hashSession(channel)) {
            userIdChannelMap.remove(getSession(channel).getUserId());
            channel.attr(Attributes.SESSION).set(null);
        }
    }

    /**
     * 当前Channel是否含有Session
     *
     * @param channel
     * @return
     */
    public static boolean hashSession(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }

    /**
     * 获取当前Channel对应的session
     *
     * @param channel
     * @return
     */
    public static Session getSession(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    /**
     * 根据用户Id来获取Channel
     *
     * @param userId
     * @return
     */
    public static Channel getChannel(String userId) {
        return userIdChannelMap.get(userId);
    }
}
