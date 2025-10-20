# Task2  Automated Web Server Configuration Using Ansible Playbooks


## Objective
Automate the configuration of a web server using Ansible:
- Install **Nginx**
- Customize the default web page
- Verify the configuration on the managed node


## Step1 create inventory file 

```
[webservers]
192.168.202.129 ansible_user=ebrahim ansible_become_pass=your pass of managed node
```

## Step2 Create webserver_setup.yml

```
---
- name: Configure Web Server
  hosts: webservers
  become: yes
  tasks:
    - name: Update apt cache
      apt:
        update_cache: yes

    - name: Install Nginx
      apt:
        name: nginx
        state: present

    - name: Ensure Nginx is running and enabled
      service:
        name: nginx
        state: started
        enabled: yes

    - name: Customize the default web page
      copy:
        dest: /var/www/html/index.html
        content: |
          <!DOCTYPE html>
          <html>
          <head>
              <title>Welcome to My Web Server</title>
          </head>
          <body>
              <h1>Hello from Ansible Automated Web Server!</h1>
          </body>
          </html>

    - name: Verify Nginx configuration
      command: nginx -t
      register: nginx_test
      ignore_errors: yes

    - name: Show Nginx test result
      debug:
        var: nginx_test.stdout
```

## Step3 Running the Playbook

```
ansible-playbook -i inventory.ini webserver_setup.yml
```

## Step4 Verification

```
http://192.168.202.129

>you will show

Hello from Ansible Automated Web Server!
```

