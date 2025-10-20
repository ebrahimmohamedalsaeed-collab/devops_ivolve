# Task1

# Lab 1: Initial Ansible Configuration and Ad-Hoc Execution

## Objective
- Install and configure **Ansible Automation Platform** on the control node.
- Set up secure SSH communication with the managed node.
- Create an inventory and execute an ad-hoc command to verify functionality.

---
## Step1 Install Ansible on Control Node

```
sudo apt install ansible
```

## Step2 Generate SSH Key on Control Node

```
ssh-keygen -t rsa -b 4096

```
## Step3 Copy Public Key to Managed Node

```
ssh-copy-id user@managed-node-ip

ssh-copy-id ebrahim@192.168.202.129

after that

ssh ebrahim@192.168.202.129

```
## Step4 Create Inventory File 

```
[webservers]
192.168.202.129 ansible_user=ebrahim ansible_become_pass=123

```

## Step5 Test Connectivity

```
ansible -i inventory.ini managed_nodes -m ping
```

## Step6 Perform Ad-Hoc Command (Check Disk Space)

```
ansible -i inventory.ini managed_nodes -m command -a "df -h"
```






