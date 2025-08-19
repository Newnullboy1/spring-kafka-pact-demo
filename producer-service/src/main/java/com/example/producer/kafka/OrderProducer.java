package com.example.producer.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {
  private final KafkaTemplate<String, String> template;

  public OrderProducer(KafkaTemplate<String, String> template) {
    this.template = template;
  }

  public void send(String topic, String json) {
    template.send(topic, json);
  }
}
