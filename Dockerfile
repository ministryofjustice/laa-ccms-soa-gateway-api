FROM ghcr.io/ministryofjustice/laa-base-container-images/java:25-corretto-alpine

COPY soa-gateway-service/build/libs/soa-gateway-service-*.jar /app/soa-gateway-service.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/soa-gateway-service.jar"]
