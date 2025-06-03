# 📌 Insertion de fichiers CSV dans MongoDB avec Java

Ce guide explique comment insérer des fichiers CSV dans une base de données MongoDB à partir d’un programme Java dans un environnement Vagrant.

---

## ⚙️ Prérequis

1. Avoir une **machine virtuelle Vagrant** fonctionnelle.
2. Avoir **MongoDB** installé et en cours d’exécution (`mongod`).
3. Avoir **Java (JDK)** installé.
4. Avoir accès aux fichiers CSV à insérer.
5. Avoir téléchargé les bibliothèques suivantes dans le répertoire du projet :

   * [`mongodb-driver-3.4.3.jar`](https://repo1.maven.org/maven2/org/mongodb/mongodb-driver/3.4.3/)
   * `mongodb-driver-core-3.4.3.jar`
   * `bson-3.4.3.jar`
   * [`opencsv-5.7.1.jar`](https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/)
   * [`commons-lang3-3.12.0.jar`](https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/)

---

## 📁 Étapes détaillées

### 1. 📂 Copier les fichiers CSV vers la machine virtuelle

Sur votre machine hôte :

```bash
cp -r dataset/ /vagrant/
```

Dans la machine virtuelle Vagrant :

```bash
cd /vagrant/mongodb
```

---

### 2. 📅 Télécharger les dépendances Java

```bash
# MongoDB Driver 3.4.3
wget https://repo1.maven.org/maven2/org/mongodb/mongodb-driver/3.4.3/mongodb-driver-3.4.3.jar
wget https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/3.4.3/mongodb-driver-core-3.4.3.jar
wget https://repo1.maven.org/maven2/org/mongodb/bson/3.4.3/bson-3.4.3.jar

# OpenCSV et Commons Lang3
wget https://repo1.maven.org/maven2/com/opencsv/opencsv/5.7.1/opencsv-5.7.1.jar
wget https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar
```

---

### 3. 🛠️ Compiler le code Java

```bash
javac -cp ".:mongodb-driver-3.4.3.jar:mongodb-driver-core-3.4.3.jar:bson-3.4.3.jar:opencsv-5.7.1.jar:commons-lang3-3.12.0.jar" MongoDBInsertFromCSV.java
```

---

### 4. ▶️ Exécuter le programme

```bash
java -cp ".:mongodb-driver-3.4.3.jar:mongodb-driver-core-3.4.3.jar:bson-3.4.3.jar:opencsv-5.7.1.jar:commons-lang3-3.12.0.jar" MongoDBInsertFromCSV
```

Si tout se passe bien, vous verrez :

```
✅ Connexion réussie à MongoDB
✅ Données insérées dans accidentDetails
✅ Données insérées dans locationDetails
```

---

### 5. 🔍 Vérification via la CLI MongoDB

```bash
mongo
use testdb
show collections
db.accidentDetails.find().pretty()
db.locationDetails.find().pretty()
exit
```

---

## 📄 Exemple de structure de fichiers CSV

### `accident_details.csv`

```
OBJECTID,YEAR,MONTH,MONTHNAME,DAY,DAYNAME,HOUR,HOURNAME,MINUTE,LGT_COND,LGT_CONDNAME,WEATHER,WEATHERNAME
1,2022,1,January,1,1,12,"12:00pm-12:59pm",30,1,Daylight,1,Clear
```

### `location_details.csv`

```
OBJECTID,STATE,STATENAME,COUNTY,COUNTYNAME,CITY,CITYNAME,LATITUDE,LONGITUD,x,y,RUR_URB,RUR_URBNAME
1,1,Alabama,107,"PICKENS (107)",0,"NOT APPLICABLE",33.49096667,-88.27408333,-9826626.0065,3960654.377,1,Rural
```

---

## ✅ Résultat attendu

Deux collections sont créées dans MongoDB :

* `accidentDetails`
* `locationDetails`

Chaque collection contient les données correspondantes de son fichier CSV.

---

## 📚 Fichier Java principal : `MongoDBInsertFromCSV.java`

Assurez-vous que le code est bien structuré avec des fonctions séparées pour une meilleure lisibilité. Exemple simplifié :

```java
public class MongoDBInsertFromCSV {
    public static void main(String[] args) {
        MongoDatabase database = connectToMongoDB("localhost", 27017, "testdb");
        insertCSVToCollection(database, "accidentDetails", "dataset/accident_details.csv");
        insertCSVToCollection(database, "locationDetails", "dataset/location_details.csv");
    }

    static MongoDatabase connectToMongoDB(String host, int port, String dbName) {
        // Connexion MongoDB
    }

    static void insertCSVToCollection(MongoDatabase db, String collectionName, String csvFilePath) {
        // Lecture CSV + insertion
    }
}
```

---