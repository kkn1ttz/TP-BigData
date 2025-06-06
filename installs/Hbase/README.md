# Instructions Installation Apache HBase sur VM

### Desinstaller l'ancienne version

* Stopper si deja lancer
```
/opt/hbase/bin/stop-hbase.sh
```

* Effacer les repertoires
```
sudo rm -rf /opt/hbase
sudo rm -rf /opt/hbase-data
```

### Installer JDK-11 (si pas déjà installé)

```sh
sudo dnf install -y java-11-openjdk-devel wget tar
```

### Télécharger & Installer HBase

* Télécharger HBase stable compatible Hadoop 3.x (et donc NiFi + Hive)

```sh
cd /tmp
sudo wget https://downloads.apache.org/hbase/2.5.11/hbase-2.5.11-bin.tar.gz
sudo tar -xvzf hbase-2.5.11-bin.tar.gz
sudo mv hbase-2.5.11 /opt/
cd /opt
sudo ln -s /opt/hbase-2.5.11 /opt/hbase
cd ~
```

### Configurer HBase (Standalone)

* Créer un dossier pour les données HBase

```sh
sudo mkdir -p /opt/hbase-data
sudo chown -R vagrant:vagrant /opt/hbase-data
```

* Configurer `hbase-site.xml`

```sh
sudo bash -c '
cat <<EOF > /opt/hbase/conf/hbase-site.xml
<configuration>
  <property>
    <name>hbase.rootdir</name>
    <value>hdfs://localhost:9000/hbase</value>
  </property>
  <property>
    <name>hbase.cluster.distributed</name>
    <value>true</value>
  </property>
  <property>
    <name>hbase.zookeeper.quorum</name>
    <value>localhost</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/opt/hbase-data/zookeeper</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.clientPort</name>
    <value>2181</value>
  </property>
  <property>
    <name>hbase.wal.provider</name>
    <value>filesystem</value>
  </property>
</configuration>
EOF
'
```

### Variables d'environnement

* Ajouter dans `~/.bashrc`

```sh
echo '
# HBase
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
export HBASE_HOME=/opt/hbase
export PATH=$PATH:$HBASE_HOME/bin
' >> ~/.bashrc

source ~/.bashrc
```

### Permissions

* Donner les droits à l'utilisateur `vagrant`

```sh
sudo chown -R vagrant:vagrant /opt/hbase-2.5.11
```

### Démarrer HBase

```sh
/opt/hbase/bin/start-hbase.sh
```

* Vérifier que HBase est démarré:

```sh
jps
```

> Doit afficher HMaster, HRegionServer, HQuorumPeer

```sh
hbase shell
```

* Vérifier le statut:

```hbase
status
```

* si output
```
1 active master, 0 backup masters, 1 servers, 0 dead, 2.0000 average load
```
Hbase OK ✅
