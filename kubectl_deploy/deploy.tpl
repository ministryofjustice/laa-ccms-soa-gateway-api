apiVersion: apps/v1
kind: Deployment
metadata:
  name: laa-ccms-civil-soa-gateway-api
spec:
  replicas: 1
  selector:
    matchLabels:
      app: laa-ccms-civil-soa-gateway-api-dev
  template:
    metadata:
      labels:
        app: laa-ccms-civil-soa-gateway-api-dev
    spec:
      containers:
        - name: laa-ccms-civil-soa-gateway-api
          image: ${ECR_URL}:${IMAGE_TAG}
          ports:
            - containerPort: 5000

