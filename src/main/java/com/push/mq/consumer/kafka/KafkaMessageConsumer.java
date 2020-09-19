package com.push.mq.consumer.kafka;

import com.alibaba.fastjson.JSON;
import com.push.entity.Message;
import com.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xhzy
 */
@Component
@Slf4j
public class KafkaMessageConsumer {

    @Resource(name="nettyServiceImpl")
    private MessageService messageService;

    @KafkaListener(topics = {"message-push"})
    public void receive(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("record =" + record);
            log.info("message =" + message);

            Message msg = JSON.parseObject(message.toString(), Message.class);
            if(Objects.isNull(msg) || StringUtils.isEmpty(msg.getBody())){
                log.info("json parse error");
                return ;
            }
            messageService.push(msg);
        }
    }


}
