package com.tarena.mq.test;

import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSenderImpl implements MQSender {
    
    @Autowired
    private AmqpTemplate amqpTemplate;
    
    @Override
    public void sendMsg(MQMessage message, String queueName) {
        System.out.println("进入 ");
        if(message==null){
            System.out.println("消息发送失败：消息为null");
            return;
        }
        if(message.getId()==null){
            message.setId(UUID.randomUUID().toString());            
        }
        amqpTemplate.convertAndSend(queueName, message);
    }
}

/*
    convertAndSend：将Java对象转换为消息发送到匹配Key的交换机中Exchange，由于配置了JSON转换，这里是将Java对象转换成JSON字符串的形式。
　　原文：Convert a Java object to an Amqp Message and send it to a default exchange with a specific routing key.
*/