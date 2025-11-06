# Task 32 – Multi-branch CI/CD Pipeline with Jenkins

## 📝 Description

In this task, we create a multi-branch Jenkins pipeline that builds a Maven application, builds and pushes a Docker image, scans it, and deploys the application to a Kubernetes namespace corresponding to the branch.

The pipeline uses a shared library (EbrahimSharedLib) to simplify steps like building, testing, and deploying.

## 🛠 Prerequisites

Jenkins installed with multi-branch pipeline capability.

Docker installed and configured.

Kubernetes cluster with namespaces:

dev

stag

prod

main (optional)

Shared library configured in Jenkins: EbrahimSharedLib

Jenkins slave/agent with label slave.

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

    environment {
        DOCKER_USER = 'ebrahimrhh'
        DOCKER_PASS = credentials('dockerhub')
    }

    stages {

        // 🔹 Clean Workspace
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        // 🔹 Checkout
        stage('Checkout') {
            steps {
                checkout scm
                sh 'echo "Current branch: ${BRANCH_NAME}"'
                sh 'ls -R jenkins/task32/Jenkins_App/'  // تأكد من وجود الملفات
            }
        }

        // 🔹 Run Unit Tests
        stage('RunUnitTest') {
            steps { runUnitTest() }
        }

        // 🔹 Build Maven App
        stage('BuildApp') {
            steps { buildApp() }
        }

        // 🔹 Build Docker Image
        stage('BuildImage') {
            steps {
                script {
                    env.IMAGE_TAG = buildImage(DOCKER_USER)
                }
            }
        }

        // 🔹 Scan Image (optional)
        stage('ScanImage') {
            steps { scanImage() }
        }

        // 🔹 Push Image to DockerHub
        stage('PushImage') {
            steps {
                script { pushImage(env.IMAGE_TAG) }
            }
        }

        // 🔹 Remove local image to save space
        stage('RemoveImageLocally') {
            steps { removeImageLocally() }
        }

        // 🔹 Deploy on Kubernetes
        stage('DeployOnK8s') {
            steps {
                script { deployOnK8s(env.IMAGE_TAG) }
            }
        }
    }

    post {
        always {
            echo "✅ Pipeline for ${env.BRANCH_NAME} finished"
        }
        failure {
            echo "❌ Pipeline failed on branch ${env.BRANCH_NAME}"
        }
    }
}
```

## Verify 
### For Pipelines

![Screenshot Task32](https://github.com/ebrahimmohamedalsaeed-collab/devops_ivolve/blob/main/jenkins/task32/Screenshot%20(304).png)

![Screenshot Task32](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task32/Screenshot%20(303).png)

![Screenshot Task32](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task32/Screenshot%20(307).png)

![Screenshot Task32](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task32/Screenshot%20(306).png)

![Screenshot Task32](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task32/Screenshot%20(305).png)

### Check Pods 

![Screenshot Task32](https://raw.githubusercontent.com/ebrahimmohamedalsaeed-collab/devops_ivolve/main/jenkins/task32/Screenshot%20(309).png)






