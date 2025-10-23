# 🧪 Lab 15: Node Isolation Using Taints in Kubernetes

## 🎯 Objective
Configure a Kubernetes cluster to isolate workloads by adding a **taint** on the second node.  
This prevents Pods from being scheduled on that node unless they have a matching toleration.

---


## Step1 Check Cluster Nodes
```
kubectl get nodes

>will appear
ebrahim@ebrahim:~/devops_ivolve$ kubectl get nodes
NAME          STATUS   ROLES           AGE   VERSION
ebrahim       Ready    control-plane   67m   v1.34.1
managednode   Ready    <none>          46m   v1.34.1
```
## Step2 Create a YAML File for the Taint(node-taint.yaml)

```
apiVersion: v1
kind: Node
metadata:
  name: managednode
spec:
  taints:
  - key: "workload"
    value: "worker"
    effect: "NoSchedule"
```
## Step3 Apply the YAML File

```
kubectl apply -f node-taint.yaml

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task15$ kubectl apply -f node-taint.yaml
Warning: resource nodes/managednode is missing the kubectl.kubernetes.io/last-applied-configuration annotation which is required by kubectl apply. kubectl apply should only be used on resources created declaratively by either kubectl create --save-config or kubectl apply. The missing annotation will be patched automatically.
node/managednode configured >this will appear
```

## Step4 Verify the Taint
```
kubectl describe node managednode | grep -A5 Taints

>will appear
ebrahim@ebrahim:~/devops_ivolve/k8s/task15$ kubectl describe node managednode | grep -A5 Taints
Taints:             workload=worker:NoSchedule
Unschedulable:      false
Lease:
  HolderIdentity:  managednode
  AcquireTime:     <unset>
  RenewTime:       Thu, 23 Oct 2025 12:45:59 +0300
```

## Step5 (Optional) Remove the Taint
```
kubectl taint nodes managednode workload=worker:NoSchedule-
```


