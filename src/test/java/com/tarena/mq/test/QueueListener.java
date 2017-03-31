package com.tarena.mq.test;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class QueueListener implements MessageListener {

  @Override
  public void onMessage(Message message) {
    System.out.println("aaaaaaaaaaaaaaaaaa");
    System.out.println(message.getMessageProperties());
  }

}