# Instructions Connection HBase vers HIVE via Nifi

## Prérequis

Avoir complété :

* [installs/Apache Nifi](https://github.com/kkn1ttz/TP-M/tree/master/installs/Apache%20Nifi)
* [installs/Hadoop HDFS](https://github.com/kkn1ttz/TP-M/tree/master/installs/Hadoop)
* [installs/HBase](https://github.com/kkn1ttz/TP-M/tree/master/installs/HBase)

## Mise en place initiale

* Copier les fichiers de configurations de HDFS dans Nifi

```
cp /opt/hbase/conf/hbase-site.xml /opt/nifi/conf/
```

* Copier les jar de hbase vers nifi

```
cp /opt/hbase/lib/hbase-client-2.5.11.jar /opt/nifi/lib/
cp /opt/hbase/lib/hbase-common-2.5.11.jar /opt/nifi/lib/
cp /opt/hbase/lib/hbase-server-2.5.11.jar /opt/nifi/lib/
cp /opt/hbase/lib/hbase-protocol-2.5.11.jar /opt/nifi/lib/
cp /opt/hbase/lib/hbase-mapreduce-2.5.11.jar /opt/nifi/lib/
cp /opt/hbase/lib/hbase-hadoop-compat-2.5.11.jar /opt/nifi/lib/
cp /usr/local/hadoop-3.3.6/share/hadoop/common/hadoop-common-3.3.6.jar /opt/nifi/lib/
cp /usr/local/hadoop-3.3.6/share/hadoop/hdfs/hadoop-hdfs-3.3.6.jar /opt/nifi/lib/
```

* Lancer HDFS
  
```bash
start-dfs.sh
```

* Lancer HBase

```bash
source ~/.bashrc
/opt/hbase/bin/start-hbase.sh
```

* Créer une table HBase de test

```bash
hbase shell
```

```hbase
create 'people_hbase', 'info'
put 'people_hbase', 'row1', 'info:first_name', 'Ada'
put 'people_hbase', 'row1', 'info:last_name', 'Lovelace'
put 'people_hbase', 'row1', 'info:city', 'London'
exit
```

## Partie Nifi

* Lancer `Apache Nifi` dans la VM

```bash
/opt/nifi/bin/nifi.sh start
```

> `restart` si déjà lancé

* Naviguer vers [http://localhost:8080/nifi](http://localhost:8080/nifi) sur votre navigateur

* Ajouter les processeurs suivants :

### 1. Ajouter un Controller Service `HBaseClientService`

* Aller dans `Controller Services`
* Ajouter un `HBase_2_ClientService`
* Configurer :

  * `HBase Configuration Files` = `/opt/hbase/conf/hbase-site.xml`
  * Activer le service

### 2. Ajouter un processeur `ScanHBase`

* Paramétrer :

  * `Table Name` = people_hbase
  * `Columns` = info:first_name,info:last_name,info:city
  * `JsonFormat` = col-qual-and-val
  * `Encoding Strategy` = None

### 3. Ajouter un processeur `ConvertRecord`

* `Record Reader` = AvroReader (déjà préconfiguré pour `ScanHBase`)
* `Record Writer` = CSVRecordSetWriter

  * Créer un `CSVRecordSetWriter` avec les séparateurs adaptés (`,`, `UTF-8`)

### 4. Ajouter un processeur `PutHDFS`

* Paramétrer :

  * `Directory` = /user/hive/warehouse/ext\_people\_hbase
  * `Conflict Resolution Strategy` = replace

### 5. Ajouter un simple processeur `GenerateFlowFile` avant ScanHbase

* Aucune configuration, c'est juste pour lancer le flow et valider `ScanHbase`

---

* Relier les processeurs : `GenerateFlowFile` -> `ScanHBase` -> `ConvertRecord` -> `PutHDFS`

* Configurer tous les processeurs pour qu'ils soient `valid`

  * Dans `Relationships`, choisir `terminate` pour `failure` et `original`

* Click droit sur le canvas puis `Start`

> Vous pouvez `disable` les autres processeurs du canvas pour éviter qu'ils se lancent avec ce flow.

## Retour dans la VM - Vérification

* Lister si le dossier `/user/hive/warehouse/ext_people_hbase` a été créé par Nifi

```bash
hdfs dfs -ls /user/hive/warehouse/ext_people_hbase
```

* Lire le contenu du fichier

```bash
hdfs dfs -cat /user/hive/warehouse/ext_people_hbase/nom-du-fichier
```

## This is the tandremo

#### Pour ne pas surcharger le flow Nifi.

* Configurer `Scheduling` du processor `GenerateFlowFile`

  * `Run schedule` = 59 min

## PARTIE TENA IZY

* Reproduire le scénario de test pour d'autres tables HBase

  * `ScanHBase`

    * `Table Name` = `<nom-table>`
  * `PutHDFS`

    * `Directory` = /user/warehouse/hive/`<nom-table>`

* Démarrer Beeline

```bash
beeline -u jdbc:hive2://localhost:10000
```

* Créer une table **externe** Hive

```sql
CREATE EXTERNAL TABLE <nom-table> (
    rowkey STRING,
    first_name STRING,
    last_name STRING,
    city STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/user/warehouse/hive/<nom-table>/';
```

* Vérifier les données

```sql
SELECT * FROM <nom-table>;
```

* Refaire l'opération pour chaque table
* Table maintenant accessible via python ou autre outil BI
