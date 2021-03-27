FROM maven:3.6.0-jdk-11-slim as BUILD_IMAGE
RUN java --version
COPY . .
RUN mvn clean
RUN mvn package -Dmaven.test.skip=true



FROM openjdk:11-jre
COPY --from=BUILD_IMAGE ./target/demo-task-0.0.1-SNAPSHOT.jar /usr/local/lib/demo-task-0.0.1-SNAPSHOT.jar
EXPOSE 80
ENTRYPOINT [ "java","-jar", "/usr/local/lib/demo-task-0.0.1-SNAPSHOT.jar" ]
