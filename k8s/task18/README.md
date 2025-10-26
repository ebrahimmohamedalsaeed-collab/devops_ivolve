# 🧪 Lab 18: Persistent Storage Setup for Application Logging

## 🎯 Objective
Set up persistent storage in Kubernetes to store application logs permanently, even if pods are restarted or deleted.

---

## 🧱 Step 1: Create log directory on the node

Create a directory `/mnt/app-logs` on the node and give it full permission:

```bash
sudo mkdir -p /mnt/app-logs
sudo chmod 777 /mnt/app-logs
```

## Step2 Define a Persistent Volume (PV)
**File:** `pv.yaml`

```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: app-logs-pv
spec:
  capacity:
    storage: 1Gi
  volumeMode: Filesystem
  accessModes:
    - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  storageClassName: manual
  hostPath:
    path: /mnt/app-logs
```
### Apply 
```
kubectl apply -f pv.yaml
```

## Step 3: Define a Persistent Volume Claim (PVC)
**File:** `pvc.yaml`
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: app-logs-pvc
spec:
  accessModes:
    - ReadWriteMany
  storageClassName: manual
  resources:
    requests:
      storage: 1Gi
```

### Apply 
```
kubectl apply -f pvc.yaml
```

## Verify 

```
ebrahim@ebrahim:~/devops_ivolve/k8s/task18$ kubectl get pv
kubectl get pvc
NAME          CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                  STORAGECLASS   VOLUMEATTRIBUTESCLASS   REASON   AGE
app-logs-pv   1Gi        RWX            Retain           Bound    default/app-logs-pvc   manual         <unset>                          9s
NAME           STATUS   VOLUME        CAPACITY   ACCESS MODES   STORAGECLASS   VOLUMEATTRIBUTESCLASS   AGE
app-logs-pvc   Bound    app-logs-pv   1Gi        RWX            manual         <unset>                 9s
```






