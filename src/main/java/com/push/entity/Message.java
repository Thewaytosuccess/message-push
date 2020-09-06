package com.push.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author xhzy
 */
@Data
public class Message {

    /**
     * 消息id
     */
    private String id;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 接收人id
     */
    private long receiverId;

    /**
     * 接收人需要处理的业务类型
     */
    private int bizType;

    /**
     * 接收人需要处理的业务id
     */
    private long bizId;
}
