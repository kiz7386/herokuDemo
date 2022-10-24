package com.example.herokudemo.component.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class GateReceiver implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(GateReceiver.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> redisSerializer = stringRedisTemplate.getStringSerializer();
        String deserializer = redisSerializer.deserialize(message.getBody());
        logger.info("收到消息 ：" + deserializer);
    }
}
