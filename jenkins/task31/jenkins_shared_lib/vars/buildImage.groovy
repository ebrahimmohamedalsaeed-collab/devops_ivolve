def call() {
    echo "Building Docker image..."
    // استخدم المسار الكامل داخل الـ repo
    sh 'docker build -t myapp:latest jenkins/task31/Jenkins_App'
}
