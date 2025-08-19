# Spring Boot + Kafka + Pact Broker (Local Demo)

This is a minimal hands-on project with two Spring Boot apps and message-based contract tests via Pact.

## Services
- **Kafka** in Docker (KRaft mode)
- **Kafka UI** on http://localhost:8081
- **Pact Broker** on http://localhost:9292 (Postgres-backed)

## Apps
- **producer-service**: REST `/orders` -> publishes to Kafka topic `orders`
- **consumer-service**: Listens on Kafka topic `orders`

## Quick start

1. Start infra:
```bash
cd docker
docker compose up -d
```

2. Generate consumer pact and publish to broker:
```bash
# From repo root
mvn -pl consumer-service test
mvn -Plocal-broker -pl consumer-service pact:publish
```

3. Provider verifies from broker:
```bash
mvn -Plocal-broker -pl producer-service pact:verify
```

4. Run both apps:
```bash
mvn -pl producer-service spring-boot:run
mvn -pl consumer-service spring-boot:run
```

5. Send a test order:
```bash
curl -X POST http://localhost:8080/orders   -H "Content-Type: application/json"   -d '{"orderId":"123","customerId":"C-999","amount":199.99}'
```

6. Inspect messages in Kafka UI at http://localhost:8081.

## Notes
- If you see `au.com.dius.pact.provider.junitsupport.verifier` not found, ensure the `producer-service` has both `junit5` **and** `junit5spring` test dependencies and that your local Maven cache is refreshed: `mvn -U -pl producer-service -DskipTests=false test`.
- Publishing to the same consumer version causes a 409 in Pact Broker. This build uses the short Git commit ID as the version. Use `-DprojectVersion=$(date +%s)` to override per publish.
- Dependencies target Java 17 and Spring Boot 3.3.x.

```shell
mvn -Dtest=*ConsumerPactTest -Dsurefire.failIfNoSpecifiedTests=false test
--
mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpact.test.version=001-consumer -Dpact.publish.consumer.branchName=feature/my-feature-001 -Dpact.consumer.tags=tag-001 pact:publish
--
mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-consumer -DpacticipantVersion=001-consumer -DtoEnvironment=test pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-consumer -DpacticipantVersion=001-consumer pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-consumer -Dlatest=true -DtoEnvironment=test pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-consumer -Dlatest=true pact:can-i-deploy
--
mvn -Dpactbroker.url=http://localhost:9292 -Dpactbroker.username=brokerUser -Dpactbroker.password=brokerPass -Dpact.verifier.publishResults=true -Dpact.provider.version=001-provider -Dpact.provider.tag=tag-001 -Dpact.provider.branch=feature/my-feature-001 -Dtest=*ProviderPactTest -Dsurefire.failIfNoSpecifiedTests=false test
--
mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-producer -DpacticipantVersion=001-provider -DtoEnvironment=test pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-producer -DpacticipantVersion=001-provider pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-producer -Dlatest=true -DtoEnvironment=test pact:can-i-deploy

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpacticipant=order-producer -Dlatest=true pact:can-i-deploy
--

mvn -Dpact.provider.host=localhost -Dpact.provider.port=9292 -Dpactbroker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpact.provider.name=order-producer -Dpact.provider.version=001-provider -Dpact.provider.branch=feature/my-feature-001 -Dpact.provider.tag=tag-001 -Dpact.verifier.publishResults=true pact:verify 

mvn -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpact.provider.name=order-producer -Dpact.provider.version=001-provider -Dpact.provider.branch=feature/my-feature-001 -Dpact.provider.tag=tag-001 -Dpact.verifier.publishResults=true pact:verify 

mvn -pl producer-service -Dpact.broker.url=http://localhost:9292 -Dpact.broker.username=brokerUser -Dpact.broker.password=brokerPass -Dpact.provider.name=order-producer -Dpact.test.version=001-provider -Dpact.provider.branch=feature/my-feature-001 -Dpact.provider.tag=tag-001 -Dpact.verifier.publishResults=true pact:verify 
```

```shell
docker run --rm \
  -e PACT_BROKER_BASE_URL=http://172.17.0.1:9292 \
  pactfoundation/pact-cli:latest \
  pact-broker record-deployment \
    --pacticipant order-consumer \
    --version 001-consumer \
    --environment test

docker run --rm \
  -e PACT_BROKER_BASE_URL=http://172.17.0.1:9292 \
  pactfoundation/pact-cli:latest \
  pact-broker record-deployment \
    --pacticipant order-producer \
    --version 002-provider \
    --environment test
```
