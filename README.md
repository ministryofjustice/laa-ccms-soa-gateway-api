# laa-ccms-soa-gateway-api

This API is made up of multiple projects:
* soa-gateway-api
* soa-gateway-service

This application requires a consistent connection to Oracle SOA. 

## soa-gateway-api

The soa-gateway-api project is a lightweight api interface that is generated using the open-api generator.
The [open-api-specification.yml](./soa-gateway-api/open-api-specification.yml) needs to be kept up to date. 

The interface is generated using openApiGenerate task. This task is triggered as part of a gradle build. 
The tasks have also been configured so that when you run a gradle clean, all generated code will also be cleared.

## soa-gateway-service

The soa-gateway-service implements the api interface generated in the soa-gateway-api subproject.
This service handles all interactions with Oracle's SOA. Multiple SOA wsdl files are imported and 
used to generate the model and client classes required to call the SOA WebService endpoints.

## local development
To run this service locally, you will need to set two environment variables:
- `LAA_CCMS_SOAGATEWAY_USERNAME`
- `LAA_CCMS_SOAGATEWAY_PASSWORD`

To get the values for these two variables, contact another developer on the team.

## Common Components

This API uses components from the [LAA CCMS Common Library](https://github.com/ministryofjustice/laa-ccms-spring-boot-common):

- [laa-ccms-spring-boot-plugin](https://github.com/ministryofjustice/laa-ccms-spring-boot-common?tab=readme-ov-file#laa-ccms-spring-boot-gradle-plugin-for-java--spring-boot-projects)
- [laa-ccms-spring-boot-starter-auth](https://github.com/ministryofjustice/laa-ccms-spring-boot-common/tree/main/laa-ccms-spring-boot-starters/laa-ccms-spring-boot-starter-auth)
