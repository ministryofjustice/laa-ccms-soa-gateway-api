FROM eclipse-temurin:17
VOLUME /tmp
COPY soa-gateway-service-0.0.1-SNAPSHOT.jar soa-gateway-service.jar
EXPOSE 8080
RUN addgroup --system --gid 800 customgroup \
    && adduser --system --uid 800 --ingroup customgroup --shell /bin/sh customuser
RUN chown customuser:customgroup soa-gateway-service.jar
USER 800

ENV TZ=Europe/London
ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0"
CMD java -jar soa-gateway-service.jar
