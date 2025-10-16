# Task 4

# 🧩 Lab 4: Securing Sensitive Data with Ansible Vault

## 🎯 Objective
Automate MySQL installation, database and user creation, and secure credentials using **Ansible Vault**.

---

## 🪜 Step 1: Project Structure

```bash
ansible/
├── inventory.ini
├── lab4_playbook.yml
├── vault.yml
└── roles/
    └── mysql/
        ├── tasks/
        │   └── main.yml
        └── defaults/
            └── main.yml
```

---

## Step 2: Inventory File
```inventory.ini

[db_servers]
192.168.236.135 ansible_user=ebrahim  ansible_become_pass=pass of managed node
```

---

## Step 3: Role Tasks
```
---
---
- name: Automate MySQL setup with Vault
  hosts: db_servers
  become: true
  vars_files:
    - vault.yml

  tasks:
    - name: Install MySQL server
      ansible.builtin.package:
        name: mysql-server
        state: present

    - name: Install Python MySQL dependencies
      ansible.builtin.package:
        name: python3-pymysql
        state: present

    - name: Start and enable MySQL service
      ansible.builtin.service:
        name: mysql
        state: started
        enabled: true

    - name: Create iVolve database
      community.mysql.mysql_db:
        name: iVolve
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Create MySQL user and grant privileges
      community.mysql.mysql_user:
        name: "{{ db_user }}"
        password: "{{ db_password }}"
        priv: "iVolve.*:ALL"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Validate connection with new user
      ansible.builtin.shell: |
        mysql -u {{ db_user }} -p{{ db_password }} -e "SHOW DATABASES;"
      register: db_output

    - name: Display validation result
      ansible.builtin.debug:
        var: db_output.stdout


```

---



## Step 4: Role Defaults

```
---
db_name: iVolve
db_user: ivolve_user
db_password: "{{ vault_db_password }}"
```

---

## Step 5: Vault File
```
vault_db_password: "YourStrongPassword123"
```
---
```  use encrypt
ansible-vault encrypt vault.yml
```

---

## Step 7: Run the Playbook

```
ansible-playbook -i inventory.ini lab4_playbook.yml --ask-vault-pass
```
---

## Step 8: Validate Database Creation

```
mysql -u ivolve_user -p
SHOW DATABASES;
```
