package com.example.producer.web;

import com.example.producer.kafka.OrderProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderProducer producer;
  private final ObjectMapper om = new ObjectMapper();

  public OrderController(OrderProducer producer) {
    this.producer = producer;
  }

  @PostMapping
  public Map<String, Object> create(@RequestBody Map<String, Object> req) throws Exception {
    String json = om.writeValueAsString(req);
    producer.send("orders", json);
    return Map.of("status", "sent", "payload", req);
  }
}
