# Lab 27: Securing Kubernetes with RBAC and Service Accounts

## 🎯 Objective

 Secure Kubernetes access by creating a Service Account with limited RBAC permissions and generate a custom kubeconfig file for it.

 ## Step1 create namespace ivlove
 ```
kubectl create namespace ivolve
```

## Step2 Create Service Account
```
kubectl create serviceaccount jenkins-sa -n ivolve
```
### ✅ Verify 
```
kubectl get sa -n ivolve

>will appear
ebrahim@ebrahim:~$ kubectl get sa -n ivolve
NAME         SECRETS   AGE
default      0         15s
jenkins-sa   0         7s
```

## Step3 Create jenkins-sa-secret.yaml
```
apiVersion: v1
kind: Secret
metadata:
  name: jenkins-sa-token
  namespace: ivolve
  annotations:
    kubernetes.io/service-account.name: jenkins-sa
type: kubernetes.io/service-account-token
```
### Apply 
```
kubectl apply -f jenkins-sa-secret.yaml
```
### Describe 
```
kubectl describe secret jenkins-sa-token -n ivolve


>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task27$ kubectl describe secret jenkins-sa-token -n ivolve
Name:         jenkins-sa-token
Namespace:    ivolve
Labels:       <none>
Annotations:  kubernetes.io/service-account.name: jenkins-sa
              kubernetes.io/service-account.uid: 1f85b4e5-8f4d-42cb-93ee-b30635db2866

Type:  kubernetes.io/service-account-token

Data
====
ca.crt:     1107 bytes
namespace:  6 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IlJydkgwUkR0Tjl5UVItNmFrUW1zWUk3Nnh6czZlWFhGOHBrTGZKZUJ1d2MifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJpdm9sdmUiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlY3JldC5uYW1lIjoiamVua2lucy1zYS10b2tlbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJqZW5raW5zLXNhIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMWY4NWI0ZTUtOGY0ZC00MmNiLTkzZWUtYjMwNjM1ZGIyODY2Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50Oml2b2x2ZTpqZW5raW5zLXNhIn0.ZSxlKiW1oxkr8yUbzLJ_YQ0_5Yh_x6n9jvigGhlK5vlDupjMUVINtlskzleAQwOFnBqF0SfSZ0OF2QPCW3BhwUMRqRqG_A9Im8VxIz4yHS2FUtzGZfNeWNUqM5c3lFsIZJmLsr91D1zruYjTd9-OyhAsicKh1A-Grgty-5IDdq_G4yyrZRXessOtAYCwPvn0eBYoTfgby6MwaVkftI8ZitiqzsbpGDuV7WWbpfrneaTX-gePrU5QthvToZiyCtWzlNx-NuZ3Zl_twqvXob5iaHkkxq64gZG-gIU-D3IO-HtF2Xd5tsllDMtByOcJcyRo3FDaIZZSyJ2gKt3MzhTRRw

```
## Step4 Create Role with Read-Only Access to Pods
**File:** `pod-reader-role.yaml
```
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: ivolve
  name: pod-reader
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list"]
```
### Apply 
```
kubectl apply -f pod-reader-role.yaml
```

## Step5 Bind Role to Service Account
**File:** `pod-reader-rolebinding.yaml`
```
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods
  namespace: ivolve
subjects:
- kind: ServiceAccount
  name: jenkins-sa
  namespace: ivolve
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```
### Apply 
```
kubectl apply -f pod-reader-rolebinding.yaml
```

## Step6 Generate Custom Kubeconfig for jenkins-sa
```
TOKEN=$(kubectl get secret jenkins-sa-token -n ivolve -o jsonpath='{.data.token}' | base64 --decode)
CLUSTER_NAME=$(kubectl config view -o jsonpath='{.clusters[0].name}')
CLUSTER_SERVER=$(kubectl config view -o jsonpath='{.clusters[0].cluster.server}')
CA_CERT=$(kubectl config view --raw -o jsonpath='{.clusters[0].cluster.certificate-authority-data}')
```
```
Create Kubeconfig

cat <<EOF > jenkins-sa.kubeconfig
apiVersion: v1
kind: Config
clusters:
- name: $CLUSTER_NAME
  cluster:
    certificate-authority-data: $CA_CERT
    server: $CLUSTER_SERVER
contexts:
- name: jenkins-sa-context
  context:
    cluster: $CLUSTER_NAME
    namespace: ivolve
    user: jenkins-sa
current-context: jenkins-sa-context
users:
- name: jenkins-sa
  user:
    token: $TOKEN
EOF
```
## Step7 Verify Limited Access
**Allowed (Read-Only)**
```
kubectl --kubeconfig=jenkins-sa.kubeconfig get pods -n ivolve

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task27$ kubectl --kubeconfig=jenkins-sa.kubeconfig get pods -n ivolve
NAME       READY   STATUS    RESTARTS   AGE
test-pod   1/1     Running   0          83s
```
**Forbidden (Not Allowed)**
```
ebrahim@ebrahim:~/devops_ivolve/k8s/task27$ kubectl --kubeconfig=jenkins-sa.kubeconfig delete pod test-pod -n ivolve
Error from server (Forbidden): pods "test-pod" is forbidden: User "system:serviceaccount:ivolve:jenkins-sa" cannot delete resource "pods" in API group "" in the namespace "ivolve"
ebrahim@ebrahim:~/devops_ivolve/k8s/task27$ kubectl --kubeconfig=jenkins-sa.kubeconfig run another-pod --image=nginx -n ivolve
Error from server (Forbidden): pods is forbidden: User "system:serviceaccount:ivolve:jenkins-sa" cannot create resource "pods" in API group "" in the namespace "ivolve"
```



