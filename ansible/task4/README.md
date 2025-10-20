# Task4 Securing Sensitive Data with Ansible Vault

## Description


In this lab, we automate MySQL installation and configuration using Ansible, while securing sensitive information such as database passwords using Ansible Vault. This ensures credentials remain encrypted and safe.


## Objective
Install MySQL on the managed node.

Create the iVolve database.

Create a MySQL user with full privileges on iVolve.

Encrypt sensitive information (database passwords) using Ansible Vault.

Validate database functionality by connecting and listing databases.

## Step1 inventory file
```
[managed_nodes]
192.168.202.129 ansible_user=ebrahim ansible_become_pass=pass of your vm node
```

## Step2 Vault File

```
ansible-vault create vault.yml

```
```
db_user: ivolve_user
db_password: StrongP@ss123
mysql_root_password: RootP@ss456
```
## Step3 secure-db.yml 

```
 update_cache: yes

    - name: Ensure MySQL service is running
      service:
        name: mysql
        state: started
        enabled: yes

    - name: Create iVolve database using Unix socket
      community.mysql.mysql_db:
        name: iVolve
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Create database user with privileges for localhost using Unix socket
      community.mysql.mysql_user:
        name: "{{ db_user }}"
        password: "{{ db_password }}"
        priv: 'iVolve.*:ALL'
        host: "localhost"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Flush MySQL privileges to apply changes
      community.mysql.mysql_user:
        name: "{{ db_user }}"
        host: "localhost"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Validate database by connecting and listing databases using Unix socket
      command: "mysql -u {{ db_user }} -p{{ db_password }} -e 'SHOW DATABASES;'"
      environment:
        MYSQL_UNIX_PORT: /var/run/mysqld/mysqld.sock
      register: db_list
      ignore_errors: yes

    - name: Show list of databases
      debug:
        var: db_list.stdout_lines
```

## Step4 Run Playbook

```
ansible-playbook -i inventory.ini secure-db.yml --ask-vault-pass
```

## Step5 Validation Steps 

```
sudo systemctl status mysql

mysql.service - MySQL Community Server
     Loaded: loaded (/usr/lib/systemd/system/mysql.service; enabled; preset: enabled)
     Active: active (running) since Mon 2025-10-20 15:16:08 EEST; 1h 34min ago

```

```
sudo mysql
SHOW DATABASES;

mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| iVolve             |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)
```




