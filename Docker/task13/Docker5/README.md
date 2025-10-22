# 🧪 Lab 13: Custom Docker Network for Microservices

## 🎯 Objective
Create a custom Docker network to connect multiple microservices (frontend & backend) and verify communication between them.

---

## Step1 Clone the Repository
```
git clone https://github.com/Ibrahim-Adel15/Docker5.git
```

## Step2 Create Dockerfile for Frontend

```
# Use official Python image
FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Copy requirements file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application files
COPY . .

# Expose port
EXPOSE 5000

# Run the app
CMD ["python", "app.py"]
```
## Step3 Build the Frontend Image  
> i stand on frontend so use
~/devops_ivolve/Docker/task13/Docker5/frontend
```
docker build -t frontend-app .
```
## Step4 Create Dockerfile for Backend

```
# Use official Python image
FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Install Flask
RUN pip install flask

# Copy application files
COPY . .

# Expose port
EXPOSE 5000

# Run the app
CMD ["python", "app.py"]
```

## Step5 Build the Backend Image
>i stand on backend
~/devops_ivolve/Docker/task13/Docker5/backend
```
docker build -t backend-app .
```

## Step6 Create Custom Docker Network
```
docker network create --subnet=192.168.0.0/24 ivolve-network
```
```
>verify

docker network ls

a2d977323150   ivolve-network   bridge    local

```
## Step7 Run Containers

```
> Backend (on ivolve-network)

docker run -d --name backend --network ivolve-network backend-app

> Frontend1 (on ivolve-network)

docker run -d --name frontend1 --network ivolve-network -p 5001:5000 frontend-app

> Frontend2 (on default network)

docker run -d --name frontend2 -p 5002:5000 frontend-app

```

## Step8 Verify Communication


### docker container frontend1

```
docker exec -it frontend1 bash
apt update && apt install -y iputils-ping
ping backend
```

71fdf714e474   frontend-app                          "python app.py"          14 minutes ago   Up 14 minutes               0.0.0.0:5001->5000/tcp, [::]:5001->5000/tcp   frontend1

> Output
```
root@71fdf714e474:/app# ping backend
PING backend (192.168.0.2) 56(84) bytes of data.
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=1 ttl=64 time=0.306 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=2 ttl=64 time=0.052 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=3 ttl=64 time=0.054 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=4 ttl=64 time=0.054 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=5 ttl=64 time=0.063 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=6 ttl=64 time=0.050 ms
64 bytes from backend.ivolve-network (192.168.0.2): icmp_seq=7 ttl=64 time=0.043 ms
```

### docker container frontend2 

```
docker exec -it frontend2 bash
apt update && apt install -y iputils-ping
ping backend
```

480aa5a6303b   frontend-app                          "python app.py"          13 minutes ago   Up 13 minutes               0.0.0.0:5002->5000/tcp, [::]:5002->5000/tcp   frontend2

>Output
 
 root@480aa5a6303b:/app# ping backend

 ping: backend: Name or service not known


## Step9 stop and delete containers

```
docker stop backend frontend1 frontend2
docker rm backend frontend1 frontend2
```

## Step10  delete ivolve-network

```
docker network rm ivolve-network
```

