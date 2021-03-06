package com.robert.webserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sun.deploy.net.HttpRequest.CONTENT_LENGTH;
import static com.sun.deploy.net.HttpRequest.CONTENT_TYPE;
import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;
import static io.netty.handler.codec.http.HttpHeaderNames.SET_COOKIE;

public class HttpDemoServerHandler extends SimpleChannelInboundHandler<HttpObject> {
 
    private static final Logger logger = Logger.getLogger(HttpDemoServerHandler.class.getName());
 
    private DefaultFullHttpRequest fullHttpRequest;
 
    private boolean readingChunks;
 
    private final StringBuilder responseContent = new StringBuilder();
 
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk
 
    private HttpPostRequestDecoder decoder;
 
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }
 
    public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        fullHttpRequest = (DefaultFullHttpRequest) msg;
 
        /**
         * 在服务器端打印请求信息
         */
        System.out.println("VERSION: " + fullHttpRequest.protocolVersion().text() + "\r\n");
        System.out.println("REQUEST_URI: " + fullHttpRequest.uri() + "\r\n\r\n");
        System.out.println("\r\n\r\n");
        for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
            System.out.println("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
        }
 
        /**
         * 服务器端返回信息
         */
        responseContent.setLength(0);
        responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
        responseContent.append("===================================\r\n");
 
        responseContent.append("VERSION: ").append(fullHttpRequest.protocolVersion().text()).append("\r\n");
        responseContent.append("REQUEST_URI: ").append(fullHttpRequest.uri()).append("\r\n\r\n");
        responseContent.append("\r\n\r\n");
        for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
            responseContent.append("HEADER: ").append(entry.getKey()).append('=').append(entry.getValue()).append("\r\n");
        }
        responseContent.append("\r\n\r\n");
        Set<Cookie> cookies;
        String value = fullHttpRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        for (Cookie cookie : cookies) {
            responseContent.append("COOKIE: ").append(cookie.toString()).append("\r\n");
        }
        responseContent.append("\r\n\r\n");
 
        if (fullHttpRequest.getMethod().equals(HttpMethod.GET)) {
            //get请求
            QueryStringDecoder decoderQuery = new QueryStringDecoder(fullHttpRequest.getUri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                    responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
                }
            }
            responseContent.append("\r\n\r\n");
 
            responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
            writeResponse(ctx.channel());
            return;
        } else if (fullHttpRequest.getMethod().equals(HttpMethod.POST)) {
            //post请求
            decoder = new HttpPostRequestDecoder(factory, fullHttpRequest);
            readingChunks = HttpHeaders.isTransferEncodingChunked(fullHttpRequest);
            responseContent.append("Is Chunked: " + readingChunks + "\r\n");
            responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");
 
            try {
                while (decoder.hasNext()) {
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        try {
                            writeHttpData(data);
                        } finally {
                            data.release();
                        }
                    }
                }
            } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
                responseContent.append("\r\n\r\nEND OF POST CONTENT\r\n\r\n");
            }
            writeResponse(ctx.channel());
            return;
        } else {
            System.out.println("discard.......");
            return;
        }
    }
 
    private void reset() {
        fullHttpRequest = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }
 
    private void writeHttpData(InterfaceHttpData data) {
 
        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                e1.printStackTrace();
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return;
            }
            if (value.length() > 100) {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " data too long\r\n");
            } else {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.toString() + "\r\n");
            }
        }
    }
 
 
    /**
     * http返回响应数据
     *
     * @param channel
     */
    private void writeResponse(Channel channel) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);
 
        // Decide whether to close the connection or not.
        boolean close = fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
                || fullHttpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
 
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
 
        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }
 
        Set<Cookie> cookies;
        String value = fullHttpRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (Cookie cookie : cookies) {
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
            }
        }
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING, responseContent.toString(), cause);
        ctx.channel().close();
    }
 
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        messageReceived(ctx, msg);
    }
}