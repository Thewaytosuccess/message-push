package com.push.entity;

import com.push.enums.BizTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author xhzy
 */
@Data
public class Message {

    /**
     * 消息id，非空
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容，非空
     */
    private String body;

    /**
     * 发送时间，非空
     */
    private Date sendTime;

    /**
     * 接收人id
     * 当点对点推送时，此参数不可少
     */
    private long receiverId;

    /**
     * 消息的类型：text/email，非空
     * text:文本消息
     * email:邮件消息
     */
    private int type;

    /**
     * 适用范围：p2p/broadcast，非空
     * p2p:推送给个人
     * broadcast:广播，推送给所有人
     */
    private int scope;

    /**
     * 发送渠道：Web/App/WeChat/DingDing/ShortMessage
     * 当messageType=text时，此参数不可少，用于选择发送渠道
     */
    private int channelId;

    /**
     * 业务类型,对应枚举类BizTypeEnum
     * 当messageType=email时，此参数不可少，用于选择邮件模板
     */
    private BizTypeEnum bizType;
}
