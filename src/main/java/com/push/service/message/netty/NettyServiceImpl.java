package com.push.service.message.netty;

import com.push.config.netty.NettyServerHandler;
import com.push.entity.Message;
import com.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * kafka + netty + websocket/client,同时适合H5和移动端推送
 * @author xhzy
 */
@Slf4j
@Service
public class NettyServiceImpl implements MessageService {

    @Override
    public void push(Message msg) {
        if(StringUtils.hasText(msg.getBody())){
            NettyServerHandler.sendMessage(msg);
        }
    }
}
