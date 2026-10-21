[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-ccms-soa-gateway-api/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-ccms-soa-gateway-api)

# CCMS SOA Gateway API

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

In addition to setting these environment variables, you will need  https://github.com/ministryofjustice/laa-ccms-mock-contracts to be running for local and dev profiles. 

### Connecting to SOA dev

SOA dev only accepts HTTPS, which the local `SOA_SERVICE_URL` of
`http://localhost:8051` cannot speak to directly. The `laa-ccms-soa-proxy`
container in the
[laa-ccms-caab development stack](https://github.com/ministryofjustice/laa-ccms-caab/blob/main/docs/first-time-setup.md#soa-proxy)
listens on `localhost:8051` and does the HTTPS to SOA. With it running and the
SOA port-forward on local port 8052, run this service from the IDE as before;
nothing here changes.

## Logging

The service writes its console logs as [Elastic Common Schema (ECS)](https://www.elastic.co/docs/reference/ecs)
JSON, in line with the LAA monitoring and logging guardrail. This is configured under
`logging.structured` in [`application.yml`](./soa-gateway-service/src/main/resources/application.yml); there is deliberately no
`logback-spring.xml`, as one would override this configuration.

### Running locally

The `local` profile switches structured logging off in favour of a readable console pattern, which
still carries the `[traceId,spanId,sessionID]` of the current request:

```shell
./gradlew :soa-gateway-service:bootRun --args='--spring.profiles.active=local'
```

```text
2026-10-01T23:41:59.958+01:00  INFO 12345 --- [omcat-handler-0] [6abee1b75d73754cc9f35ca586522766,e75b167f726aa789,9f5de6e6-b841-4398-932d-d95fb651748c] u.g.l.s.auth.ApiAuthenticationFilter     : Request rejected for endpoint 'GET /organisations': No API access token provided.
```

### Structured (ECS) output

With any other profile, each log event is a single line of JSON. For example (formatted for
readability):

```json
{
  "@timestamp": "2026-10-01T22:41:43.762331Z",
  "log": {
    "level": "INFO",
    "logger": "uk.gov.laa.springboot.auth.ApiAuthenticationFilter"
  },
  "process": {
    "pid": 12345,
    "thread": {
      "name": "tomcat-handler-0"
    }
  },
  "service": {
    "name": "laa-ccms-soa-gateway-api",
    "version": "1.4.2",
    "environment": "dev",
    "node": {
      "name": "laa-ccms-soa-gateway-api-84f6c7d9b-t5r1y"
    }
  },
  "message": "Request rejected for endpoint 'GET /organisations': No API access token provided.",
  "traceId": "6abee1a7dcf9a61e573b3ad11210f63d",
  "spanId": "b7af1abbb9cf40a2",
  "sessionID": "9f5de6e6-b841-4398-932d-d95fb651748c",
  "ecs": {
    "version": "8.11"
  }
}
```

| Field                 | Source                                                                                   |
|-----------------------|------------------------------------------------------------------------------------------|
| `service.name`        | `spring.application.name`                                                                |
| `service.version`     | The Gradle project version, substituted for `@version@` in `application.yml` at build time |
| `service.environment` | The active Spring profiles (`SPRING_PROFILES_ACTIVE`), or `default` when none is set     |
| `service.node.name`   | The `HOSTNAME` environment variable, which is the pod name in Kubernetes                 |
| `traceId`, `spanId`   | Micrometer Tracing (Brave), for log events written while a request is being handled      |

### Log levels

| Environment variable   | Logger                | Default |
|------------------------|-----------------------|---------|
| `ROOT_LOGGING_LEVEL`   | root                  | `info`  |
| `SPRING_LOGGING_LEVEL` | `org.springframework` | `info`  |
| `APP_LOGGING_LEVEL`    | `uk.gov.laa.ccms.soa` | `info`  |

## Common Components

This API uses components from the [LAA CCMS Common Library](https://github.com/ministryofjustice/laa-ccms-spring-boot-common):

- [laa-ccms-spring-boot-plugin](https://github.com/ministryofjustice/laa-ccms-spring-boot-common?tab=readme-ov-file#laa-ccms-spring-boot-gradle-plugin-for-java--spring-boot-projects)
- [laa-ccms-spring-boot-starter-auth](https://github.com/ministryofjustice/laa-ccms-spring-boot-common/tree/main/laa-ccms-spring-boot-starters/laa-ccms-spring-boot-starter-auth)

## Contributing
Follow the [contribution guide](./CONTRIBUTING.md) to make code changes.
### Request correlation and verification

`sessionID` is a generated opaque logging ID, never the authentication session cookie.
Requests with an existing HTTP session reuse its logging ID; stateless requests get a new ID
without creating a session. It is scoped to this service and is not propagated downstream.
`traceId` and `spanId` are supplied by Micrometer while a request is handled; startup logs
have no request correlation. The local pattern includes all three fields.

Run the focused checks with `./gradlew soa-gateway-service:test --tests '*LoggingAcceptanceTest' --tests '*SessionCorrelationFilterTest'`.
They use synthetic requests and write actual captured log lines to `soa-gateway-service/build/logging-evidence/`.
Do not enable request payload or SQL bind parameter logging with real personal data.

Captured synthetic-request screenshots and validation scope: [logging evidence](docs/logging/README.md).
