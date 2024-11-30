# 1 STAGE
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# 2 STAGE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Создание непривилегированного пользователя для безопасности
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/target/s21_community_bot-0.0.1-SNAPSHOT.jar /app/telegram-bot.jar

EXPOSE 5555

ARG DB_NAME
ARG DB_USER
ARG DB_PASSWORD

ENV DB_HOST=localhost
ENV DB_PORT=5432

ENV BOT_NAME=${BOT_NAME}
ENV BOT_TOKEN=${BOT_TOKEN}

ENV SCHOOL_USERNAME=${SCHOOL_USERNAME}
ENV SCHOOL_PASSWORD=${SCHOOL_PASSWORD}
ENV ROCKET_CHAT_PASSWORD=${ROCKET_CHAT_PASSWORD}

ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME -Dspring.datasource.username=$DB_USER -Dspring.datasource.password=$DB_PASSWORD -Dbot.token=$BOT_TOKEN -Dbot.username=$BOT_NAME -jar /app/telegram-bot.jar"]
