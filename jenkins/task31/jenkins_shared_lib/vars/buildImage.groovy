def call() {
    echo "Building Docker image..."
    sh 'docker build -t myapp:latest jenkins/task31/Jenkins_App'
}
