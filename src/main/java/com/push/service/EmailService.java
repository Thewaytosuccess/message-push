package com.push.service;

import com.push.entity.Message;

/**
 * 邮件服务
 * @author xhzy
 */
public interface EmailService {

    /**
     * 发送邮件
     * @param message 消息内容
     */
    void send(Message message);
}
