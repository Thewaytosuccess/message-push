package com.push.config.socket;

import com.push.entity.Message;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xhzy
 */
@Component
//@ServerEndpoint(value = "/websocket/message/{userId}",configurator = WebSocketConfigurator.class)
@Slf4j
@EqualsAndHashCode
public class WebSocketServer {

    private Session session;

    private long userId;

    private final AtomicInteger memberCount = new AtomicInteger();

    private static final CopyOnWriteArraySet<WebSocketServer> CLIENTS = new CopyOnWriteArraySet<>();

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @OnOpen
    public void onOpen(@PathParam("userId") String userId,Session session, EndpointConfig config){
        this.session = session;
        this.userId = Long.parseLong(userId);

        CLIENTS.add(this);
        memberCount.incrementAndGet();
        log.info("当前在线人数：{}",getMemberCount());
        sendMsg("连接成功");
    }

    @SneakyThrows
    private void sendMsg(String msg, boolean...sync) {
        if(sync.length > 0 && !sync[0]){
            session.getAsyncRemote().sendText(msg);
        }else{
            session.getBasicRemote().sendText(msg);
        }
    }

    public int getMemberCount(){
        return memberCount.get();
    }

    @OnClose
    public void onClose(){
        CLIENTS.remove(this);
        memberCount.decrementAndGet();
        log.info("当前在线人数：{}",getMemberCount());
    }

    @OnMessage
    public void onMessage(String msg){
         log.info("message to client:{}",msg);
         //broadcast
         for(WebSocketServer server:CLIENTS){
             server.sendMsg(msg,false);
         }
    }

    /**
     * 外部调用
     * @param msg 消息内容
     */
    public static void sendMessage(Message msg){
        CLIENTS.stream().filter(e -> e.userId == msg.getReceiverId()).forEach(e -> e.sendMsg(msg.getBody(),false));
    }

    @OnError
    public void onError(Session session,Throwable e){
        log.error("on error:{}",e.getMessage());
    }
}
