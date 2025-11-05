def call() {
    echo "Deploying application on Kubernetes..."
    sh '''
    kubectl apply -f jenkins/task31/Jenkins_App/deployment.yaml
    kubectl get pods
    '''
}
