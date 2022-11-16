FROM adoptopenjdk/openjdk11:jre-11.0.16.1_1-alpine
EXPOSE 8080
ARG JAR_FILE=build/libs/JasperReportBoot-0.0.2.jar
COPY ${JAR_FILE} app.jar

RUN apk --no-cache add fontconfig ttf-dejavu && \    
    fc-cache -f

ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java","-jar", "/app.jar"]