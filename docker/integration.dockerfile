ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.14.2-jdk17@sha256:9ba82ad61f9e5729d14d43268b970b7f5007ab2300c1b0bbfa767a2c78dc1c7b as build
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
