# Task 2

## Objective
Automate the configuration of a web server using Ansible:
- Install Nginx
- Customize the web page
- Verify the configuration on the managed node

---

## Step 1: Prepare Inventory File
Create \`inventory.ini\`:

```ini
[webservers]
192.168.236.135 ansible_user=ebrahim
```

> Replace \`192.168.236.135\` and \`ebrahim\` with your managed node IP and username.

---

## Step 2: Create Ansible Playbook
Create \`webserver.yml\`:

```yaml
---
- name: Automated Web Server Configuration
  hosts: webservers
  become: true
  tasks:

    - name: Update apt cache
      ansible.builtin.apt:
        update_cache: yes

    - name: Install Nginx
      ansible.builtin.apt:
        name: nginx
        state: present

    - name: Ensure Nginx service is running and enabled
      ansible.builtin.service:
        name: nginx
        state: started
        enabled: true

    - name: Customize the web page
      ansible.builtin.copy:
        dest: /var/www/html/index.html
        content: |
          <html>
          <head><title>Welcome to Lab 2</title></head>
          <body>
          <h1>Hello from Ansible Web Server!</h1>
          </body>
          </html>
        owner: www-data
        group: www-data
        mode: '0644'

    - name: Verify Nginx is serving the page
      ansible.builtin.uri:
        url: http://localhost
        return_content: yes
      register: result

    - name: Show verification result
      ansible.builtin.debug:
        var: result.content
```

---

## Step 3: Run the Playbook
```bash
ansible-playbook -i inventory.ini webserver.yml
```

---

## Step 4: Verify on Managed Node
- Open a browser or use \`curl\`:

```bash
curl http://192.168.236.135
```

- You should see the customized page:  
**"Hello from Ansible Web Server!"**

---

## Notes
- Ensure SSH connectivity between the control node and managed node.  
- Use \`become: true\` for tasks requiring root privileges.  
- Customize the HTML content as needed for your lab.
EOL
