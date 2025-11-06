def call(String imageTag) {
    def ns = env.BRANCH_NAME
    echo "Deploying ${imageTag} on namespace ${ns}"
    sh """
    sed 's|IMAGE_PLACEHOLDER|${imageTag}|' jenkins/task32/Jenkins_App/deployment-template.yaml > /tmp/deploy-${ns}.yaml
    kubectl apply -n ${ns} -f /tmp/deploy-${ns}.yaml
    kubectl get pods -n ${ns}
    """
}

