# Instructions pour execution de  l'analyse

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/blob/master/installs/Apache%20Nifi/README.md)
* [installs/Hbase](https://github.com/kkn1ttz/TP-M/blob/master/installs/Hbase/README.md)
* [installs/Mongo DB](https://github.com/kkn1ttz/TP-M/blob/master/installs/Mongo%20DB/README.md)
* [connexions/mysql-nifi-hive](https://github.com/kkn1ttz/TP-M/blob/master/connexions/mysql-nifi-hive/README.md)
* [connexions/hdfs-nifi-hive](https://github.com/kkn1ttz/TP-M/blob/master/connexions/hdfs-nifi-hive/README.md)
* [connexions/mongo-nifi-hive](https://github.com/kkn1ttz/TP-M/blob/master/connexions/mongo-nifi-hive/README.md)
* [connexions/hbase-nifi-hive](https://github.com/kkn1ttz/TP-M/blob/master/connexions/hbase-nifi-hive/README.md)

## Mise en place initiale
* Placer le fichier `analyse.py`de ce dossier `analyse` dans un dossier dans l'emplacement local du VM , exemple : `TpBigData/analyse`

* Lancer HDFS

```
start-dfs.sh
```

* Démarrer Hive
```
nohup hive --service metastore > /dev/null &
nohup hiveserver2 > /dev/null &
```

* Telecharger JDBC Hive 
```
mkdir -p /vagrant/lib
wget https://repo1.maven.org/maven2/org/apache/hive/hive-jdbc/3.1.2/hive-jdbc-3.1.2-standalone.jar -P /vagrant/lib

```

* Creer un `venv`en dehors du dossier `/vagrant`

```
cd ~
python3.9 -m venv myenv
```

* Activer le venv
```
source ~/myenv/bin/activate
```

## Installation des dependances necessaires et lancement du script

* Aller dans le dossier du projet
```
cd /vagrant/TpBigData/analyse/
```

* Installer les dependances 
```
pip install pandas==1.5.3 numpy==1.23.5 jaydebeapi JPype1
```

* Lancer le script
```
python analyse.py
```

> Les resulats des analyses doivent s'afficher sous forme de tableau


* Pour desactiver le venv et revenir a la VM normal

```
deactivate
```



