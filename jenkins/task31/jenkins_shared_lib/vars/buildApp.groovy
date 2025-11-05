def call() {
    echo "Building application..."
    // استخدام المسار الكامل من root workspace
    sh 'mvn clean package -f jenkins/task31/Jenkins_App/pom.xml'
}
