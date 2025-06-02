### <span style="color: red">!!! Mbola tsy vitaaaa !!!</span>

# Instructions Connexion MongoDB vers HIVE via Nifi

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)
* [installs/MongoDB](https://github.com/kkn1ttz/TP-M/tree/master/installs/Mongo%20DB)
* [connexions/mysql-nifi-hive](https://github.com/kkn1ttz/TP-M/tree/master/connexions/mysql-nifi-hive)

## Mise en place initiale

* Lancer HDFS

```
start-dfs.sh
```

## Mise en place MongoDB

* Assurez-vous que MongoDB est démarré

```bash
sudo systemctl start mongod
```

* Création d'une base de données et d'une collection avec des données

```javascript
use testdb

db.people.insertMany([
  { first_name: "Alan", last_name: "Turing", city: "London" },
  { first_name: "Katherine", last_name: "Johnson", city: "West Virginia" }
])
```

## Ajout du Driver MongoDB dans Nifi

* Télécharger le driver MongoDB compatible avec Nifi 1.25.0 et MongoDB 4.4

```bash
wget https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.4.3/mongodb-driver-sync-4.4.3.jar
```

* Copier le fichier dans le dossier `lib` de Nifi

```bash
cp mongodb-driver-sync-4.4.3.jar /opt/nifi/lib/
```

## Partie Nifi

* Lancer `Apache Nifi` dans la VM

```bash
/opt/nifi/bin/nifi.sh start
```

> `restart` si déjà lancé

* Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur

* Créer les services `JsonTreeReader` et `CSVRecordSetWriter` pour la conversion JSON -> CSV

* Ensuite, ajouter les processeurs suivants :

### 1. Ajouter un processeur `GetMongo`

* Paramétrer :

  * `Mongo URI` = mongodb://localhost:27017
  * `Database Name` = testdb
  * `Collection Name` = people
  * `Batch Size` = 1000
  * `Query` = `{}`
  * `Projection` = (laisser vide)

### 2. Ajouter un processeur `ConvertRecord`

* `Record Reader` = JsonTreeReader
* `Record Writer` = CSVRecordSetWriter

Configurer :

* `JsonTreeReader` :

  * `Schema Access Strategy` = Infer Schema
* `CSVRecordSetWriter` :

  * `Schema Access Strategy` = Use Schema from Record
  * `Record Separator` = `\n`
  * `Include Header Line` = true

### 3. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Directory` = /user/hive/warehouse/ext_people_mongo
  * `Conflict Resolution Strategy` = replace

* Relier les processeurs : `GetMongo` -> `ConvertRecord` -> `PutHDFS`

* Configurer `ConvertRecord` et `PutHDFS` pour qu'ils soient `valid`

  * Dans `Relationships`, choisir `terminate` pour `failure`

* Click droit sur le canvas puis `Start`

## Retour dans la VM - Vérification

* Lister si le dossier `/user/hive/warehouse/ext_people_mongo` a été créé par Nifi

```
hdfs dfs -ls /user/hive/warehouse/ext_people_mongo
```

* Lire le contenu d'un fichier

```
hdfs dfs -cat /user/hive/warehouse/ext_people_mongo/nom-du-fichier
```

## PARTIE TENA IZY

* Démarrer Beeline

* Créer une table **interne** Hive

```sql
CREATE TABLE people_mongo (
    _id STRING,
    first_name STRING,
    last_name STRING,
    city STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```

* Charger les données depuis HDFS

```sql
LOAD DATA INPATH '/tmp/ext_people_mongo'
INTO TABLE people_mongo;
```

* Vérifier les données

```sql
SELECT * FROM people_mongo;
```

Vous devriez voir :

| \_id          | first\_name | last\_name | city          |
| ------------- | ----------- | ---------- | ------------- |
| ObjectId(...) | Alan        | Turing     | London        |
| ObjectId(...) | Katherine   | Johnson    | West Virginia |

Toutes mes Félicitations pour MongoDB -> Nifi -> Hive (Table Interne) !
