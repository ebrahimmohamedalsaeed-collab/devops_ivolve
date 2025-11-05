def call() {
    echo "Building Docker image..."
    sh 'docker build -t myapp:latest Jenkins_App/.'
}

