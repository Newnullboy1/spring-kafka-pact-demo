package com.example.consumer.pact;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.v4.MessageContents;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
        providerName = "order-producer",
        providerType = ProviderType.ASYNCH,          // async messages
        pactVersion = PactSpecVersion.V4             // force V4
)
public class OrderMessageConsumerPactTest {

    @Pact(consumer = "order-consumer")
    V4Pact orderCreated(PactBuilder builder) {
        return builder
                // Using the V4 builder for an async message. `usingLegacyMessageDsl()` is OK for simple JSON.
                .usingLegacyMessageDsl()
                .expectsToReceive("order created message")
                .withMetadata(Map.of("contentType", "application/json"))
                .withContent("{\"orderId\":\"123\",\"customerId\":\"C-999\",\"amount\":199.99}")
                .toPact(); // V4Pact is inferred by the method return type
    }

    @Test
    @PactTestFor(pactMethod = "orderCreated")
    void validateMessageContents(List<V4Interaction.AsynchronousMessage> interactions) {
        // Take the first interaction and get its V4 MessageContents
        MessageContents contents = interactions.get(0).getContents();
        String json = new String(contents.getContents().getValue(), StandardCharsets.UTF_8);

        System.out.println("V4 Pact message: " + json);
        // TODO: parse with Jackson and assert fields
    }
}
