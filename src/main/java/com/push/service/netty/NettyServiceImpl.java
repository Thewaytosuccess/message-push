package com.push.service.netty;

import com.push.config.netty.NettyServerHandler;
import com.push.entity.Message;
import com.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * kafka + netty + websocket
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
