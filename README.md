# EventFlow Orchestrator

A distributed microservices platform demonstrating the **Saga Pattern**, **Kafka Messaging**, and **Database Locking**.

## Setup & Run

1. **Start Infrastructure:**
   docker-compose up -d

2. **Build:**
   mvn clean install -DskipTests

3. **Seed Inventory:**
   docker exec -i eventflow-postgres psql -U user -d inventory_db -c "INSERT INTO inventory (product_id, available_quantity) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 100);"

4. **Run Services (3 Terminals):**
   mvn spring-boot:run -f order-orchestrator/pom.xml
   mvn spring-boot:run -f payment-service/pom.xml
   mvn spring-boot:run -f inventory-service/pom.xml

Test Case:
curl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "productId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "quantity": 1,
  "totalAmount": 500.00
}'