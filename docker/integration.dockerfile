ARG _GRADLE_SRC_ROOT=/home/gradle/src

FROM gradle:8.12.1-jdk17@sha256:cd50c1a698a2d3ef4a3c4bdd1d6076de4027a19cb8254cc2df2305c18b7776dd as build
ARG _GRADLE_SRC_ROOT
COPY --chown=root:root . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle --no-daemon sample:shadowJar

FROM eclipse-temurin:17.0.14_7-jre-noble@sha256:ea665210f431bd2da42fe40375d0f9dc500ce0d72ef7b13b5f4f1e02ba64f7e2 as sample
ARG _GRADLE_SRC_ROOT
RUN mkdir /app
COPY --chown=root:root docker/files/* /app/
COPY --from=build /home/gradle/src/sample/build/libs/sample*-all.jar /app/sample.jar
WORKDIR /app
ENTRYPOINT ["/app/sample.sh"]
