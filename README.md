# kafkahazelapp
Sampe Kafka + Hazel app

Run:

From the main directory:

1. Start Zookeeper and Kafka on docker

docker-compose up

2. Start the microservices

mvn spring-boot:run


3. Call a test endpoint (creates a sample message and pushes onto a Kafka topic:

curl localhost:8080/test


Additional endpoints:

Hazelcast Service:

curl localhost:8080/write?id={id}&json={json}

curl localhost:8080/read?id={id}

