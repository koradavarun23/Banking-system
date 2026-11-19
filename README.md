# Simple Banking System

Console banking app in Java 17 with Maven, JUnit 5 tests and a Jenkins pipeline.

Features: open/close accounts, deposit, withdraw, transfer, balance, statement.

## Run locally
```bash
mvn clean verify
java -jar target/banking-system-1.0.0.jar
```

## Jenkins
Needs tools named `maven3` and `jdk17` (Manage Jenkins -> Tools).
Create a Pipeline job -> "Pipeline script from SCM" -> this repo -> `Jenkinsfile`.
