# Instructions Connexion HDFS vers HIVE via Nifi

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)
* [connexions/mysql-nifi-hive](https://github.com/kkn1ttz/TP-M/tree/master/connexions/mysql-nifi-hive)

## Mise en place initiale

* Lancer HDFS

```
start-dfs.sh
```

## Mise en place des données dans HDFS (Simulation Source)

* Créer un dossier source dans HDFS et ajouter un fichier CSV

```bash
hdfs dfs -mkdir -p /data/people_hdfs

echo -e "id,first_name,last_name,city\n1,Ada,Lovelace,London\n2,Grace,Hopper,New York" > people.csv

hdfs dfs -put people.csv /data/people_hdfs/
```

* Vérifier les données

```bash
hdfs dfs -cat /data/people_hdfs/people.csv
```

## Partie Nifi

* Lancer `Apache Nifi` dans la VM

```bash
/opt/nifi/bin/nifi.sh start
```

> `restart` si déjà lancé

* Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur

* Ajouter les processeurs suivants :

### 1. Ajouter un processeur `ListHDFS`

* Paramétrer :

  * `Directory` = /data/people_hdfs
  * `Recurse Subdirectories` = true
  * `Minimum File Age` = 0 sec

### 2. Ajouter un processeur `FetchHDFS`

* Aucun paramétrage spécial nécessaire, il récupère automatiquement les fichiers listés.

### 3. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Directory` = /user/hive/warehouse/ext_people_hdfs
  * `Conflict Resolution Strategy` = replace
  * `Compression Codec` = NONE

* Relier les processeurs : `ListHDFS` -> `FetchHDFS` -> `PutHDFS`

* Configurer tous les processeurs pour qu'ils soient `valid`

  * Dans `Relationships`, choisir `terminate` pour `failure`

* Click droit sur le canvas puis `Start`
> Vous pouvez `disable` les autres processeurs du canvas pour eviter qu'ils se lancent avec ce flow.

## Retour dans la VM - Vérification

* Lister si le dossier `/user/hive/warehouse/ext_people_hdfs` a été créé par Nifi

```
hdfs dfs -ls /user/hive/warehouse/ext_people_hdfs
```

* Lire le contenu du fichier

```
hdfs dfs -cat /user/hive/warehouse/ext_people_hdfs/people.csv
```

## PARTIE TENA IZY

### <span style="color: red">!!! Mbola tsy vitaaaa !!!</span>

* Démarrer Beeline

* Créer une table **externe** Hive

```sql
CREATE EXTERNAL TABLE ext_people_hdfs (
    id INT,
    first_name STRING,
    last_name STRING,
    city STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/hive/warehouse/ext_people_hdfs/';
```

* Vérifier les données

```sql
SELECT * FROM ext_people_hdfs;
```

Vous devriez voir :

| id | first\_name | last\_name | city     |
| -- | ----------- | ---------- | -------- |
| 1  | Ada         | Lovelace   | London   |
| 2  | Grace       | Hopper     | New York |

Toutes mes Félicitations pour HDFS -> Nifi -> Hive (Table Externe) !
