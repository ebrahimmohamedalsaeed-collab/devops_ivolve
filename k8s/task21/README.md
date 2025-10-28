# Lab 21: Kubernetes Init Container for Pre-Deployment Database Setup

## 🎯 Objective

Modify the existing Node.js Deployment to include an init container that prepares the MySQL database before the main application starts.

## 1. Prerequisites

Kubernetes cluster with 2 nodes.

MySQL and Node.js deployments from Task 20.

Docker image: ebrahimrhh/nodejs-app:lab14

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
## Step6 deployment-with-init.yaml
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
      
      # Init Container - يعمل قبل الـ main container
      initContainers:
      - name: db-setup
        image: mysql:5.7
        command:
        - bash
        - -c
        - |
          echo "🔄 Waiting for MySQL to be ready..."
          until mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1" &>/dev/null; do
            echo "⏳ MySQL not ready yet, waiting..."
            sleep 3
          done
          
          echo "✅ MySQL is ready!"
          echo "📝 Creating database 'ivolve' if not exists..."
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "CREATE DATABASE IF NOT EXISTS ivolve;"
          
          echo "👤 Creating user 'appuser' if not exists..."
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'apppassword';"
          
          echo "🔐 Granting all privileges on 'ivolve' database to 'appuser'..."
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "GRANT ALL PRIVILEGES ON ivolve.* TO 'appuser'@'%';"
          
          echo "♻️  Flushing privileges..."
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "FLUSH PRIVILEGES;"
          
          echo "✅ Database and user setup completed successfully!"
          echo "📊 Verifying setup..."
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "SHOW DATABASES;" | grep ivolve
          mysql -h"${MYSQL_HOST}" -u"root" -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT user, host FROM mysql.user WHERE user='appuser';"
          
          echo "🎉 Init container finished successfully!"
        env:
        - name: MYSQL_HOST
          value: "mysql-service"
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_ROOT_PASSWORD
      
      # Main Container
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
          value: "appuser"  # استخدم الـ user الجديد
        - name: DB_PASSWORD
          value: "apppassword"  # استخدم الـ password الجديد
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
kubectl apply -f deployment-with-init.yaml
kubectl apply -f service.yaml
kubectl apply -f mysql-complete.yaml
```
## Step10 Verify 

```
ebrahim@ebrahim:~/devops_ivolve/k8s/task21$ kubectl get pods -w
NAME                          READY   STATUS    RESTARTS   AGE
mysql-59686bb59-5rt6f         1/1     Running   0          41m
nodejs-app-5d56f649fd-2ltlq   1/1     Running   0          3m5s
nodejs-app-5d56f649fd-zz96h   1/1     Running   0          3m5s
```
## Step11 Check Init Container Logs
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task21$ kubectl logs nodejs-app-5d56f649fd-2ltlq -c db-setup
🔄 Waiting for MySQL to be ready...
✅ MySQL is ready!
📝 Creating database 'ivolve' if not exists...
mysql: [Warning] Using a password on the command line interface can be insecure.
👤 Creating user 'appuser' if not exists...
mysql: [Warning] Using a password on the command line interface can be insecure.
🔐 Granting all privileges on 'ivolve' database to 'appuser'...
mysql: [Warning] Using a password on the command line interface can be insecure.
♻️  Flushing privileges...
mysql: [Warning] Using a password on the command line interface can be insecure.
✅ Database and user setup completed successfully!
📊 Verifying setup...
mysql: [Warning] Using a password on the command line interface can be insecure.
ivolve
mysql: [Warning] Using a password on the command line interface can be insecure.
user    host
appuser %
🎉 Init container finished successfully!
```
## Step12 Verify from MySQL
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task21$ kubectl exec -it mysql-59686bb59-5rt6f -- mysql -u root -prootpassword
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 587
Server version: 8.0.44 MySQL Community Server - GPL

Copyright (c) 2000, 2025, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| ivolve             |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)

mysql> SELECT user, host FROM mysql.user WHERE user='appuser';
+---------+------+
| user    | host |
+---------+------+
| appuser | %    |
+---------+------+
1 row in set (0.00 sec)

mysql> SHOW GRANTS FOR 'appuser'@'%';
+-----------------------------------------------------+
| Grants for appuser@%                                |
+-----------------------------------------------------+
| GRANT USAGE ON *.* TO `appuser`@`%`                 |
| GRANT ALL PRIVILEGES ON `ivolve`.* TO `appuser`@`%` |
+-----------------------------------------------------+
2 rows in set (0.00 sec)

mysql> exit
```
