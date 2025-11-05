# 🧪 Lab 28: Jenkins Installation

## 🎯 Objective
Install and configure Jenkins using one of the following options:
- **Option 1:** Install Jenkins as a service on the host.
- **Option 2:** Deploy Jenkins as a container/pod.

---

## ⚙️ Option 1: Install Jenkins as a Service

 ## Step1 Update and install Java
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk -y
   java -version
```

## Step2 Add Jenkins repository and key
```
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
```
## Step3 Install Jenkins
```
sudo apt update
sudo apt install jenkins -y
```
## Step4 Enable and start the Jenkins service
```
sudo systemctl enable jenkins
sudo systemctl start jenkins
sudo systemctl status jenkins
```
