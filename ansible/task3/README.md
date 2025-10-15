# Task 3

# Lab 3: Structured Configuration Management with Ansible Roles

## Objective
- Create structured Ansible roles for:
  - Docker
  - Kubernetes CLI (kubectl)
  - Jenkins
- Write a playbook to run all the roles
- Verify the installation on the managed node

## Step 2: Inventory File (\inventory.ini\)

\\\`
[servers]
192.168.236.135 ansible_user=ebrahim
\\\`


## Step 3: Docker Role Tasks (\roles/docker/tasks/main.yml\)

```yaml
- name: Install prerequisites
  ansible.builtin.apt:
    name:
      - apt-transport-https
      - ca-certificates
      - curl
      - software-properties-common
    state: present
    update_cache: yes

- name: Add Docker GPG key
  ansible.builtin.apt_key:
    url: https://download.docker.com/linux/ubuntu/gpg
    state: present

- name: Add Docker repository
  ansible.builtin.apt_repository:
    repo: deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable
    state: present

- name: Install Docker
  ansible.builtin.apt:
    name: docker-ce
    state: present
    update_cache: yes

- name: Ensure Docker service is running
  ansible.builtin.service:
    name: docker
    state: started
    enabled: true
```

---



## Step 4: Kubectl Role Tasks (\roles/kubectl/tasks/main.yml\)

```yaml
- name: Download kubectl
  ansible.builtin.get_url:
    url: https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl
    dest: /usr/local/bin/kubectl
    mode: '0755'

- name: Verify kubectl version
  ansible.builtin.command: kubectl version --client
  register: kubectl_version

- name: Show kubectl version
  ansible.builtin.debug:
    var: kubectl_version.stdout
```

---

## Step 5: Jenkins Role Tasks (\roles/jenkins/tasks/main.yml\)

```yaml
---
- name: Install Java (prerequisite for Jenkins)
  ansible.builtin.apt:
    name: openjdk-11-jdk
    state: present
    update_cache: yes

- name: Add Jenkins key
  ansible.builtin.apt_key:
    url: https://pkg.jenkins.io/debian-stable/jenkins.io.key
    state: present

- name: Add Jenkins repository
  ansible.builtin.apt_repository:
    repo: deb https://pkg.jenkins.io/debian-stable binary/
    state: present

- name: Install Jenkins
  ansible.builtin.apt:
    name: jenkins
    state: present
    update_cache: yes

- name: Ensure Jenkins service is running
  ansible.builtin.service:
    name: jenkins
    state: started
    enabled: true
  ```

  ---

  ## Step 6: Lab3 Playbook (\lab3_playbook.yml\)

```yaml
---
- name: Configure Docker, Kubectl, and Jenkins
  hosts: servers
  become: true
  roles:
    - docker
    - kubectl
    - jenkins
```

---

## Step 7: Run the Playbook

```bash
ansible-playbook -i inventory.ini lab3_playbook.yml
```

---

## Step 8: Verify Installation on Managed Node

```bash
# Check Docker
docker --version

# Check Kubectl
kubectl version --client

# Check Jenkins
systemctl status jenkins
```

---

## some notes
 ```jenkins

 sudo apt update

sudo apt install openjdk-17-jdk

sudo update-alternatives --config java

If Java 17 appears in the list, select it by typing its number.

java -version

sudo systemctl restart Jenkins
```
