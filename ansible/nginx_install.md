## Step 1: Install Ansible

```bash
sudo apt update
sudo apt install ansible -y
ansible --version
```

---

## Step 2: Generate SSH Key

```bash
ssh-keygen -t rsa -b 4096
```

---

## Step 3: Copy Public Key to Managed Node

```bash
ssh-copy-id user@managed-node-ip
```

---

## Step 4: Create Inventory File

```ini
[webservers]
managed-node-ip ansible_user=user
```

---

## Step 5: Run Ad-Hoc Command

```bash
ansible webservers -i inventory.ini -m shell -a "df -h"
```

---

## Best Practices

- Ensure SSH connectivity between control and managed nodes.
- Test SSH key authentication before running commands.
- Use descriptive names for inventory groups.
- Keep your Ansible files organized for future labs.





