# 🧪 Task 19: StatefulSet Deployment with Headless Service

## 🎯 Objective

Deploy a MySQL database using a StatefulSet and a Headless Service to ensure stable network identity and persistent storage.

## Step1 create yamlfile contain all things that need 
**File:** `statefulset.yml`
```
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
  namespace: ivolve
type: Opaque
data:
  # echo -n 'mypassword' | base64
  MYSQL_ROOT_PASSWORD: bXlwYXNzd29yZA==

---
apiVersion: v1
kind: Service
metadata:
  name: mysql-headless
  namespace: ivolve
  labels:
    app: mysql
spec:
  clusterIP: None
  selector:
    app: mysql
  ports:
    - name: mysql
      port: 3306
      targetPort: 3306

---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
  namespace: ivolve
spec:
  serviceName: mysql-headless
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      tolerations:
        - key: "workload"
          operator: "Equal"
          value: "worker"
          effect: "NoSchedule"
      containers:
        - name: mysql
          image: mysql:8.0
          ports:
            - containerPort: 3306
              name: mysql
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: MYSQL_ROOT_PASSWORD
          volumeMounts:
            - name: mysql-persistent-storage
              mountPath: /var/lib/mysql
  volumeClaimTemplates:
    - metadata:
        name: mysql-persistent-storage
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 1Gi
```

## Step2 create pv.yaml
```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-pv
  namespace: ivolve
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteOnce
  storageClassName: manual
  hostPath:
    path: /mnt/data/mysql
```

### Apply
```
kubectl apply -f statefulset-mysql.yaml
```

## Step3 Verify 

```
ebrahim@ebrahim:~/devops_ivolve/k8s/task19$ kubectl get pods -n ivolve
kubectl get svc -n ivolve
kubectl get pvc -n ivolve
NAME      READY   STATUS    RESTARTS   AGE
mysql-0   1/1     Running   0          31m
NAME             TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)    AGE
mysql-headless   ClusterIP   None         <none>        3306/TCP   29m
NAME                               STATUS   VOLUME     CAPACITY   ACCESS MODES   STORAGECLASS   VOLUMEATTRIBUTESCLASS   AGE
mysql-persistent-storage-mysql-0   Bound    mysql-pv   1Gi        RWO                           <unset>                 31m
```

## Access mysql 

```
kubectl exec -it mysql-0 -n ivolve -- mysql -uroot -p


mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```


