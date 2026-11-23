FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
ARG MODULO
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -pl ${MODULO} -am package -Dmaven.test.skip=true && cp ${MODULO}/target/*.jar /app/servicio.jar

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/servicio.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70"
COPY --chmod=755 deploy/start.sh /app/start.sh
ENTRYPOINT ["/app/start.sh"]
