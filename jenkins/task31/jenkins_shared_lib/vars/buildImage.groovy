def call() {
    echo "Building Docker image..."
    sh 'ls -la .'             // تأكد الملفات موجودة
    sh 'docker build -t myapp:latest ./Jenkins_App'
}

