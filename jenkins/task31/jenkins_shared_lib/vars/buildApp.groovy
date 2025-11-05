def call() {
    echo "Building application..."
    sh 'ls -la Jenkins_App'
    sh 'mvn clean package -f Jenkins_App/pom.xml'
}
