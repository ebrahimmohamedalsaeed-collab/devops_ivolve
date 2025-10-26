# Lab 17: Managing Configuration and Sensitive Data with ConfigMaps and Secrets

## 🎯 Objective
Manage application configuration and sensitive data separately using ConfigMaps and Secrets in Kubernetes.

---

## 🧩 Step 1: Create a ConfigMap for non-sensitive data

**File:** `mysql-configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-config
  namespace: ivolve
data:
  DB_HOST: mysql-service
  DB_USER: ivolve_user
```
### Apllay mysql-configmap.yaml
```
kubectl apply -f mysql-configmap.yaml
```

## Step2 Create a Secret for sensitive data
> Base64 encode passwords
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task17$ echo -n 'password123' | base64
cGFzc3dvcmQxMjM=
ebrahim@ebrahim:~/devops_ivolve/k8s/task17$ echo -n 'rootpass' | base64
cm9vdHBhc3M=
```
**File**: `mysql-secret.yaml`

```
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
  namespace: ivolve
type: Opaque
data:
  DB_PASSWORD: cGFzc3dvcmQxMjM=
  MYSQL_ROOT_PASSWORD: cm9vdHBhc3M=
```

### Apply the file
```
kubectl apply -f mysql-secret.yaml
```

## Step3 Verify 

```
ebrahim@ebrahim:~/devops_ivolve/k8s/task17$ kubectl get configmap -n ivolve
kubectl get secret -n ivolve
NAME               DATA   AGE
kube-root-ca.crt   1      29s
mysql-config       2      21s
NAME           TYPE     DATA   AGE
mysql-secret   Opaque   2      13s
```





