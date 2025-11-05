def call() {
    echo "Removing local Docker image..."
    sh 'docker rmi myapp:latest || true'
}

