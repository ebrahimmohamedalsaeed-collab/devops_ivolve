# Lab 23: Pod Resource Management with CPU and Memory Requests and Limits

## Objective
Configure the Node.js Deployment to include **CPU and Memory requests and limits** for resource management.
i wall change only deploymentfile from task22 
## Deployment YAML (task23/deployment-nodejs-resources.yaml)
```yaml
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
      
      # Init Container to setup DB (Task 21)
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
        env:
        - name: MYSQL_HOST
          value: "mysql-service"
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_ROOT_PASSWORD
      
      # Main Node.js container with probes and resource management (Task 22 & 23)
      containers:
      - name: nodejs
        image: ebrahimrhh/node-app:lab14
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 3000
        env:
        - name: DB_HOST
          value: "mysql-service"
        - name: DB_USER
          value: "appuser"
        - name: DB_PASSWORD
          value: "apppassword"
        - name: DB_NAME
          value: "ivolve"
        volumeMounts:
        - name: nodejs-storage
          mountPath: /app/data
        resources:
          # Task 23: Resource Requests & Limits
          requests:
            cpu: 250m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        readinessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 10
          periodSeconds: 5
          failureThreshold: 3
        livenessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 20
          periodSeconds: 10
          failureThreshold: 3
      
      volumes:
      - name: nodejs-storage
        persistentVolumeClaim:
          claimName: nodejs-pvc
```
### Apply 
```
kubectl apply -f deployment-nodejs-resources.yaml
```
### Verify 
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task23$ kubectl get pods
NAME                         READY   STATUS    RESTARTS   AGE
mysql-59686bb59-5rt6f        1/1     Running   0          131m
nodejs-app-787f5c867-hh5tt   1/1     Running   0          102s
nodejs-app-787f5c867-pp4gr   1/1     Running   0          118s
```

## Describe Pod
```
kubectl describe pod nodejs-app-787f5c867-hh5tt
```
```
>will appear

ebrahim@ebrahim:~/devops_ivolve/k8s/task23$ kubectl describe pod nodejs-app-787f5c867-hh5tt
Name:             nodejs-app-787f5c867-hh5tt
Namespace:        default
Priority:         0
Service Account:  default
Node:             managednode/192.168.226.129
Start Time:       Tue, 28 Oct 2025 04:47:04 +0300
Labels:           app=nodejs-app
                  pod-template-hash=787f5c867
Annotations:      <none>
Status:           Running
IP:               10.244.1.14
IPs:
  IP:           10.244.1.14
Controlled By:  ReplicaSet/nodejs-app-787f5c867
Init Containers:
  db-setup:
    Container ID:  containerd://71b5f92ba2dd05c2c480bff376da83ea357044e97f73e5ce2d0aa8d44ea16d3a
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

    State:          Terminated
      Reason:       Completed
      Exit Code:    0
      Started:      Tue, 28 Oct 2025 04:47:06 +0300
      Finished:     Tue, 28 Oct 2025 04:47:06 +0300
    Ready:          True
    Restart Count:  0
    Environment:
      MYSQL_HOST:           mysql-service
      MYSQL_ROOT_PASSWORD:  <set to the key 'MYSQL_ROOT_PASSWORD' in secret 'mysql-secret'>  Optional: false
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-v4z8d (ro)
Containers:
  nodejs:
    Container ID:   containerd://5a3291076106683e225b6f2b903fcf705d27967872378e7211d74c3286664df3
    Image:          ebrahimrhh/node-app:lab14
    Image ID:       docker.io/ebrahimrhh/node-app@sha256:c5a1ee35e7c88fd6f68e91437b4e0c60c01ff0ea39cfbefde2522490529ea438
    Port:           3000/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Tue, 28 Oct 2025 04:47:07 +0300
    Ready:          True
    Restart Count:  0
    Limits:
      cpu:     500m
      memory:  512Mi
    Requests:
      cpu:      250m
      memory:   256Mi
    Liveness:   http-get http://:3000/health delay=20s timeout=1s period=10s #success=1 #failure=3
    Readiness:  http-get http://:3000/health delay=10s timeout=1s period=5s #success=1 #failure=3
    Environment:
      DB_HOST:      mysql-service
      DB_USER:      appuser
      DB_PASSWORD:  apppassword
      DB_NAME:      ivolve
    Mounts:
      /app/data from nodejs-storage (rw)
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-v4z8d (ro)
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
  kube-api-access-v4z8d:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   Burstable
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
                             workload=worker:NoSchedule
Events:
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  2m10s  default-scheduler  Successfully assigned default/nodejs-app-787f5c867-hh5tt to managednode
  Normal  Pulled     2m9s   kubelet            Container image "mysql:5.7" already present on machine
  Normal  Created    2m9s   kubelet            Created container: db-setup
  Normal  Started    2m8s   kubelet            Started container db-setup
  Normal  Pulled     2m7s   kubelet            Container image "ebrahimrhh/node-app:lab14" already present on machine
  Normal  Created    2m7s   kubelet            Created container: nodejs
  Normal  Started    2m7s   kubelet            Started container nodejs
```
