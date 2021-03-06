package com.push.service.message.websocket;

import com.push.config.socket.WebSocketServer;
import com.push.entity.Message;
import com.push.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * kafka + spring-boot-starter-websocket，仅适合H5推送
 * @author xhzy
 */
@Service
public class WebSocketServiceImpl implements MessageService {

    @Override
    public void push(Message msg) {
        if(StringUtils.hasText(msg.getBody())){
            WebSocketServer.sendMessage(msg);
        }
    }
}
