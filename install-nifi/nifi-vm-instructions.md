# Instructions Installation Apache Nifi dans VM 

- vagrant up
- vagrant ssh

### Installer JDK-11
- Telecharger et installer jdk 11 (lancer meme si deja installer pas de probleme)
```sh
sudo dnf install -y java-11-openjdk-devel wget tar
```


### Telecharger & Installer Nifi
- Telecharger Nifi (version deja compatible avec tous les composants)
```
wget https://archive.apache.org/dist/nifi/1.25.0/nifi-1.25.0-bin.zip -P /tmp
```

- Unpack le `.zip`
```
sudo unzip /tmp/nifi-1.25.0-bin.zip -d /opt
```

- Creer un `simlink`
```
sudo ln -s /opt/nifi-1.25.0 /opt/nifi
```

### Configurer Nifi
- Indiquer l'emplacement des libs Hadoop native à NiFi
```
sudo -u nifi bash -c '
echo "
# --- custom paths ---
java.arg.18=-Djava.library.path=/usr/local/hadoop-3.3.6/lib/native
nifi.nar.library.directory=./extensions
" >> /opt/nifi/conf/bootstrap.conf
'
```

- Copier les processeurs Hbase et Hadoop dans `Nifi/extensions`
```
sudo -u nifi mkdir -p /opt/nifi/extensions

sudo -u nifi cp /usr/local/hadoop-3.3.6/etc/hadoop/*.xml \
                /opt/hbase/conf/hbase-site.xml           \
                /opt/nifi/extensions/
```

- Desactiver la securite HTTPS
```
sudo sed -i \
  -e 's|^nifi.web.https.*|# &|' \
  -e 's|^nifi.security.*|# &|' \
  -e '/^nifi.remote.input.secure=/d' \
  -e '$a nifi.remote.input.secure=false' \
  /opt/nifi/conf/nifi.properties
```

- configurer le `PORT` par defaut de Nifi
```
sudo sed -i '/^nifi.web.http.port=/c nifi.web.http.port=8080' /opt/nifi/conf/nifi.properties
```

- configurer `JAVA_HOME` utiliser par Nifi
```
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk' \
  | sudo tee -a /opt/nifi/bin/nifi-env.sh > /dev/null
```

- Donner tout les droit a l'user vagrant sur le repertoire nifi
```
sudo chown -R vagrant:vagrant /opt/nifi-1.25.0
```

### Configurer le VM
- Ajouter ces ligne parmis les `forwarded_port` dans le `Vagrantfile`
```
# APACHE NIFI
config.vm.network "forwarded_port", guest: 8080, host: 8080, auto_correct: true
```

- Quitter et relancer le VM
```
exit

vagrant halt

vagrant up

vagrant ssh
```

### Lancer Nifi
- arahana vavaka kely
```
/opt/nifi/bin/nifi.sh start
```

- ouvrir le navigateur a l'addresse `http://localhost:8080/nifi`