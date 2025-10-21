# 🧪 Lab 11: Managing Docker Environment Variables Across Build and Runtime

## 🎯 Objective
Learn how to manage environment variables in Docker at **build-time** and **runtime** using:
- Variables defined in the **Dockerfile**
- Variables passed in the **run command**
- Variables loaded from an **environment file**

---


## Step1 Clone the Application Code
```bash
git clone https://github.com/Ibrahim-Adel15/Docker-3.git
cd Docker-3
```

## Step2 Create a Dockerfile

```
# Use official Python base image
FROM python:3.9-slim

# Set production environment variables (build-time)
ENV APP_MODE=production
ENV APP_REGION=canada-west

# Set working directory
WORKDIR /app

# Copy project files into the container
COPY . /app

# Install Flask
RUN pip install flask

# Expose port 8080
EXPOSE 8080

# Run the Flask app
CMD ["python", "app.py"]
```

## Step3 Build the Docker Image

```
docker build -t myflask-app .
```
## Step4 Run Containers in Different Environments


### (a) Development (using -e flags)

```
docker run -d -p 8080:8080 --name dev-app \
-e APP_MODE=development \
-e APP_REGION=us-east \
myflask-app
```

### (b) Staging (using .env file) 
   Create a file named staging.env inside the Docker-3 directory

 ```
  APP_MODE=staging
  APP_REGION=us-west
```
```
docker run -d -p 8081:8080 --name staging-app \
--env-file staging.env \
myflask-app
```

### (c) Production (values from Dockerfile) 
```
docker run -d -p 8082:8080 --name prod-app myflask-app

```

### show the result 

```
http://192.168.202.128

App mode: development, Region: us-east
```






