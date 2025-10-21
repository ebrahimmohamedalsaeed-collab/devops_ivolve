# 🧪 Lab 12: Docker Volume and Bind Mount with Nginx

## 🎯 Objective
- Persist Nginx logs using a **Docker Volume**  
- Serve a custom HTML file using a **Bind Mount**  

---

## ⚙️ Step 1: Create Docker Volume
```bash
docker volume create nginx_logs
```
### verify
```
docker volume ls

>will appear
local     nginx_logs

```

## Step 2: Create Directory for Bind Mount

```
mkdir -p nginx-bind/html
```

## Step 3: Create Custom HTML File
```
echo "Hello from Bind Mount" > nginx-bind/html/index.html
```

## Step 4: Run Nginx Container with Volume and Bind Mount

```
docker run -d \
  --name nginx-bind-vol \
  -p 8080:80 \
  -v nginx_logs:/var/log/nginx \
  -v $(pwd)/nginx-bind/html:/usr/share/nginx/html \
  nginx
```

### Step : Verify Nginx Page

```
http://localhost:8080
http://192.168.202.128

>will appear
Hello from Bind Mount
```

## Step 5: Update the HTML File
```
echo "Updated: Hello again from Bind Mount!" > nginx-bind/html/index.html
```
###  Verify Nginx Page

```
http://192.168.202.128:8080

>will appear
Updated: Hello again from Bind Mount!
```

## Step 6: Verify Logs Stored in Volume

```
docker exec -it nginx-bind-vol ls /var/log/nginx

>will see
access.log
error.log
```

## Step 7: Cleanup

```
docker stop nginx-bind-vol
docker rm nginx-bind-vol
```

### remove volume 

```
docker volume rm nginx_logs
```


 
