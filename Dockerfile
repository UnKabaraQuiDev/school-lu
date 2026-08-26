FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

RUN git -C pclib pull || true
COPY pclib/ pclib/
COPY pom.xml .

RUN mvn -f pclib/pom.xml install -DskipTests -B
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -B


FROM eclipse-temurin:21-jre

WORKDIR /opt/school-lu

COPY --from=build /build/target/school-lu.jar school-lu.jar

EXPOSE 8080

CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    -Xms512m \
    -Xmx1g \
    -jar school-lu.jar