// vars/deployOnK8s.groovy
def call(String imageTag) {
    def ns = env.BRANCH_NAME
    echo "Deploying ${imageTag} on namespace ${ns}"

    // المسار الكامل بالنسبة للـ workspace
    def workspace = pwd()
    def filePath = "${workspace}/jenkins/task32/Jenkins_App/deployment-template.yaml"
    
    if (!fileExists(filePath)) {
        error "❌ File ${filePath} does not exist on this node! Current workspace: ${workspace}"
    }

    sh """
    sed 's|IMAGE_PLACEHOLDER|${imageTag}|' ${filePath} > /tmp/deploy-${ns}.yaml
    kubectl apply -n ${ns} -f /tmp/deploy-${ns}.yaml
    kubectl get pods -n ${ns}
    """
}



