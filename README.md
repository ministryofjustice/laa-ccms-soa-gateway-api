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

