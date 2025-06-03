# ✅ Guide d’exécution du code Java MySQL dans une VM Vagrant

Ce guide vous permet de configurer et d’exécuter le code Java de connexion et d’insertion MySQL à partir de fichiers CSV, à l’intérieur de votre machine virtuelle Vagrant.

---

## 📁 1. Copier les fichiers nécessaires

- Copier le dossier `dataset` depuis le projet GitHub vers votre VM Vagrant :

```bash
cp -r /vagrant/dataset ~/
```

- Copier le fichier `MySQLConnectionTest.java` dans le répertoire cible :

```bash
mkdir -p /vagrant/script/mysql
cp /vagrant/path/to/MySQLConnectionTest.java /vagrant/script/mysql/
cd /vagrant/script/mysql
```

---

## ☑️ 2. Installer les dépendances

Télécharger les bibliothèques nécessaires :

```bash
# 📦 Driver JDBC MySQL
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.28.tar.gz

# 📦 Librairie OpenCSV
wget https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/opencsv-5.7.1.jar

# 📦 Librairie Apache Commons Lang (dépendance de OpenCSV)
wget https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar
```

Décompresser l’archive JDBC MySQL :

```bash
tar -xzf mysql-connector-java-8.0.28.tar.gz
cp mysql-connector-java-8.0.28/mysql-connector-java-8.0.28.jar .
```

---

## 🛠 3. Compiler le code Java

Dans le dossier contenant `MySQLConnectionTest.java` :

```bash
javac -cp ".:mysql-connector-java-8.0.28.jar:opencsv-5.7.1.jar:commons-lang3-3.12.0.jar" MySQLConnectionTest.java
```

---

## ▶️ 4. Exécuter le programme

```bash
java -cp ".:mysql-connector-java-8.0.28.jar:opencsv-5.7.1.jar:commons-lang3-3.12.0.jar" MySQLConnectionTest
```

---

## ✅ À vérifier avant d'exécuter

- **MySQL est bien installé et en cours d’exécution** sur la VM :  
  ```bash
  sudo systemctl status mysqld
  ```

- **L’utilisateur root MySQL a un mot de passe vide** ou modifiez le fichier `MySQLConnectionTest.java` avec vos identifiants.

- **Le port 3306 est accessible** dans la VM.
