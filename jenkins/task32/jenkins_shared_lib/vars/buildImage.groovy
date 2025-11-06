def call(String dockerUser = 'ebrahimrhh') {
    def imageTag = "${dockerUser}/myapp:${env.BRANCH_NAME}"
    echo "Building image ${imageTag}"
    sh "docker build -t ${imageTag} jenkins/task32/Jenkins_App"
    return imageTag
}

