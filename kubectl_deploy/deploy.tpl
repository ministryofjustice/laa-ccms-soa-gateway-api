apiVersion: apps/v1
kind: Deployment
metadata:
  name: caab-soa-api
  namespace: laa-ccms-civil
  labels:
    app.kubernetes.io/name: caab-soa-api
spec:
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: caab-soa-api
  template:
    metadata:
      labels:
        app.kubernetes.io/name: caab-soa-api
    spec:
      containers:
        - name: caab-soa-api
          image: ${ECR_URL}:${IMAGE_TAG}
          ports:
            - containerPort: 8007
          env:
            - name: NOTIFICATIONS_SERVICE_NAME
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: notifications-service-name
            - name: NOTIFICATIONS_SERVICE_URL
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: notifications-service-url
            - name: CONTRACT_DETAILS_SERVICE_NAME
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: contract-details-service-name
            - name: CONTRACT_DETAILS_SERVICE_URL
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: contract-details-service-url
            - name: CLIENT_SERVICE_NAME
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: client-service-name
            - name: CLIENT_SERVICE_URL
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: client-service-url
            - name: REFERENCE_DATA_SERVICE_NAME
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: reference-data-service-name
            - name: REFERENCE_DATA_SERVICE_URL
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: reference-data-service-url
            - name: CASE_SERVICE_NAME
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: case-service-name
            - name: CASE_SERVICE_URL
              valueFrom:
                secretKeyRef:
                  name: saml-metadata-uri
                  key: case-service-url
