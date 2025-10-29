# 🧪 Lab 26: Deployment Update and Rollback
## 🎯 Objective

 Update a Kubernetes deployment with a new Docker image, monitor the rollout, verify the changes, and perform a rollback to the previous version.

 ## Step1️⃣ Create Project Directory
 ```
mkdir frontend
cd frontend
```
## Step2️⃣ Create Frontend File
 **File:** `index.html`
 ```
<!DOCTYPE html>
<html>
<head>
  <title>DevOps Lab 26</title>
</head>
<body>
  <h1>Hello from Egypt!</h1>
</body>
</html>
```
## Step3️⃣ Create Dockerfile 
```
FROM nginx:alpine
COPY index.html /usr/share/nginx/html/index.html
```
## Step4️⃣ Build and Push First Image
```
docker build -t ebrahimrhh/frontend:v1 .
docker push ebrahimrhh/frontend:v1
```
## Step5️⃣ Create Deployment and Service
**File:** ` deployment.yaml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: ebrahimrhh/frontend:v1
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-service
spec:
  type: NodePort
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30001
```
### Apply 
```
kubectl apply -f deployment.yaml
```

### Access to app 
```
http://<Node-IP>:30001
```
![Frontend Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/k8s/task26/frontend/Screenshot%20(259).png)


## Step7️⃣ Update Source Code 
```
from
<h1>Hello from Egypt!</h1>
to
<h1>Hello from Cairo!</h1>
```
## Step8️⃣ Build and Push New Image
```
docker build -t ebrahimrhh/frontend:v2 .
docker push ebrahimrhh/frontend:v2
```
## Step9️⃣ Update Deployment Image
```
from
image: ebrahimrhh/frontend:v1
to
image: ebrahimrhh/frontend:v2
```

### Apply 
```
kubectl apply -f deployment.yaml
```
### Monitor rollout
```
kubectl rollout status deployment frontend-deployment

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task26/frontend$ kubectl rollout status deployment frontend-deployment
deployment "frontend-deployment" successfully rolled out

```
### Verify 
```
curl http://<Node-IP>:30001
```

![Frontend Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/k8s/task26/frontend/Screenshot%20(261).png)


## Step10 Check Rollout History
```
kubectl rollout history deployment frontend-deployment

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task26/frontend$ kubectl rollout history deployment frontend-deployment
deployment.apps/frontend-deployment
REVISION  CHANGE-CAUSE
1         <none>
2         <none>
```
## Step11 Perform Rollback
```
kubectl rollout undo deployment frontend-deployment
```

## Step12 Re-verify Rollback
```
curl http://<Node-IP>:30001

>will appear

Hello from Egypt!
```












