FROM gradle:jdk21-corretto AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x validateStructure -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -ms /bin/bash appuser && chown -R appuser:appuser /app
USER appuser

COPY --from=builder /app/applications/app-service/build/libs/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]