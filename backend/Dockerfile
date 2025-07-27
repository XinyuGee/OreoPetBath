# ---------- build stage ----------
FROM eclipse-temurin:17 AS build
WORKDIR /app
COPY . . 
RUN ./gradlew clean build -x test --stacktrace --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
