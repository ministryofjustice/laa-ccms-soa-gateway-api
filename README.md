# laa-ccms-soa-gateway-api

This API is made up of multiple projects:
* soa-gateway-api
* soa-gateway-service

This application requires a consistent connection to soa. 


## soa-gateway-api

The soa-gateway-api project is a lightweight api interface that is generated using the open-api generator.
The [open-api-specification.yml](./soa-gateway-api/open-api-specification.yml) need to be kept up to date. 

The interface is generated using openApiGenerate task. This is task is used on a build. 
The tasks have also been configured when you do a clean all generated code it cleared.

## soa-gateway-service

The soa-gateway-service implements the api interface generated in the soa-gateway-api subproject.
This service is what is used to call soa. It uses wsdl file to generate the classes required to talk to soa.

