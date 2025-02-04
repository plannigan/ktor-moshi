ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.12.1-jdk17@sha256:d6c8111fce3d9de0ddac8f439072052ba4bd6bc85300fb13fd82d2381b2e080a as build
ARG _GRADLE_SRC_ROOT
COPY --chown=root:root . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon sample:shadowJar

FROM eclipse-temurin:21.0.6_7-jre-noble@sha256:3ef64ec531571987f58ccc90bd3d7f92950539f1baa00a5c45b660d6faccf37d as sample
ARG _GRADLE_SRC_ROOT
RUN mkdir /app
COPY --chown=root:root docker/files/* /app/
COPY --from=build /home/gradle/src/sample/build/libs/sample*-all.jar /app/sample.jar
WORKDIR /app
ENTRYPOINT ["/app/sample.sh"]
