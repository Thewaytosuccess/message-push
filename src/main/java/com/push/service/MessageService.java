package com.push.service;

import com.push.entity.Message;

/**
 * @author xhzy
 */
public interface MessageService {

    /**
     * 消息推送
     * @param message 消息内容
     */
    void push(Message message);
}
