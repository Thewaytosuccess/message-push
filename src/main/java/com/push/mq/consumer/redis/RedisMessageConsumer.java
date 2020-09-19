package com.push.mq.consumer.redis;

import com.alibaba.fastjson.JSON;
import com.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author xhzy
 */
@Component
@Slf4j
public class RedisMessageConsumer extends MessageListenerAdapter {

    @Value("${mq.message.topic}")
    private String topic;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name="minaServiceImpl")
    private MessageService messageService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        stringRedisTemplate.setEnableDefaultSerializer(true);
        RedisSerializer<String> stringSerializer = stringRedisTemplate.getStringSerializer();
        String text = stringSerializer.deserialize(body);
        String topic = stringSerializer.deserialize(channel);
        log.info("topic={},msg={}",topic,text);

        com.push.entity.Message msg = JSON.parseObject(text, com.push.entity.Message.class);
        if(Objects.nonNull(msg)){
            messageService.push(msg);
        }
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory,
                                                   MessageListenerAdapter adapter){
       RedisMessageListenerContainer container = new RedisMessageListenerContainer();
       container.setConnectionFactory(factory);
       container.addMessageListener(adapter,new PatternTopic(topic));
       return container;
    }
}
