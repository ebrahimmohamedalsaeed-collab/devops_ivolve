def call() {
    echo "Building application..."
    // Build the Maven project to generate the JAR
    sh 'mvn clean package -f Jenkins_App/pom.xml'
}
