def call() {
    echo "Building application..."
    sh 'mvn clean package -f jenkins/task31/Jenkins_App/pom.xml'
}
