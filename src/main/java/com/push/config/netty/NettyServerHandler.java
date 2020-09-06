package com.push.config.netty;

import com.push.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.push.constant.ConstantPool.*;

/**
 * @author xhzy
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    @Value("${netty.server.host}")
    private String host;

    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.server.uri-prefix}")
    private String uriPrefix;

    private final AtomicInteger memberCount = new AtomicInteger();

    /**
     * 客户端群组
     */
    private static ChannelGroup clients;

    private WebSocketServerHandshaker handShaker;

    public static void sendMessage(Message msg) {
        if(Objects.nonNull(clients) && !clients.isEmpty()){
            log.info("netty push start===");
            clients.stream().filter(e -> msg.getReceiverId() == Long.parseLong(e.attr(
                    AttributeKey.valueOf(e.id().asLongText())).get().toString())).
                    forEach(e -> e.writeAndFlush(new TextWebSocketFrame(msg.getBody())));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        memberCount.incrementAndGet();
        log.info("在线人数：{}",memberCount.get());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        //第一次握手请求为http请求
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }

        if(Objects.isNull(clients)){
            clients = new DefaultChannelGroup(ctx.executor());
        }
        clients.add(ctx.channel());

        //之后的请求为webSocket请求
        if(msg instanceof WebSocketFrame){
             handleWebSocketRequest(ctx,(WebSocketFrame)msg);
        }
    }

    private void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //关闭socket连接
        if(msg instanceof CloseWebSocketFrame){
           handShaker.close(ctx.channel(),(CloseWebSocketFrame)msg.retain());
           clients.remove(ctx.channel());
           memberCount.decrementAndGet();
           log.info("当前在线人数：{}",memberCount.get());
           return;
        }

        //心跳检测
        if(msg instanceof PingWebSocketFrame){
            ctx.channel().writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            return;
        }

        //support text msg only
        if(!(msg instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException("only text msg is supported.");
        }
        clients.writeAndFlush(new TextWebSocketFrame("response from server"));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        Map<String,Object> map = getRequestParams(uri);
        if(Objects.isNull(map) || Objects.isNull(map.get(USER_ID))){
            throw new IllegalArgumentException("illegal request");
        }

        ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText())).
                setIfAbsent(Long.parseLong(map.get(USER_ID).toString()));

        log.info("http userId = {}",ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText())).get());
        //判断请求头中是否有upgrade
        if(!request.decoderResult().isSuccess() || !WEB_SOCKET.equals(request.headers().get(UPGRADE))){
            sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //建立握手连接
        WebSocketServerHandshakerFactory handShakerFactory = new WebSocketServerHandshakerFactory(
                "ws://"+host+":"+port+"/"+uriPrefix+"/message", null, false);
        handShaker = handShakerFactory.newHandshaker(request);
        if(Objects.isNull(handShaker)){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handShaker.handshake(ctx.channel(),request);
        }
    }

    private Map<String,Object> getRequestParams(String uri) {
        if(uri.contains(QUESTION_SIGN)){
            Map<String,Object> map = new HashMap<>(MAP_SIZE);
            uri = uri.substring(uri.indexOf(QUESTION_SIGN)+1);

            String[] pa;
            if(uri.contains(AND_SIGN)){
                String[] params = uri.split(AND_SIGN);
                for(String s:params){
                    pa = s.split(EQUAL_SIGN);
                    if(pa.length > 1){
                        map.put(pa[0],pa[1]);
                    }
                }
            }else{
                pa = uri.split(EQUAL_SIGN);
                if(pa.length > 1){
                    map.put(pa[0],pa[1]);
                }
            }
            return map;
        }
        return null;
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
       if(response.status().code() != HttpStatus.OK.value()){
           ByteBuf buffer = ctx.alloc().buffer();
           buffer.writeBytes("request is error".getBytes());
           response.content().writableBytes();
           buffer.release();
       }

       ChannelFuture future = ctx.channel().writeAndFlush(response);
       if(Objects.isNull(request.headers().get(KEEP_ALIVE))){
           future.addListener(ChannelFutureListener.CLOSE);
       }
    }
}
