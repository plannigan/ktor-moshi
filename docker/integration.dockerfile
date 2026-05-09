ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.14.3-jdk17@sha256:ce50078d456e497bc86b76d386503e84aa45416d85069ad66bc072bf07566b25 as build
ARG _GRADLE_SRC_ROOT
COPY --chown=root:root . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon sample:shadowJar

FROM eclipse-temurin:17.0.17_10-jre-noble@sha256:3421d7f073ded5491600707946dd7593502bdc87a18b3f234e7069574fb80401 as sample
ARG _GRADLE_SRC_ROOT
RUN mkdir /app
COPY --chown=root:root docker/files/* /app/
COPY --from=build /home/gradle/src/sample/build/libs/sample*-all.jar /app/sample.jar
WORKDIR /app
ENTRYPOINT ["/app/sample.sh"]
