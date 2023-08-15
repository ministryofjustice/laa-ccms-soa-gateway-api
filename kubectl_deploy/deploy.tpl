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

