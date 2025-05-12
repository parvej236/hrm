FROM openjdk:11.0.11-jdk

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

## Override it with: java -jar /app.jar
## Default remote-debug on
CMD ["java", "-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8512", "-jar", "/app.jar"]