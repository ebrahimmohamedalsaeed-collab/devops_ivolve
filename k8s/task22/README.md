# Task 22 - Health Monitoring of Application Pods

## 🎯 Objective
Configure **readiness** and **liveness probes** to monitor the health of application pods.

- **Readiness Probe** → Determines when the application is ready to receive traffic.  
- **Liveness Probe** → Ensures the application is still running properly.

## 🏗️ Deployment Files

### 1️⃣ `mysql-complete.yaml`
This file deploys a full MySQL setup including:
- **ConfigMap** for database name  
- **Secret** for sensitive credentials  
- **PersistentVolume** and **PersistentVolumeClaim** for data storage  
- **Deployment** with probes to monitor MySQL health  
- **Service** to expose MySQL internally

### 2️⃣ `deployment-with-init.yaml`
Deploys a **Node.js application** that connects to the MySQL database and includes:

- **Init container** `db-setup` that waits for MySQL to be ready, then:
  - Creates the database `ivolve`
  - Creates the user `appuser` with full privileges
- **Main container** runs the Node.js app image  
  `ebrahimrhh/node-app:lab14`
- Configured **Readiness** and **Liveness** probes hitting endpoint `/health`

### I applied all files from task21 I will change deployment.yaml only from task21
 
**File:** `deployment-with-init.yaml`
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
### Apply 
```
kubectl apply -f deployment-with-init.yaml
```

## Verify pods 
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task22$ kubectl get pods -w
NAME                         READY   STATUS    RESTARTS   AGE
mysql-59686bb59-5rt6f        1/1     Running   0          81m
nodejs-app-cfccbbcfc-hrdkh   1/1     Running   0          2m6s
nodejs-app-cfccbbcfc-qjzh6   1/1     Running   0          110s
```

## pod discribtion 
```
kubectl describe pod nodejs-app-cfccbbcfc-hrdkh
```
```
Will appear

ebrahim@ebrahim:~/devops_ivolve/k8s/task22$ kubectl describe pod nodejs-app-cfccbbcfc-hrdkh
Name:             nodejs-app-cfccbbcfc-hrdkh
Namespace:        default
Priority:         0
Service Account:  default
Node:             managednode/192.168.226.129
Start Time:       Tue, 28 Oct 2025 03:56:25 +0300
Labels:           app=nodejs-app
                  pod-template-hash=cfccbbcfc
Annotations:      <none>
Status:           Running
IP:               10.244.1.11
IPs:
  IP:           10.244.1.11
Controlled By:  ReplicaSet/nodejs-app-cfccbbcfc
Init Containers:
  db-setup:
    Container ID:  containerd://271844ff2aacc7bf4fe8cab989c3c18a516d12e8f105a490fa7defe87d72aed0
    Image:         mysql:5.7
    Image ID:      docker.io/library/mysql@sha256:4bc6bc963e6d8443453676cae56536f4b8156d78bae03c0145cbe47c2aad73bb
    Port:          <none>
    Host Port:     <none>
    Command:
      bash
      -c
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

    State:          Terminated
      Reason:       Completed
      Exit Code:    0
      Started:      Tue, 28 Oct 2025 03:56:26 +0300
      Finished:     Tue, 28 Oct 2025 03:56:27 +0300
    Ready:          True
    Restart Count:  0
    Environment:
      MYSQL_HOST:           mysql-service
      MYSQL_ROOT_PASSWORD:  <set to the key 'MYSQL_ROOT_PASSWORD' in secret 'mysql-secret'>  Optional: false
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-m7vpq (ro)
Containers:
  nodejs:
    Container ID:   containerd://2c0f12cc582b7beb2f5d669604e16688c08c1bd99e5647bd07dea4183faed4ed
    Image:          ebrahimrhh/node-app:lab14
    Image ID:       docker.io/ebrahimrhh/node-app@sha256:c5a1ee35e7c88fd6f68e91437b4e0c60c01ff0ea39cfbefde2522490529ea438
    Port:           3000/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Tue, 28 Oct 2025 03:56:28 +0300
    Ready:          True
    Restart Count:  0
    Liveness:       http-get http://:3000/health delay=20s timeout=1s period=10s #success=1 #failure=3
    Readiness:      http-get http://:3000/health delay=10s timeout=1s period=5s #success=1 #failure=3
    Environment:
      DB_HOST:      mysql-service
      DB_USER:      appuser
      DB_PASSWORD:  apppassword
      DB_NAME:      ivolve
    Mounts:
      /app/data from nodejs-storage (rw)
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-m7vpq (ro)
Conditions:
  Type                        Status
  PodReadyToStartContainers   True
  Initialized                 True
  Ready                       True
  ContainersReady             True
  PodScheduled                True
Volumes:
  nodejs-storage:
    Type:       PersistentVolumeClaim (a reference to a PersistentVolumeClaim in the same namespace)
    ClaimName:  nodejs-pvc
    ReadOnly:   false
  kube-api-access-m7vpq:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
                             workload=worker:NoSchedule
Events:
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  2m42s  default-scheduler  Successfully assigned default/nodejs-app-cfccbbcfc-hrdkh to managednode
  Normal  Pulled     2m41s  kubelet            Container image "mysql:5.7" already present on machine
  Normal  Created    2m41s  kubelet            Created container: db-setup
  Normal  Started    2m41s  kubelet            Started container db-setup
  Normal  Pulled     2m40s  kubelet            Container image "ebrahimrhh/node-app:lab14" already present on machine
  Normal  Created    2m40s  kubelet            Created container: nodejs
  Normal  Started    2m39s  kubelet            Started container nodejs
```
