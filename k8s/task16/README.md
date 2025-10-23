# 🧪 Lab 16: Namespace Management and Resource Quota Enforcement

## 🎯 Objective
Create a new namespace called **ivolve** and apply a resource quota that limits the number of Pods to only **2** within the namespace.

---

## 🧩 Step 1: Create the Namespace
```bash
kubectl create namespace ivolve
```
### verify namespace 
```
kubectl get namespaces

>wil appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task16$ kubectl get namespaces
NAME              STATUS   AGE
default           Active   79m
ivolve            Active   7s
```
## Step2 Create a Resource Quota Manifest(quota.yaml)

```
apiVersion: v1
kind: ResourceQuota
metadata:
  name: pod-limit
  namespace: ivolve
spec:
  hard:
    pods: "2"
```

## Step3 Apply the Resource Quota

```
kubectl apply -f quota.yaml

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task16$ kubectl apply -f quota.yaml
resourcequota/pod-limit created

```
### Check the quota status

```
kubectl get resourcequota -n ivolve

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task16$ kubectl get resourcequota -n ivolve
NAME        REQUEST     LIMIT   AGE
pod-limit   pods: 0/2           13s
```

## Step4 Test the Quota
Try to create 3 pods inside the namespace
```

ebrahim@ebrahim:~/devops_ivolve/k8s/task16$ kubectl run pod1 -n ivolve --image=nginx
kubectl run pod2 -n ivolve --image=nginx
kubectl run pod3 -n ivolve --image=nginx
pod/pod1 created
pod/pod2 created
Error from server (Forbidden): pods "pod3" is forbidden: exceeded quota: pod-limit, requested: pods=1, used: pods=2, limited: pods=2
```

## Step5 Cleanup

```
kubectl delete resourcequota pod-limit -n ivolve
kubectl delete namespace ivolve
```










