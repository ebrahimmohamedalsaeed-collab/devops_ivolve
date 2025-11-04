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
```
Started by user adminstrator
[Pipeline] Start of Pipeline
[Pipeline] node
Running on Jenkins in /var/lib/jenkins/workspace/Lab30-Pipeline
[Pipeline] {
[Pipeline] withEnv
[Pipeline] {
[Pipeline] stage
[Pipeline] { (Clone Repository)
[Pipeline] git
The recommended git tool is: NONE
No credentials specified
 > git rev-parse --resolve-git-dir /var/lib/jenkins/workspace/Lab30-Pipeline/.git # timeout=10
Fetching changes from the remote Git repository
 > git config remote.origin.url https://github.com/Ibrahim-Adel15/Jenkins_App.git # timeout=10
Fetching upstream changes from https://github.com/Ibrahim-Adel15/Jenkins_App.git
 > git --version # timeout=10
 > git --version # 'git version 2.43.0'
 > git fetch --tags --force --progress -- https://github.com/Ibrahim-Adel15/Jenkins_App.git +refs/heads/*:refs/remotes/origin/* # timeout=10
 > git rev-parse refs/remotes/origin/main^{commit} # timeout=10
Checking out Revision f37bb9984451bd6db859cbacad32ecbe472f003d (refs/remotes/origin/main)
 > git config core.sparsecheckout # timeout=10
 > git checkout -f f37bb9984451bd6db859cbacad32ecbe472f003d # timeout=10
 > git branch -a -v --no-abbrev # timeout=10
 > git branch -D main # timeout=10
 > git checkout -b main f37bb9984451bd6db859cbacad32ecbe472f003d # timeout=10
Commit message: "Add files via upload"
 > git rev-list --no-walk f37bb9984451bd6db859cbacad32ecbe472f003d # timeout=10
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Build App (Maven))
[Pipeline] sh
+ mvn clean package -DskipTests
[[1;34mINFO[m] Scanning for projects...
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--------------------------< [0;36mcom.example:demo[0;1m >--------------------------[m
[[1;34mINFO[m] [1mBuilding demo 0.0.1-SNAPSHOT[m
[[1;34mINFO[m] [1m--------------------------------[ jar ]---------------------------------[m
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-clean-plugin:3.3.2:clean[m [1m(default-clean)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] Deleting /var/lib/jenkins/workspace/Lab30-Pipeline/target
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-resources-plugin:3.3.1:resources[m [1m(default-resources)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] skip non existing resourceDirectory /var/lib/jenkins/workspace/Lab30-Pipeline/src/main/resources
[[1;34mINFO[m] skip non existing resourceDirectory /var/lib/jenkins/workspace/Lab30-Pipeline/src/main/resources
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-compiler-plugin:3.11.0:compile[m [1m(default-compile)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] Changes detected - recompiling the module! :source
[[1;34mINFO[m] Compiling 1 source file with javac [debug release 17] to target/classes
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-resources-plugin:3.3.1:testResources[m [1m(default-testResources)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] skip non existing resourceDirectory /var/lib/jenkins/workspace/Lab30-Pipeline/src/test/resources
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-compiler-plugin:3.11.0:testCompile[m [1m(default-testCompile)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] No sources to compile
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-surefire-plugin:3.1.2:test[m [1m(default-test)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] Tests are skipped.
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-jar-plugin:3.3.0:jar[m [1m(default-jar)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] Building jar: /var/lib/jenkins/workspace/Lab30-Pipeline/target/demo-0.0.1-SNAPSHOT.jar
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mspring-boot-maven-plugin:3.2.0:repackage[m [1m(repackage)[m @ [36mdemo[0;1m ---[m
[[1;34mINFO[m] Replacing main artifact /var/lib/jenkins/workspace/Lab30-Pipeline/target/demo-0.0.1-SNAPSHOT.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[[1;34mINFO[m] The original artifact has been renamed to /var/lib/jenkins/workspace/Lab30-Pipeline/target/demo-0.0.1-SNAPSHOT.jar.original
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] [1;32mBUILD SUCCESS[m
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] Total time:  7.125 s
[[1;34mINFO[m] Finished at: 2025-11-04T12:15:11+02:00
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Build Docker Image)
[Pipeline] sh
+ docker build -t ebrahimrhh/demo:latest .
DEPRECATED: The legacy builder is deprecated and will be removed in a future release.
            Install the buildx component to build images with BuildKit:
            https://docs.docker.com/go/buildx/

Sending build context to Docker daemon  19.73MB

Step 1/5 : FROM maven:sapmachine
 ---> 847aac5c1205
Step 2/5 : WORKDIR /app
 ---> Using cache
 ---> d6d51daeaaaa
Step 3/5 : COPY target/demo-0.0.1-SNAPSHOT.jar .
 ---> 65b15fd75546
Step 4/5 : CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
 ---> Running in e6ce6e2f3f48
 ---> Removed intermediate container e6ce6e2f3f48
 ---> 60eb886bcd88
Step 5/5 : EXPOSE 8080
 ---> Running in c4134b7bd1c8
 ---> Removed intermediate container c4134b7bd1c8
 ---> ad61af4f4643
Successfully built ad61af4f4643
Successfully tagged ebrahimrhh/demo:latest
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Push to Docker Hub)
[Pipeline] withCredentials
Masking supported pattern matches of $DOCKER_PASSWORD
[Pipeline] {
[Pipeline] sh
Warning: A secret was passed to "sh" using Groovy String interpolation, which is insecure.
		 Affected argument(s) used the following variable(s): [DOCKER_PASSWORD]
		 See https://jenkins.io/redirect/groovy-string-interpolation for details.
+ echo ****
+ docker login -u ebrahimrhh --password-stdin
Login Succeeded
[Pipeline] sh
+ docker push ebrahimrhh/demo:latest
The push refers to repository [docker.io/ebrahimrhh/demo]
ce6c1dc102ce: Preparing
d31cd66b7aca: Preparing
2dca30a8b731: Preparing
5f70bf18a086: Preparing
bcc4a2c53fac: Preparing
55193bdfa995: Preparing
940b2c1c2c3d: Preparing
3d5b50c3ca56: Preparing
073ec47a8c22: Preparing
55193bdfa995: Waiting
940b2c1c2c3d: Waiting
3d5b50c3ca56: Waiting
073ec47a8c22: Waiting
2dca30a8b731: Layer already exists
bcc4a2c53fac: Layer already exists
5f70bf18a086: Layer already exists
55193bdfa995: Layer already exists
940b2c1c2c3d: Layer already exists
3d5b50c3ca56: Layer already exists
073ec47a8c22: Layer already exists
d31cd66b7aca: Pushed
ce6c1dc102ce: Pushed
latest: digest: sha256:bcad149af430f48a60064fd8fc76fa847f314d0895cc140e0ebd29af55609eb6 size: 2203
[Pipeline] }
[Pipeline] // withCredentials
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Delete Local Image)
[Pipeline] echo
Deleting local Docker image...
[Pipeline] sh
+ docker rmi ebrahimrhh/demo:latest
Untagged: ebrahimrhh/demo:latest
Untagged: ebrahimrhh/demo@sha256:bcad149af430f48a60064fd8fc76fa847f314d0895cc140e0ebd29af55609eb6
Deleted: sha256:ad61af4f4643ff634bb661704bc91dc3b908730dad0885aeff3696a64e834d6a
Deleted: sha256:60eb886bcd882f23a41349e2a9af75cd3e7f69d21b6b1532800a4e349d0bf84d
Deleted: sha256:65b15fd75546f49312ef5ca86ff7835afd02c144668aa714e8f68a92d69b4e30
Deleted: sha256:a0d783efe0b9e964f355db6134bd38e8d09ab24a2f9c4e503bd6d13971a19566
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Create deployment.yaml)
[Pipeline] echo
Creating deployment.yaml in workspace...
[Pipeline] writeFile
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Deploy to Kubernetes)
[Pipeline] echo
Deploying new image to Kubernetes...
[Pipeline] sh
+ kubectl apply -f deployment.yaml
deployment.apps/demo-app created
service/demo-app-service created
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Declarative: Post Actions)
[Pipeline] echo
Pipeline finished (Always section).
[Pipeline] echo
Pipeline succeeded!
[Pipeline] }
[Pipeline] // stage
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // node
[Pipeline] End of Pipeline
Finished: SUCCESS
```

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



