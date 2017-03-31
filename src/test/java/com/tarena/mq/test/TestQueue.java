package com.tarena.mq.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarena.baseTest.SpringTestCase;

public class TestQueue extends SpringTestCase{
  
  @Autowired
  private MQSender mqsender;

  
  public void setMqSender(MQSender mqsender) {
    this.mqsender = mqsender;
  }

  final String queueName = "test_queue_routing_key";

  @Test
  public void send(){
    System.out.println("ampq");
      MQMessage message = new MQMessage();
      message.setBody("大家好，我是hjzgg!!!");
      mqsender.sendMsg(message, queueName);
  }
}
