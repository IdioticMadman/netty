package com.robert.pratice.udp;


/**
 * 提供消息
 */
public class MessageFactory {

    private static final String SN_HEADER = "收到暗号，我是（SN）:";
    private static final String PORT_HEADER = "这是暗号，请回电端口(Port):";

    /**
     * 用端口创建Searcher发送的消息
     *
     * @param port
     * @return
     */
    public static String buildMsgWithPort(int port) {
        return PORT_HEADER + port;
    }

    /**
     * Provider通过msg解析Searcher监听的端口
     *
     * @param msg
     * @return
     */
    public static int parsePortByMsg(String msg) {
        if (msg.startsWith(PORT_HEADER)) {
            return Integer.parseInt(msg.substring(PORT_HEADER.length()));
        }
        return -1;
    }

    /**
     * Provider通过自己的SN创建回送给Searcher的消息
     *
     * @param sn
     * @return
     */
    public static String buildMsgWithSn(String sn) {
        return SN_HEADER + sn;
    }

    /**
     * Searcher通过msg获取设备的sn
     *
     * @param msg
     * @return
     */
    public static String parseSnByMsg(String msg) {
        if (msg.startsWith(SN_HEADER)) {
            return msg.substring(SN_HEADER.length());
        }
        return "";
    }

}
