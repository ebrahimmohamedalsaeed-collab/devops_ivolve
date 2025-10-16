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

## Step3 


