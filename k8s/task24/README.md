# Task 24: Control Pod-to-Pod Traffic via NetworkPolicy

## Objective
Control and restrict Pod-to-Pod communication in Kubernetes using **NetworkPolicy**.

- Only allow application pods to connect to MySQL pods.
- Restrict MySQL access to port 3306.

---

## Files

| File | Description |
|------|-------------|
| `app-deployment.yaml` | Deployment for application pods (`app=application`) |
| `mysql-deployment.yaml` | Deployment for MySQL pods (`app=mysql`) |
| `other-deployment.yaml` | Deployment for other pods (`app=other`) |
| `networkpolicy.yaml` | NetworkPolicy `allow-app-to-mysql` to restrict MySQL access |

## Step1 mysql-deployment.yaml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  labels:
    app: mysql
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
        image: mysql:5.7
        env:
          - name: MYSQL_ROOT_PASSWORD
            value: "rootpassword"
        ports:
          - containerPort: 3306
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  labels:
    app: mysql
spec:
  selector:
    app: mysql
  ports:
    - port: 3306
      targetPort: 3306
      protocol: TCP
  type: ClusterIP
```
## Step2 app-deployment.yaml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: application
  labels:
    app: application
spec:
  replicas: 1
  selector:
    matchLabels:
      app: application
  template:
    metadata:
      labels:
        app: application
    spec:
      containers:
      - name: app
        image: nginx:stable
        ports:
          - containerPort: 80
```
## Step3 other-deployment.yaml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: other
  labels:
    app: other
spec:
  replicas: 1
  selector:
    matchLabels:
      app: other
  template:
    metadata:
      labels:
        app: other
    spec:
      containers:
      - name: other
        image: nginx:stable
        ports:
          - containerPort: 80
```
## Step4 networkpolicy.yaml
```
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-app-to-mysql
  namespace: default
spec:
  podSelector:
    matchLabels:
      app: mysql
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              app: application
      ports:
        - protocol: TCP
          port: 3306
```

## Step5 Apply 
```
kubectl apply -f mysql-deployment.yaml
kubectl apply -f app-deployment.yaml
kubectl apply -f other-deployment.yaml
kubectl apply -f networkpolicy.yaml
```

## Verify 
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task24$ kubectl get pods
kubectl get networkpolicy
NAME                           READY   STATUS    RESTARTS   AGE
application-54d874556f-kz8dx   1/1     Running   0          19s
mysql-676c5bb695-nstj8         1/1     Running   0          19s
other-5f7fdbb5dd-dzzvq         1/1     Running   0          19s
NAME                 POD-SELECTOR   AGE
allow-app-to-mysql   app=mysql      18s
```
## Test connectivity
**Allowed (application pod → MySQL)**
```
kubectl run mysql-client-allowed --rm -it --image=mysql:5.7 --restart=Never \
--overrides='{"apiVersion":"v1","kind":"Pod","metadata":{"labels":{"app":"application"}}}' -- bash
```
```
mysqladmin -h mysql -P 3306 -u root -prootpassword ping

>>ebrahim@ebrahim:~/devops_ivolve/k8s/task24$ kubectl run test-allowed --rm -it --image=mysql:5.7 --restart=Never \
 --overrides='{"apiVersion":"v1","kind":"Pod","metadata":{"labels":{"app":"application"}}}' -- bash
 If you don't see a command prompt, try pressing enter.
 bash-4.2# mysqladmin -h mysql -P 3306 -u root -prootpassword ping
 mysqladmin: [Warning] Using a password on the command line interface can be insecure.
 mysqld is alive >> this will appear <<
```
**Denied (other pod → MySQL)**
```
kubectl run mysql-client-denied --rm -it --image=mysql:5.7 --restart=Never \
--overrides='{"apiVersion":"v1","kind":"Pod","metadata":{"labels":{"app":"other"}}}' -- bash
```
```
mysqladmin -h mysql -P 3306 -u root -prootpassword ping

ebrahim@ebrahim:~/devops_ivolve/k8s/task24$ kubectl run test-denied --rm -it --image=mysql:5.7 --restart=Never \
--overrides='{"apiVersion":"v1","kind":"Pod","metadata":{"labels":{"app":"other"}}}' -- bash
If you don't see a command prompt, try pressing enter.
bash-4.2# mysqladmin -h mysql -P 3306 -u root -prootpassword ping
mysqladmin: [Warning] Using a password on the command line interface can be insecure.
^Cmysqladmin: connect to server at 'mysql' failed  >> this will appear << 
error: 'Can't connect to MySQL server on 'mysql' (4)'
Check that mysqld is running on mysql and that the port is 3306.
```

