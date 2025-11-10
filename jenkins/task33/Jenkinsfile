pipeline {
    agent any
    stages {
        stage('Build Backend Image') {
            steps { 
                dir('Backend') { 
                    sh 'docker build -t ebrahimrhh/phishing-backend:latest .' 
                } 
            }
        }
        stage('Build Frontend Image') {
            steps { 
                dir('my-nginx') { 
                    sh 'docker build -t ebrahimrhh/phishing-frontend:latest .' 
                } 
            }
        }
        stage('Push Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                    sh '''
                        echo "$DOCKERHUB_PASS" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker push $DOCKERHUB_USER/phishing-backend:latest
                        docker push $DOCKERHUB_USER/phishing-frontend:latest
                        docker logout
                    '''
                }
            }
        }
        stage('Clean Local Images') {
            steps {
                sh 'docker rmi ebrahimrhh/phishing-backend:latest || true'
                sh 'docker rmi ebrahimrhh/phishing-frontend:latest || true'
            }
        }
        stage('Update Deployment YAMLs') {
            steps {
                sh '''
                sed -i "s|image: .*phishing-backend.*|image: ebrahimrhh/phishing-backend:latest|" project-files/backend-deployment.yaml
                sed -i "s|image: .*phishing-frontend.*|image: ebrahimrhh/phishing-frontend:latest|" project-files/frontend-deployment.yaml
                '''
            }
        }
        stage('Push Updates to GitHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    dir('project-files') {
                        sh '''
                        git checkout main || git checkout -b main
                        git add .
                        git commit -m "Update images for ArgoCD deployment" || echo "No changes to commit"
                        git push https://$GIT_USER:$GIT_PASS@github.com/ebrahimmohamedalsaeed-collab/PayPal_K8s.git main
                        '''
                    }
                }
            }
        }
    }
} // <- تأكد إن القوس ده موجود
