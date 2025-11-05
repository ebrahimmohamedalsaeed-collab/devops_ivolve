# Lab 30: Jenkins Pipeline for Application Deployment

## Overview

This lab demonstrates a **complete CI/CD pipeline** using **Jenkins, Docker, and Kubernetes**.  
The pipeline performs the following tasks automatically:

1. Run unit tests and build the Java application using Maven.
2. Build a Docker image from the project Dockerfile.
3. Push the Docker image to Docker Hub.
4. Delete the local Docker image to free space.
5. Update the Kubernetes deployment manifest with the new image.
6. Deploy the application to a Kubernetes cluster.
7. Handle post-build actions (always, success, failure).

---

## Step1 Clone Repository

```bash
git clone https://github.com/Ibrahim-Adel15/Jenkins_App.git
cd Jenkins_App

```


## Step2 Create Jenkinsfile
```
pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = 'dockerhub' // تم تغييره هنا
        IMAGE_NAME = 'ebrahimrhh/demo:latest'
        GIT_REPO = 'https://github.com/Ibrahim-Adel15/Jenkins_App.git'
        GIT_BRANCH = 'main'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build App (Maven)') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS}", 
                                                  passwordVariable: 'DOCKER_PASSWORD', 
                                                  usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                    sh "docker push ${IMAGE_NAME}"
                }
            }
        }

        stage('Delete Local Image') {
            steps {
                echo 'Deleting local Docker image...'
                sh "docker rmi ${IMAGE_NAME}"
            }
        }

        stage('Create deployment.yaml') {
            steps {
                echo 'Creating deployment.yaml in workspace...'
                writeFile file: 'deployment.yaml', text: """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: demo-app
  template:
    metadata:
      labels:
        app: demo-app
    spec:
      containers:
        - name: demo-app
          image: ${IMAGE_NAME}
          ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: demo-app-service
spec:
  selector:
    app: demo-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: NodePort
"""
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo 'Deploying new image to Kubernetes...'
                sh "kubectl apply -f deployment.yaml"
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished (Always section).'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```
## Step3 Create deployment.yaml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: demo-app
  template:
    metadata:
      labels:
        app: demo-app
    spec:
      containers:
        - name: demo-app
          image: ebrahimrhh/demo:latest
          ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: demo-app-service
spec:
  selector:
    app: demo-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: NodePort
```

## Show pipeline 
![Jenkins Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task30/Jenkins_App/Screenshot%20(292).png)


## verify 
```
ebrahim@ebrahim:~/devops_ivolve/jenkins/task30/Jenkins_App$ kubectl get pods
NAME                                   READY   STATUS        RESTARTS       AGE
demo-app-698bf7f46b-l9nrn              1/1     Running       0              4m45s
```
```
ebrahim@ebrahim:~/devops_ivolve/jenkins/task30/Jenkins_App$ kubectl get svc
NAME               TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
demo-app-service   NodePort    10.96.166.136   <none>        8080:31869/TCP   67s
```
## Test app
```
ebrahim@ebrahim:~/devops_ivolve/jenkins/task30/Jenkins_App$ curl http://192.168.233.128:31869
Hello from Dockerized Spring Boot!ebrahim
```




