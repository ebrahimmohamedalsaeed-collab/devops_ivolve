# PayPal Phishing Simulation - Kubernetes Project

⚠️ **For Educational/Security Training Purposes Only**

## Project Description
This is a Kubernetes-based phishing simulation environment for security awareness training.

## Architecture
- **Frontend:** Nginx serving responsive PayPal login page
- **Backend:** Flask API for credential collection
- **Database:** MySQL for data storage

## Components
- 3 Deployments (Frontend, Backend, MySQL)
- 3 Services (2 NodePort, 1 ClusterIP)
- Docker images hosted on Docker Hub

## Technologies Used
- Kubernetes
- Docker
- Flask (Python)
- MySQL
- Nginx

## Docker Hub
Images available at: https://hub.docker.com/u/ebrahimrhh

## Project Structure
```
phishing-directory/
├── my-nginx/          # Frontend (Nginx + HTML)
├── Backend/           # Backend (Flask API)
└── project-files/     # Kubernetes YAML files
```

## Deployment Instructions
1. Build and push Docker images
2. Apply Kubernetes configurations from `project-files/`
3. Access frontend at `http://<Node-IP>:30000`

## Author
Ebrahim

## Disclaimer
This project is for educational purposes only. Unauthorized use is illegal.

## Check dockerhub 
**backend image**
![PayPal Screenshot](https://github.com/ebrahimmohamedalsaeed-collab/PayPal_K8s/raw/main/Screenshot%20(319).png)

**frontend image**
![Screenshot 320](https://github.com/ebrahimmohamedalsaeed-collab/PayPal_K8s/raw/main/Screenshot%20(320).png)

## Chech PayPal 
```
http://192.168.233.128:30000
```

![Screenshot 321](https://github.com/ebrahimmohamedalsaeed-collab/PayPal_K8s/raw/main/Screenshot%20(321).png)

## Check Database
```
ebrahim@ebrahim:~/devops_ivolve$ kubectl get pods | grep mysql
mysql-deployment-7d44c79947-7zxtg      1/1     Running       0             61m
```
```
kubectl exec -it mysql-deployment-7d44c79947-7zxtg -- mysql -u demo -pdemo phishing_database
```    
**will appear**
![Screenshot 318](https://github.com/ebrahimmohamedalsaeed-collab/PayPal_K8s/raw/main/Screenshot%20(318).png)










