package com.example.producer.pact;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;

import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("order-producer")
@PactBroker(
        url = "${pactbroker.url}",
        authentication = @PactBrokerAuth(
                username = "${pactbroker.username}",
                password = "${pactbroker.password}"
        ))
class OrderMessageProviderPactTest {

  @BeforeEach
  void before(PactVerificationContext context) {
    if (context != null) context.setTarget(new MessageTestTarget());
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTests(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("default state")
  void defaultState() { /* set up state if needed */ }

  @PactVerifyProvider("order created message")
  String verifyOrderCreatedMessage() {
    return "{"
      + "\"orderId\":\"123\","
      + "\"customerId\":\"C-999\","
      + "\"amount\":199.99"
      + "}";
  }
}
