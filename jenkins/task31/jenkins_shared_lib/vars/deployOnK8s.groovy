def call() {
    echo "Deploying application on Kubernetes..."
    sh '''
    kubectl apply -f Jenkins_App/deployment.yaml
    kubectl get pods
    '''
}

