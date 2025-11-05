# Lab 31: CI/CD Pipeline with Jenkins, Docker & Kubernetes

## 1️⃣ Overview
In this lab, we set up a **CI/CD pipeline** for a Spring Boot application using:

## ## 📁 Folder Structure

```
jenkins_shared_library/
├── vars/
   ├── runUnitTests.groovy
   ├── buildApp.groovy
   ├── buildImage.groovy
   ├── scanImage.groovy
   ├── pushImage.groovy
   ├── removeImage.groovy
   └── deployOnK8s.groovy

```
## 🧩 Library Functions

| Function Name      | Description                                   |
|--------------------|-----------------------------------------------|
| `runUnitTests()`   | Runs unit tests using Maven                   |
| `buildApp()`       | Builds the Java application using Maven       |
| `buildImage(img)`  | Builds a Docker image from the app source     |
| `scanImage(img)`   | Scanning the Docker image|
| `pushImage(img, user, pass)` | Pushes image to Docker Hub         |
| `removeImage(img)` | Removes image locally to save space           |
| `deployOnK8s(api, token, ns, img, file)` | Deploys to Kubernetes  |

---

## Jenkinsfile
```
@Library('EbrahimSharedLib') _

pipeline {
    agent { label 'slave' }

    stages {
        stage('RunUnitTest') {
            steps { runUnitTest() }
        }
        stage('BuildApp') {
            steps { buildApp() }
        }
        stage('BuildImage') {
            steps { buildImage() }
        }
        stage('ScanImage') {
            steps { scanImage() }
        }
        stage('PushImage') {
            steps { pushImage() }
        }
        stage('RemoveImageLocally') {
            steps { removeImageLocally() }
        }
        stage('DeployOnK8s') {
            steps { deployOnK8s() }
        }
    }

    post {
        always { echo "✅ CI/CD Pipeline finished successfully!" }
    }
}
```
## Verify 
![Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task31/Screenshot%20.png)

![Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task31/Screenshot%20(298).png)


![Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task31/Screenshot%20(297).png)


![Screenshot](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task31/Screenshot%20(295).png)



