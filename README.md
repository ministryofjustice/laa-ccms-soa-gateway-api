# laa-ccms-soa-gateway-api

This API is made up of multiple projects:
* soa-gateway-api
* soa-gateway-service

## soa-gateway-api

The soa-gateway-api project is a lightweight api interface that is generated using the open-api generator.
The [open-api-specification.yml](./soa-gateway-api/open-api-specification.yml) need to be kept up to date. 

In order to generate the interface and models run the following:

```
./gradlew data-api:openApiGenerate
```

## soa-gateway-service

the soa-gateway-service implements the api interface generated in the data-api subproject.