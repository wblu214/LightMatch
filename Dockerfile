FROM openjdk:17
COPY *.jar /app.jar
CMD ["--server.port=2024"]
EXPOSE 2024
ENTRYPOINT ["java","-jar","/app.jar"]
LABEL authors="路文斌"