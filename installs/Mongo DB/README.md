# Instructions Mise à jour MongoDB sur VM 

### Supprimer MongoDB existant

```
sudo systemctl stop mongod

sudo yum erase -y mongodb-org

sudo rm -rf /var/lib/mongo/*

sudo rm -rf /var/log/mongodb/*
```

### Créer le fichier repo MongoDB 4.4 (copiena miaraka ireo ambany ireo)

```
sudo tee /etc/yum.repos.d/mongodb-org-4.4.repo <<EOF
[mongodb-org-4.4]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/\$releasever/mongodb-org/4.4/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.4.asc
EOF
```

### Installer MongoDB
```
sudo yum install -y mongodb-org
```

### Démarrer MongoDB et activer au boot
```
sudo systemctl start mongod
sudo systemctl enable mongod
```

### Vérifier que MongoDB tourne bien
```
sudo systemctl status mongod
mongo --eval 'db.runCommand({ connectionStatus: 1 })'
```

### Vérifier l’espace disque et permissions
- Assurer que `/var/lib/mongo` appartient bien à `mongod:mongod` :

```
sudo chown -R mongod:mongod /var/lib/mongo
sudo chmod -R 755 /var/lib/mongo
```
7. Lancer mongoDB

```
mongo
```