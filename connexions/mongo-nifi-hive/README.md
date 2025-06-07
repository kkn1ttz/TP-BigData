# Instructions Connexion MongoDB vers HIVE via Nifi

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)
* [installs/MongoDB](https://github.com/kkn1ttz/TP-M/tree/master/installs/Mongo%20DB)
* [connexions/mysql-nifi-hive](https://github.com/kkn1ttz/TP-M/tree/master/connexions/mysql-nifi-hive)

## Utilisation template

* Si vous souhaitez importer directement le template du flow nifi :
  * Click droit sur le `canvas` dans l'interface nifi
  * Choisir `Upload Template`
  * Choisir le fichier `mongo-nifi-hive.xml`

* Les instructions pour reproduire le flow Nifi sont les suivantes.

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

```
sudo systemctl start mongod
```

* Entrer dans le shell `mongo`
```
mongo
```

* Création d'une base de données

```javascript
use accidentDB

exit
```

* Importer les données csv dans mongo

```
mongoimport --type csv --headerline --db accidentDB --collection accident_environmental_conditions --file /vagrant/installationn/mongoDonnee/Accident_Environmental_Conditions.csv
mongoimport --type csv --headerline --db accidentDB --collection accident_locations --file /vagrant/installationn/mongoDonnee/Accident_Locations.csv
```


## Partie Nifi

* Lancer `Apache Nifi` dans la VM

```bash
/opt/nifi/bin/nifi.sh start
```

> `restart` si déjà lancé

* Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur


* ajouter 2 processeurs group(pour chaque collections):


## Proccess group 1

* entrer dans le premier processeur group (pour la première collection):


* Ensuite, ajouter les processeurs suivants :

### 1. Ajouter un processeur `GetMongo`

* Paramétrer :

  * `Mongo URI` = mongodb://localhost:27017
  * `Database Name` = accidentDB
  * `Collection Name` = accident_environmental_conditions
  * `Batch Size` = 40000
  * `Query` = `{}`
  * `Projection` = (laisser vide)

### 2. Ajouter un processeur `MergeRecord`

* `Record Reader` = JsonTreeReader
  * Créer le service `JsonTreeReader`
  * Enable le service
* `Record Writer` = CSVRecordSetWriter
  * Le meme qu'on a utiliser pour `mysql-nifi-hive`
* `Merge Strategy` = Bin-Packing Algorithm
* `Maximum Number of Records` = 40000
*  `Max Bin Age` = 1 min

### 3. Ajouter un processeur `UpdateAttribute`

* `filename` = donnees


### 4. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Hadoop Configuration Resources` = /opt/nifi/conf/core-site.xml,/opt/nifi/conf/hdfs-site.xml
  * `Directory` = /user/hive/warehouse/accident_environmental_conditions
  * `Conflict Resolution Strategy` = replace

  * Relier les processeurs : `GetMongo` -> `MergeRecord` -> `UpdateAttribute` -> `PutHDFS`
    * relashionships `success` et `original`
    * Quand on relie `MergeRecord` -> `UpdateAttribute` : on sélectionne suleument merged

* Configurer `ConvertRecord` et `PutHDFS` pour qu'ils soient `valid`

  * Dans `Relationships`, choisir `terminate` pour `failure`

* Click droit sur le canvas puis `Start`
> Vous pouvez `disable` les autres processeurs du canvas pour eviter qu'ils se lancent avec ce flow.

## Retour dans la VM - Vérification

* Lister si le dossier `/user/hive/warehouse/accident_environmental_conditions` a été créé par Nifi

```
hdfs dfs -ls /user/hive/warehouse/accident_environmental_conditions
```

* Lire le contenu d'un fichier

```
hdfs dfs -cat /user/hive/warehouse/accident_environmental_conditions/donnees
```

#### Warning
##### Pour ne pas surcharger le flow Nifi.
- Configurer `Schedulling` du processor `GetMongo`
  - `Run schedule` = 59 min
  - C'est l'intervalle entre laquelle le processor GetMongo s'execute

## Proccess group 2

* entrer dans le premier processeur group (pour la deuxième collection):


* Ensuite, ajouter les processeurs suivants :

### 1. Ajouter un processeur `GetMongo`

* Paramétrer :

  * `Mongo URI` = mongodb://localhost:27017
  * `Database Name` = accidentDB
  * `Collection Name` = accident_locations
  * `Batch Size` = 40000
  * `Query` = `{}`
  * `Projection` = (laisser vide)

### 2. Ajouter un processeur `MergeRecord`

* `Record Reader` = JsonTreeReader
  * Créer le service `JsonTreeReader`
  * Enable le service
* `Record Writer` = CSVRecordSetWriter
  * Le meme qu'on a utiliser pour `mysql-nifi-hive`
* `Merge Strategy` = Bin-Packing Algorithm
* `Maximum Number of Records` = 40000
*  `Max Bin Age` = 1 min

### 3. Ajouter un processeur `UpdateAttribute`

* `filename` = donneesTwo


### 4. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Hadoop Configuration Resources` = /opt/nifi/conf/core-site.xml,/opt/nifi/conf/hdfs-site.xml
  * `Directory` = /user/hive/warehouse/accident_locations
  * `Conflict Resolution Strategy` = replace

  * Relier les processeurs : `GetMongo` -> `MergeRecord` -> `UpdateAttribute` -> `PutHDFS`
    * relashionships `success` et `original`
    * Quand on relie `MergeRecord` -> `UpdateAttribute` : on sélectionne suleument merged

* Configurer `ConvertRecord` et `PutHDFS` pour qu'ils soient `valid`

  * Dans `Relationships`, choisir `terminate` pour `failure`

* Click droit sur le canvas puis `Start`
> Vous pouvez `disable` les autres processeurs du canvas pour eviter qu'ils se lancent avec ce flow.

## Retour dans la VM - Vérification

* Lister si le dossier `/user/hive/warehouse/accident_locations` a été créé par Nifi

```
hdfs dfs -ls /user/hive/warehouse/accident_locations
```

* Lire le contenu d'un fichier

```
hdfs dfs -cat /user/hive/warehouse/accident_locations/donneesTwo
```

#### Warning
##### Pour ne pas surcharger le flow Nifi.
- Configurer `Schedulling` du processor `GetMongo`
  - `Run schedule` = 59 min
  - C'est l'intervalle entre laquelle le processor GetMongo s'execute

## PARTIE HIVE

* Démarrer Beeline

* Créer des tables **interne** Hive (pour chaque collection )

```sql
CREATE TABLE accident_environmental_conditions (
    `_id` STRING,
    OBJECTID INT,
    YEAR INT,
    MONTH INT,
    MONTHNAME STRING,
    DAY INT,
    DAYNAME STRING,
    HOUR INT,
    HOURNAME STRING,
    MINUTE INT,
    LGT_COND INT,
    LGT_CONDNAME STRING,
    WEATHER INT,
    WEATHERNAME STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;


CREATE TABLE accident_locations (
    `_id` STRING,
    OBJECTID INT,
    STATE INT,
    STATENAME STRING,
    COUNTY INT,
    COUNTYNAME STRING,
    CITY INT,
    CITYNAME STRING,
    LATITUDE DOUBLE,
    LONGITUD DOUBLE,
    x DOUBLE,
    y DOUBLE,
    RUR_URB INT,
    RUR_URBNAME STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```

* Charger les données depuis HDFS

```sql
LOAD DATA INPATH '/user/hive/warehouse/accident_environmental_conditions/donnees'
INTO TABLE accident_environmental_conditions;


LOAD DATA INPATH '/user/hive/warehouse/accident_locations/donneesTwo'
INTO TABLE accident_locations;
```

* Vérifier les données

```sql
SELECT * FROM accident_environmental_conditions;

SELECT * FROM accident_locations;
```

* Refaire l'operation pour chaque table
* Table maintenant accessible via python