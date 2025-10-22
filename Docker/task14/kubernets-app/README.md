# task14: Containerized Node.js and MySQL Stack Using Docker Compose

## Step1
Clone the application source code:
```bash
git clone https://github.com/Ibrahim-Adel15/kubernets-app.git
cd kubernets-app
```

## Step2 Create a Dockerfile inside kubernets-app

```
FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000

CMD ["npm", "start"]
```

## Step3 docker-compose.yml
```

version: '3.8'

services:
  db:
    image: mysql:8
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: ivolve
    volumes:
      - db_data:/var/lib/mysql
    networks:
      - app-network

  app:
    build: .
    container_name: node-app
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db
      DB_USER: root
      DB_PASSWORD: root123
    depends_on:
      - db
    volumes:
      - ./logs:/app/logs
    networks:
      - app-network

volumes:
  db_data:

networks:
  app-network:
```

## Step4 Running the Stack

```
docker compose up -d --build
```
```
docker ps

ebrahim@ebrahim:~/devops_ivolve/Docker/task14/kubernets-app$ docker ps
CONTAINER ID   IMAGE               COMMAND                  CREATED          STATUS          PORTS                                         NAMES
e61d95b18ea3   kubernets-app_app   "docker-entrypoint.s…"   13 seconds ago   Up 13 seconds   0.0.0.0:3000->3000/tcp, [::]:3000->3000/tcp   node-app
4f5818d4cd3e   mysql:8             "docker-entrypoint.s…"   13 seconds ago   Up 13 seconds   3306/tcp, 33060/tcp                           mysql-db
```

## Step5 Verify Application

### health
```
curl http://localhost:3000/health

> will appear

ebrahim@ebrahim:~/devops_ivolve/Docker/task14/kubernets-app$ curl http://localhost:3000/health
🚀 iVolve web app is working! Keep calm and code on! 🎉
```

### ready

```
curl http://localhost:3000/ready

> will appear

ebrahim@ebrahim:~/devops_ivolve/Docker/task14/kubernets-app$ curl http://localhost:3000/ready
👍 iVolve web app is ready to rock and roll! 🤘

```

### logs 

```
ebrahim@ebrahim:~/devops_ivolve/Docker/task14/kubernets-app$ ls logs
cat logs/access.log
access.log
172.18.0.1 - - [22/Oct/2025:09:44:10 +0000] "GET /health HTTP/1.1" 200 59 "-" "curl/8.5.0"
172.18.0.1 - - [22/Oct/2025:09:44:20 +0000] "GET /health HTTP/1.1" 200 59 "-" "curl/8.5.0"
172.18.0.1 - - [22/Oct/2025:09:44:31 +0000] "GET /ready HTTP/1.1" 200 51 "-" "curl/8.5.0"

```
### mysql 
```
docker exec -it mysql-db mysql -uroot -p
```
Enter password: root123
```
SHOW DATABASES;
```
> will appear

```
mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| ivolve             |
| mysql              |
| performance_schema |
| sys                |
+--------------------+

```

## Step6 Push Docker Image to DockerHub

```
docker login

> will appear
Login Succeeded
```

### Tag the image and push to dockerhub
```
 docker tag kubernets-app_app ebrahimrhh/node-app:lab14
 docker push ebrahimrhh/node-app:lab14

> will appear
lab14: digest: sha256:c5a1ee35e7c88fd6f68e91437b4e0c60c01ff0ea39cfbefde2522490529ea438 size: 1992
```

## Step7 delete docker network volume for task14

```
docker stop node-app mysql-db

docker rm node-app mysql-db

docker volume rm kubernets-app_db_data

docker network rm kubernets-app_app-network
```



