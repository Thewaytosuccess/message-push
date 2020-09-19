package com.push.service.message.mina;

import com.push.config.mina.MinaServerHandler;
import com.push.entity.Message;
import com.push.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author xhzy
 */
@Service
public class MinaServiceImpl implements MessageService {

    @Override
    public void push(Message message) {
        if(StringUtils.hasText(message.getBody())){
            MinaServerHandler.sendMessage(message);
        }
    }
}
