def call(String imageName) {
    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
        sh """
        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
        docker build -t ${imageName} jenkins/task32/Jenkins_App
        docker push ${imageName}
        """
    }
}

