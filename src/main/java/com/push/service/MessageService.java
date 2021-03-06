package com.push.service;

import com.push.entity.Message;

/**
 * 消息服务
 * @author xhzy
 */
public interface MessageService {

    /**
     * 消息推送
     * @param message 消息内容
     */
    void push(Message message);

}
