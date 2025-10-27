# 🧪 Lab 20: Node.js Application Deployment with ClusterIP Service

## 🎯 Objective
Deploy a Node.js application connected to a MySQL database inside Kubernetes, using a **ClusterIP service**, **ConfigMap/Secret**, and **PersistentVolume**.

---



## Step1 Create Namespace
```bash
kubectl create namespace ivolve
```

## Step2 Create ConfigMap
**File:** `configmap.yaml`
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: db-config
data:
  DB_HOST: mysql-service
```
## Step3 Create Secret
**File:** `secret.yaml`
```
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
stringData:
  DB_USER: ivolve_user
  DB_PASSWORD: ivolve_password
```

## Step4 Create pv
**File:** `pv.yaml`
```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nodejs-pv
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: /mnt/data
  persistentVolumeReclaimPolicy: Retain
```

## Step5 Create pvc 
**File:** `pvc.yaml`
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nodejs-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

## Step6 Create Create Node.js Deployment
**File:** `deployment.yaml`
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nodejs-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nodejs-app
  template:
    metadata:
      labels:
        app: nodejs-app
    spec:
      tolerations:
      - key: "workload"
        operator: "Equal"
        value: "worker"
        effect: "NoSchedule"
      containers:
      - name: nodejs
        image: ebrahimrhh/node-app:lab14
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 3000
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: db-config
              key: DB_HOST
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: DB_USER
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: DB_PASSWORD
        - name: DB_NAME
          value: "ivolve"
        volumeMounts:
        - name: nodejs-storage
          mountPath: /app/data
      volumes:
      - name: nodejs-storage
        persistentVolumeClaim:
          claimName: nodejs-pvc
```
## Step7 Create ClusterIP Service
**File:** `service.yaml`
```
apiVersion: v1
kind: Service
metadata:
  name: nodejs-service
spec:
  type: ClusterIP
  selector:
    app: nodejs-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 3000
```
## Step8 mysql-complete.yaml
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-config
data:
  MYSQL_DATABASE: ivolve
---
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
stringData:
  MYSQL_ROOT_PASSWORD: rootpassword
  MYSQL_USER: ivolve_user
  MYSQL_PASSWORD: ivolve_password
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-pv
spec:
  capacity:
    storage: 2Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: /mnt/mysql-data
  persistentVolumeReclaimPolicy: Retain
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 2Gi
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        ports:
        - containerPort: 3306
        env:
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_ROOT_PASSWORD
        - name: MYSQL_DATABASE
          valueFrom:
            configMapKeyRef:
              name: mysql-config
              key: MYSQL_DATABASE
        - name: MYSQL_USER
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_USER
        - name: MYSQL_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_PASSWORD
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
        readinessProbe:
          exec:
            command:
            - mysqladmin
            - ping
            - -h
            - localhost
          initialDelaySeconds: 30
          periodSeconds: 10
        livenessProbe:
          exec:
            command:
            - mysqladmin
            - ping
            - -h
            - localhost
          initialDelaySeconds: 60
          periodSeconds: 10
      volumes:
      - name: mysql-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: mysql-service
spec:
  type: ClusterIP
  selector:
    app: mysql
  ports:
  - protocol: TCP
    port: 3306
    targetPort: 3306
```

## Step9 Apply all
```
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f pv.yaml
kubectl apply -f pvc.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f mysql-complete.yaml
```
### Verify 
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task20$ kubectl get pods -w
NAME                          READY   STATUS    RESTARTS   AGE
mysql-59686bb59-5rt6f         1/1     Running   0          3m17s
nodejs-app-74cf647f5c-8md84   1/1     Running   0          70s
nodejs-app-74cf647f5c-sp2s9   1/1     Running   0          70s
```
## Step10 Test

```
ebrahim@ebrahim:~/devops_ivolve/k8s/task20kubectl logs nodejs-app-74cf647f5c-8md84 --tail=3030
🔄 Attempting to connect to MySQL...
✅ Connected to MySQL and 'ivolve' DB found.
🚀 Server started on http://0.0.0.0:3000

ebrahim@ebrahim:~/devops_ivolve/k8s/task20$ kubectl port-forward service/nodejs-service 8080:80
Forwarding from 127.0.0.1:8080 -> 3000
Forwarding from [::1]:8080 -> 3000
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080

>will appear

ebrahim@ebrahim:~$ curl http://localhost:8080/health
🚀 iVolve web app is working! Keep calm and code on! 🎉ebrahim@ebrahim:~$
ebrahim@ebrahim:~$ curl http://localhost:8080/ready
👍 iVolve web app is ready to rock and roll! 🤘ebrahim@ebrahim:~$
```





