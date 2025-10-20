# Task3  Structured Configuration Management with Ansible Roles



## Overview
In this lab, we use **Ansible Roles** to automate the installation and configuration of three essential DevOps tools on a managed node:

- **Docker**: Container runtime
- **kubectl**: Kubernetes CLI
- **Jenkins**: Continuous Integration server

This approach demonstrates **structured configuration management**, promoting reuse and modularity using Ansible roles.

---

## Lab Objectives

1. Create Ansible roles for:
   - Docker
   - kubectl
   - Jenkins
2. Write a playbook to execute all roles on managed nodes.
3. Verify that the installations are successful and services are running.

---

## Prerequisites

- Ansible installed on the control node
- Managed node(s) reachable via SSH
- sudo privileges on the managed node
- Basic knowledge of Ansible roles and playbooks

## Step1 create Docker Role 
```
---
- name: Update apt cache
  ansible.builtin.apt:
    update_cache: yes
    cache_valid_time: 3600

- name: Install dependencies for Docker
  ansible.builtin.apt:
    name:
      - apt-transport-https
      - ca-certificates
      - curl
      - gnupg-agent
      - software-properties-common
    state: present

- name: Add Docker GPG key
  ansible.builtin.apt_key:
    url: https://download.docker.com/linux/ubuntu/gpg
    state: present

- name: Add Docker repository
  ansible.builtin.apt_repository:
    repo: deb [arch=amd64] https://download.docker.com/linux/ubuntu {{ ansible_lsb.codename }} stable
    state: present

- name: Update apt cache after adding repo
  ansible.builtin.apt:
    update_cache: yes

- name: Install Docker Engine
  ansible.builtin.apt:
    name: docker-ce
    state: present

- name: Start and enable Docker service
  ansible.builtin.service:
    name: docker
    state: started
    enabled: yes
```

## Step2 create Kubectl Role
```

---
- name: Download kubectl binary
  ansible.builtin.get_url:
    url: https://dl.k8s.io/release/v1.27.3/bin/linux/amd64/kubectl
    dest: /usr/local/bin/kubectl
    mode: '0755'

- name: Ensure kubectl is executable
  ansible.builtin.file:
    path: /usr/local/bin/kubectl
    mode: '0755'

- name: Check kubectl version
  ansible.builtin.command: kubectl version --client
  register: kubectl_version
  changed_when: false
  ignore_errors: yes

- name: Display kubectl version
  debug:
    var: kubectl_version.stdout
```

## step3 Jenkins Role
```
---
- name: Install dependencies
  ansible.builtin.apt:
    name:
      - ca-certificates
      - curl
    state: present
  become: yes

- name: Create keyrings directory
  ansible.builtin.file:
    path: /usr/share/keyrings
    state: directory
    mode: '0755'
  become: yes

- name: Download Jenkins GPG key
  ansible.builtin.get_url:
    url: https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    dest: /usr/share/keyrings/jenkins-keyring.asc
    mode: '0644'
  become: yes

- name: Add Jenkins repository
  ansible.builtin.apt_repository:
    repo: "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/"
    state: present
    filename: jenkins
  become: yes

- name: Update apt cache
  ansible.builtin.apt:
    update_cache: yes
  become: yes

- name: Install Java (required for Jenkins)
  ansible.builtin.apt:
    name: openjdk-17-jre
    state: present
  become: yes

- name: Install Jenkins
  ansible.builtin.apt:
    name: jenkins
    state: present
  become: yes

- name: Start and enable Jenkins
  ansible.builtin.systemd:
    name: jenkins
    state: started
    enabled: yes
  become: yes
```

# Step4 Playbook
```
---
- name: Lab 3 - Configure Docker, kubectl, and Jenkins
  hosts: all
  become: yes

  roles:
    - docker
    - kubectl
    - jenkins
```

## Step5 inventory file
```
[webservers]
192.168.202.129 ansible_user=ebrahim ansible_become_pass=pass of your managed node
```
## Step6 Execution
```
ansible-playbook -i inventory.ini site.yml
```

## Step7 Verify installations
```
docker --version
kubectl version --client
systemctl status jenkins
```

## Step8 some nodes
```

on managed node
sudo apt update
sudo apt install openjdk-17-jdk
sudo update-alternatives --config java

>check jenkins status

  ebrahim@ebrahim-VMware-Virtual-Platform:~$ systemctl status jenkins
Warning: The unit file, source configuration file or drop-ins of jenkins.service changed on disk. Run 'systemctl daemon-reload' to reload units.
● jenkins.service - Jenkins Continuous Integration Server
     Loaded: loaded (/usr/lib/systemd/system/jenkins.service; enabled; preset: enabled)
     Active: active (running) since Mon 2025-10-20 15:16:53 EEST; 51min ago


> to show gui of jenkins
  http//192.168.202.129

will ask password adminstrator use
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

