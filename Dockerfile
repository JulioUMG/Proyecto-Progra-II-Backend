# Compila el backend con Java 21.
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# La imagen final solo necesita Java y el archivo compilado.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system hotel && useradd --system --gid hotel hotel
COPY --from=compilacion /app/target/hotel404-0.0.1-SNAPSHOT.jar app.jar
COPY iniciar-render.sh .
RUN sed -i 's/\r$//' iniciar-render.sh && chmod +x iniciar-render.sh
USER hotel
ENV SPRING_PROFILES_ACTIVE=render
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0"
EXPOSE 10000
ENTRYPOINT ["./iniciar-render.sh"]
