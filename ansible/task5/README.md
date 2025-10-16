# Lab 5: Automated Host Discovery with Ansible Dynamic Inventory

## 🔹 Description
This lab demonstrates **Ansible Dynamic Inventory** to automatically discover AWS EC2 instances tagged with `ivolve` and manage them using playbooks.  
Dynamic inventory allows Ansible to adapt to cloud infrastructure changes without manually updating the hosts file.

---

## 🛠 Prerequisites

- Ansible >= 2.10  
- Python 3.6+  
- AWS CLI installed and configured  
- IAM user with permissions to read EC2 (`AmazonEC2ReadOnlyAccess`)  
- SSH key pair for Ubuntu EC2 instances

  ---

  ## 🟢 Step 1: Configure AWS CLI

Configure AWS credentials on your VM:

```bash
aws configure
```
---

```
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: .................
Default region name [None]: us-east-1
Default output format [None]: json
```

---
```
to show your instances use

aws ec2 describe-instances --region us-east-1 --query "Reservations[*].Instances[*].[Tags[?Key=='Name'].Value, PublicIpAddress, State.Name]" --output table

```

---


## Step2 Create Dynamic Inventory

```
plugin: amazon.aws.aws_ec2
regions:
  - us-east-1
filters:
  tag:Name: ivolve
```
---

## Step3 ansible.cfg

```

[defaults]
inventory = ./aws_ec2.yml
enable_plugins = amazon.aws.aws_ec2
remote_user = ubuntu            # أو ec2-user حسب AMI
private_key_file = /home/ebrahim/devops_ivolve/ansible/task5/ivolve-key.pem
host_key_checking = False

```

---

## Step4 Test Dynamic Inventory

```
ansible-inventory --graph -v

```
---

###  you will show your instance that you tagged it  

```'
@all:
  |--@ungrouped:
  |--@aws_ec2:
  |  |--ec2-54-88-67-35.compute-1.amazonaws.com
```
