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
