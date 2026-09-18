ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.14.3-jdk17@sha256:ce50078d456e497bc86b76d386503e84aa45416d85069ad66bc072bf07566b25 as build
ARG _GRADLE_SRC_ROOT
COPY --chown=root:root . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon sample:shadowJar

FROM eclipse-temurin:25.0.4_7-jre-noble@sha256:b573af9e331196fbc42e246da4df24df9b6c556c73e7efddfde0511f1c9508c5 as sample
ARG _GRADLE_SRC_ROOT
RUN mkdir /app
COPY --chown=root:root docker/files/* /app/
COPY --from=build /home/gradle/src/sample/build/libs/sample*-all.jar /app/sample.jar
WORKDIR /app
ENTRYPOINT ["/app/sample.sh"]
