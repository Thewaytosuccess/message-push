package com.push.config.mina;

import com.push.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xhzy
 */
@Slf4j
public class MinaServerHandler extends IoHandlerAdapter {

    private static CopyOnWriteArraySet<IoSession> clients;

    private final AtomicInteger memberCount = new AtomicInteger();

    public static void sendMessage(Message message){
        if(!CollectionUtils.isEmpty(clients)){
            //群发
            clients.forEach(e -> e.write(message.getBody()));
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        memberCount.incrementAndGet();
        log.info("在线人数：{}",memberCount.get());
        if(CollectionUtils.isEmpty(clients)){
            clients = new CopyOnWriteArraySet<>();
        }
        clients.add(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        clients.remove(session);
        memberCount.decrementAndGet();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }
}
