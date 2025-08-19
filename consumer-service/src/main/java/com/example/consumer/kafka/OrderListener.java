package com.example.consumer.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
  @KafkaListener(topics = "${app.topic}")
  public void handle(String message) {
    System.out.println("[consumer] got: " + message);
  }
}
