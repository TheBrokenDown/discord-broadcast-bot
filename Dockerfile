FROM gradle:6.4-jdk14 AS GRADLE_TOOLCHAIN
COPY ./ /app/build
WORKDIR /app/build
RUN gradle bootJar

FROM openjdk:14-alpine
WORKDIR /app
COPY --from=GRADLE_TOOLCHAIN  /app/build/build/libs/discord-broadcast-bot.jar ./app.jar
CMD java -jar app.jar --spring.config.location="/app/conf/broadcastbot.properties"