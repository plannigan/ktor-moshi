ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.14.3-jdk17@sha256:0c56341882909b2b6fb0dfc3afde2877c24f946b5275106990986de5123e3bf0 as build
ARG _GRADLE_SRC_ROOT
COPY --chown=root:root . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon sample:shadowJar

FROM eclipse-temurin:17.0.15_6-jre-noble@sha256:8e90405bb240121ab38c76aa9e74627d4bd194a12dcc2ec13bd7ce1917f6d2fb as sample
ARG _GRADLE_SRC_ROOT
RUN mkdir /app
COPY --chown=root:root docker/files/* /app/
COPY --from=build /home/gradle/src/sample/build/libs/sample*-all.jar /app/sample.jar
WORKDIR /app
ENTRYPOINT ["/app/sample.sh"]
