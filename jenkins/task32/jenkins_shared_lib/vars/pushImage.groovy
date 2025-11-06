def call(String imageTag) {
    echo "Pushing image ${imageTag}"
    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
    sh "docker push ${imageTag}"
}

