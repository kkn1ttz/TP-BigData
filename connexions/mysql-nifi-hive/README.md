# Instructions Connection MySQL vers HIVE via Nifi 

## Prerequis
Avoir completer :
* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)

## Mise en place intiale
- Telecharger et placer le driver `mysql` pour nifi

```
cd /opt/nifi
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
mv mysql-connector-j-8.3.0.jar lib/
```

- Copier les fichiers de configurations de HDFS dans Nifi
```
cp /usr/local/hadoop-3.3.6/etc/hadoop/core-site.xml /opt/nifi/conf
cp /usr/local/hadoop-3.3.6/etc/hadoop/hdfs-site.xml /opt/nifi/conf
```

### Creer un repertoire pour acceuillir le resultat de Nifi dans Hive
- Lancer HDFS
```
start-dfs.sh
```

- Creer un repertoire dans HDFS et configurer les permissions
```
hdfs dfs -mkdir -p /user/hive/warehouse
hdfs dfs -chmod 1777 /user/hive/warehouse
```

## Mise en place MySQL
- Copier manuellement Accident_Event_Details.csv , Accidents_Core_Facts.csv , Ref_Work_Zone_Status.csv qui est dans datasets dans le dossier `TpBigData/mysql`du vagrant 
- Dans la VM , entrer dans la console `MySQL`
```
sudo mysql
```

#### Dans la console `mysql>`
- Creation d'un utilisateur pour nifi
```
CREATE USER 'nifi'@'localhost' IDENTIFIED BY 'nifiPW';
GRANT ALL ON nifi.* TO 'nifi'@'localhost';
FLUSH PRIVILEGES;
```

- Creation d'une base de données de test
``` 
CREATE DATABASE nifi;
USE nifi;
``` 

- Creation des tables 
```
CREATE TABLE Ref_Work_Zone_Status (
    WRK_ZONE INT PRIMARY KEY,
    WRK_ZONENAME VARCHAR(225) NOT NULL
);

CREATE TABLE Accidents_Core_Facts (
    OBJECTID INT PRIMARY KEY,
    ST_CASE INT NOT NULL,
    FATALS INT,
    PERSONS INT,
    VE_TOTAL INT,
    VE_FORMS INT,
    PEDS INT,
    PERNOTMVIT INT,
    SCH_BUS INT,         
    RAIL VARCHAR(20),            
    WRK_ZONE INT,
    FOREIGN KEY (WRK_ZONE) REFERENCES Ref_Work_Zone_Status(WRK_ZONE)
);

CREATE TABLE Accident_Event_Details (
    OBJECTID INT,
    HARM_EV INT,
    HARM_EVNAME VARCHAR(255),
    MAN_COLL INT,
    MAN_COLLNAME VARCHAR(255),
    TYP_INT INT,
    TYP_INTNAME VARCHAR(255),
    REL_ROAD INT,
    REL_ROADNAME VARCHAR(255),
    FOREIGN KEY (OBJECTID) REFERENCES Accidents_Core_Facts(OBJECTID)
);
```

- Insertion de donnees dans les tables
```

LOAD DATA LOCAL INFILE '/vagrant/TpBigData/mysql/Ref_Work_Zone_Status.csv'
INTO TABLE Ref_Work_Zone_Status
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(`WRK_ZONE`, `WRK_ZONENAME`);

LOAD DATA LOCAL INFILE '/vagrant/TpBigData/mysql/Accidents_Core_Facts.csv'
INTO TABLE Accidents_Core_Facts
CHARACTER SET latin1
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(`OBJECTID`, `ST_CASE`, `FATALS`, `PERSONS`, `VE_TOTAL`, `VE_FORMS`, `PEDS`, `PERNOTMVIT`, `SCH_BUS`, `RAIL`, `WRK_ZONE`);

LOAD DATA LOCAL INFILE '/vagrant/TpBigData/mysql/Accident_Event_Details.csv'
INTO TABLE Accident_Event_Details
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(`OBJECTID`, `HARM_EV`, `HARM_EVNAME`, `MAN_COLL`, `MAN_COLLNAME`, `TYP_INT`, `TYP_INTNAME`, `REL_ROAD`, `REL_ROADNAME`);
```

- Quitter la console `mysql>`
```
QUIT;
```

## Partie Nifi
- Lancer `Apache Nifi` dans la VM
```
/opt/nifi/bin/nifi.sh start
```
> `restart` si deja lancer

- Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur
- Click droit sur le `canvas` (grand espace blanc) et choisissez `configure`

- Allez dans l'onglet `Controller Services` et clicker sur l'icone `+`.

- Recherchez `dbcp` et choisissez `DBCPConnectionPool` et clicker sur `ADD`.
> Lorsque l'on parle de creer un service, reproduire cette action 

- Remplissez les `properties`

    - `Database Connection URL`=  jdbc:mysql://localhost:3306/nifi?serverTimezone=UTC&useSSL=false
    - `DB Driver Class Name`=  com.mysql.cj.jdbc.Driver
    - `Database User`=  nifi
    - `Password`=  nifiPW

- Clicker sur l'icone d'eclair pour `Enable` le service

- Revenir au `Canvas` et en haut à gauche clicker sur `processor` sans relacher le click `drag` vers le canvas 
> Lorsque l'on parle d'ajouter un processor, reproduire cette action 

- Ajouter un processor `QueryDatabaseTable`

- Configurer le processor
    - `DBCP Service` = le Controlleur Service creer precedement
    - `Table Name` = Ref_Work_Zone_Status
    - `Maximum-value Columns` = WRK_ZONE

- Ajouter un processor `ConvertRecord` pour convertir le output en CSV

- Configurer `ConverRecord`. Choisissez `Create new service` pour les proprietes `Record Reader` et `Record Writter`. Reproduire comme sur l'image en dessous 

- Puis `Enable` les deux services creer precedement dans Controller Service

- Rajouter un processor `PutHDFS` et configurer comme suit 
    - `Hadoop Configuration Resources` = /opt/nifi/conf/core-site.xml,/opt/nifi/conf/hdfs-site.xml
    - `Directory` = /user/hive/warehouse/ref_work_zone_status

- Relier les trois processeurs sur Success (le dernier processeur PutHDFS doit etre relier a lui meme)

- Configurer `ConvertRecord` et `PutHDFS` pour qu'ils soient `valid`. Aller dans `Relashionships` et choisissez `terminate` pour `failure` pour les deux.

- Click droit sur le canvas puis `Start`
> Vous pouvez `disable` les autres processeurs du canvas pour eviter qu'ils se lancent avec ce flow.

### Retour dans la VM - Verification

- Lister si le dossier `ref_work_zone_status` a ete crée par nifi
```
hdfs dfs -ls /user/hive/warehouse/ref_work_zone_status
```

- vous devrier voir une ligne comme ceci :
```
-rw-r--r--   1 vagrant supergroup        101 2025-06-01 15:47 /user/hive/warehouse/ref_work_zone_status/b7591f3c-3450-49e9-a031-ba7c7c7bb3cb
```

- prendre le nom du fichier dans `ref_work_zone_status` et lire son contenu
```
hdfs dfs -cat /user/hive/warehouse/ref_work_zone_status/b7591f3c-3450-49e9-a031-ba7c7c7bb3cb
```

- Si les donnes dans le fichier s'affiche,  c'est OK pour la partie MySQL -> Nifi -> Hive

- Reproduire le meme scenario pour l'importation des autres tables Accidents_Core_Facts et Accident_Event_Details

## Attention
#### Pour que le flow Nifi renvoie des données.
- Click droit sur le processor `QueryDatabaseTable` -> `View state` -> `Clear state`
- Le processor enregistre la derniere valeur maximum de la colonne `id` pour ne par renvoyer des lignes deja envoyées *(pas important dans notre cas)*.
- Ou juste inseré une nouvelle ligne dans `mysql>`.

## Importation des donnees dans Hive
* Démarrer Hive
```
nohup hive --service metastore > /dev/null &
nohup hiveserver2 > /dev/null &
```

* Démarrer Beeline
```
beeline
```


* Créer une table **interne** Hive (exemple, modifier en fonction du shema)

```sql
CREATE TABLE Ref_Work_Zone_Status (
    WRK_ZONE INT,
    WRK_ZONENAME STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

CREATE TABLE Accidents_Core_Facts (
    OBJECTID INT,
    ST_CASE INT,
    FATALS INT,
    PERSONS INT,
    VE_TOTAL INT,
    VE_FORMS INT,
    PEDS INT,
    PERNOTMVIT INT,
    SCH_BUS INT,
    RAIL STRING,
    WRK_ZONE INT
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

CREATE TABLE Accident_Event_Details (
    OBJECTID INT,
    HARM_EV INT,
    HARM_EVNAME STRING,
    MAN_COLL INT,
    MAN_COLLNAME STRING,
    TYP_INT INT,
    TYP_INTNAME STRING,
    REL_ROAD INT,
    REL_ROADNAME STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

```

* Charger les données depuis HDFS

```sql
LOAD DATA INPATH '/user/hive/warehouse/ref_work_zone_status.csv'
INTO TABLE Ref_Work_Zone_Status;

LOAD DATA LOCAL INPATH '/vagrant/tes_donnees/accidents_core_facts.csv'
INTO TABLE Accidents_Core_Facts;

LOAD DATA LOCAL INPATH '/vagrant/tes_donnees/accident_event_details.csv'
INTO TABLE Accident_Event_Details;
```
* Pour verifier que les tables creer sont bien des table interne a Hive
```sql
DESCRIBE FORMATTED <nom-table>;
```
> Dans la section table `Table Type`, si c'est une table interne , ca doit etre `MANAGED_TABLE`et si c'est une table externe , ca doit etre `EXTERNAL_TABLE`

* Vérifier que les données sont bien inserer

```sql
SELECT * FROM <nom-table>;

ou 

SELECT count(*) FROM <nom-table>;

```

* Les tables sont maintenant accessible via python