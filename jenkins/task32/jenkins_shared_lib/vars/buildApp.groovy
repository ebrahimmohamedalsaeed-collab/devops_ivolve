def call() {
    echo "Building Maven App..."
    sh "mvn clean package -f jenkins/task32/Jenkins_App/pom.xml"
}

