FROM gradle:6.6.1-jdk11
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle installShadowDist

FROM openjdk:16
ENV PORT 8080
ENV JAVA_OPTIONS "-Xmx250m"
COPY --from=0 /home/gradle/project/build/install/ratpack_app-shadow /app/
WORKDIR /app
EXPOSE ${PORT:-8080}
ENTRYPOINT ["/bin/sh", "bin/ratpack_app", "-XX:+PrintFlagsFinal", "$JAVA_OPTIONS", "-Dapp.config=app-config.yml"]