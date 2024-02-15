FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY *.jar ZubarskaOrdinacijaApk.jar
ENTRYPOINT ["java","-jar","/ZubarskaOrdinacijaApk.jar"]
EXPOSE 8080