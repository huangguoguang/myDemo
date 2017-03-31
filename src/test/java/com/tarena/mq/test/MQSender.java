package com.tarena.mq.test;

public interface MQSender {
  void sendMsg(MQMessage message, String queueName);
}
