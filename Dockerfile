# syntax=docker/dockerfile:1.7          # ← важен BuildKit ≥ 1.4

# ── СТАДИЯ 1: билд при помощи Maven ──────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. только POM → позволяет кешировать зависимости
COPY pom.xml .

# ── кеш Maven (доступен во ВСЕХ последующих RUN) ──
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline       # качаем ЗАВИСИМОСТИ один раз

# 2. копируем исходники — меняются чаще
COPY src ./src

# 3. полноценная сборка, но зависимости уже в кеше
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package -DskipTests

# ── СТАДИЯ 2: минимальное рантайм-изображение ─────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
