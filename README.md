# Simple Banking System (Spring Boot + Web UI)

Java 17, Maven, Spring Boot 3, JUnit 5, Jenkins pipeline.

Features: open/close accounts, deposit, withdraw, transfer, statements. Web UI at `/`, REST API at `/api`.

## Run
```bash
mvn clean verify
java -jar target/banking-system-1.0.0.jar
# open http://localhost:8080
```
Or during development: `mvn spring-boot:run`

A console version still exists: `com.example.bank.Main` (run from your IDE).

## REST API
| Method | Path | Body |
|---|---|---|
| GET | /api/accounts | |
| POST | /api/accounts | `{"holderName":"Priya"}` |
| POST | /api/accounts/{no}/deposit | `{"amount":100}` |
| POST | /api/accounts/{no}/withdraw | `{"amount":50}` |
| POST | /api/transfer | `{"from":"ACC1001","to":"ACC1002","amount":25}` |
| GET | /api/accounts/{no}/transactions | |
| DELETE | /api/accounts/{no} | |

Data is in-memory and resets when the app restarts.

## Jenkins
Needs tools named `maven3` and `jdk17` (Manage Jenkins -> Tools).
Pipeline job -> "Pipeline script from SCM" -> this repo -> `Jenkinsfile`.
