# Task5 Automated Host Discovery with Ansible Dynamic Inventory



## 🎯 Objective
Automate the discovery and management of AWS EC2 instances using **Ansible Dynamic Inventory**.  
The goal is to automatically detect running EC2 instances with a specific tag and manage them using Ansible.

## at the end you will show 
```
ebrahim@ebrahim:~/devops_ivolve/ansible/task5$ ansible-inventory --graph -v
Using /home/ebrahim/devops_ivolve/ansible/task5/ansible.cfg as config file
Using inventory plugin 'ansible_collections.amazon.aws.plugins.inventory.aws_ec2' to process inventory source '/home/ebrahim/devops_ivolve/ansible/task5/aws_ec2.yml'
@all:
  |--@ungrouped:
  |--@aws_ec2:
  |  |--ec2-54-88-67-35.compute-1.amazonaws.com
  ```
