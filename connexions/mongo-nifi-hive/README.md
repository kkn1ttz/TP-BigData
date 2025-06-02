# Instructions Connexion MongoDB vers HIVE via Nifi

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)
* [installs/MongoDB](https://github.com/kkn1ttz/TP-M/tree/master/installs/Mongo%20DB)
* [connexions/mysql-nifi-hive](https://github.com/kkn1ttz/TP-M/tree/master/connexions/mysql-nifi-hive)

## Mise en place initiale


* Télécharger et deplacer les drivers MongoDB pour nifi

```bash
cd /tmp
wget https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.4.1/mongodb-driver-sync-4.4.1.jar -O mongodb-driver-sync-4.4.1.jar
wget https://repo1.maven.org/maven2/org/mongodb/bson/4.4.1/bson-4.4.1.jar -O bson-4.4.1.jar
wget https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.4.1/mongodb-driver-core-4.4.1.jar -O mongodb-driver-core-4.4.1.jar
sudo mv mongodb-driver-sync-4.4.1.jar /opt/nifi/lib/
sudo mv bson-4.4.1.jar /opt/nifi/lib/
sudo mv mongodb-driver-core-4.4.1.jar /opt/nifi/lib/
cd ~
```

* Lancer HDFS

```
start-dfs.sh
```

## Mise en place MongoDB

* Assurez-vous que MongoDB est démarré

```bash
sudo systemctl start mongod
```


* Entrer dans le shell `mongo`
```
mongo
```

* Création d'une base de données et d'une collection avec des données

```javascript
use testdb

db.people.insertMany([
  { first_name: "Alan", last_name: "Turing", city: "London" },
  { first_name: "Katherine", last_name: "Johnson", city: "West Virginia" }
])
```


## Partie Nifi

* Lancer `Apache Nifi` dans la VM

```bash
/opt/nifi/bin/nifi.sh start
```

> `restart` si déjà lancé

* Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur


* Ensuite, ajouter les processeurs suivants :

### 1. Ajouter un processeur `GetMongo`

* Paramétrer :

  * `Mongo URI` = mongodb://localhost:27017
  * `Database Name` = testdb
  * `Collection Name` = people
  * `Batch Size` = 1000
  * `Query` = `{}`
  * `Projection` = (laisser vide)

* Schedulling
  * `Run schedule` = 59 min
> Ne pas oublier sinon vous allez vite vous retrouvez avec des milliers de flowfiles en 1 sec sur nifi

### 2. Ajouter un processeur `ConvertRecord`

* `Record Reader` = JsonTreeReader
  * Créer le service `JsonTreeReader`
  * Enable le service
* `Record Writer` = CSVRecordSetWriter
  * Le meme qu'utiliser pour `mysql-nifi-hive`

### 3. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Directory` = /user/hive/warehouse/ext_people_mongo
  * `Conflict Resolution Strategy` = replace

* Relier les processeurs : `GetMongo` -> `ConvertRecord` -> `PutHDFS`
  * relashionships `success` et `original`

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
### <span style="color: red">!!! Mbola tsy vitaaaa !!!</span>


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
