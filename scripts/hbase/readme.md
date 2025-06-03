# 🧩 HBase CSV Importer

Ce projet Java permet d'insérer automatiquement des données depuis un fichier CSV dans une table HBase nommée `Accident_Response_Times`.

---

## 📂 Structure attendue

```
/vagrant/
├── HBaseCSVImporter.java                ← Le fichier Java à compiler
├── datasets/
│   └── hbase/
│       └── Accident_Response_Times.csv  ← Le fichier CSV à insérer
```

---

## 📄 Exemple de contenu du fichier CSV

```
OBJECTID,ARR_HOUR,ARR_MIN,HOSP_HR,HOSP_MN
1,13,4,13,47
2,14,10,14,52
3,12,30,13,5
```

---

## ⚙️ Pré-requis

- Java installé sur votre machine
- HBase installé et fonctionnel
- Zookeeper actif (par défaut sur `localhost:2181`)
- La table `Accident_Response_Times` doit exister dans HBase avec une famille de colonnes `info`

---

## 🛠️ Étapes d’utilisation

### 1. Créer la table dans le shell HBase (si ce n’est pas encore fait)

```bash
hbase shell
```

```hbase
create 'Accident_Response_Times', 'info'
```

### 2. Compiler le programme Java

Depuis le répertoire contenant le fichier `HBaseCSVImporter.java`, exécutez :

```bash
javac -cp ".:/opt/hbase/lib/*:/opt/hadoop/share/hadoop/common/*:/opt/hadoop/share/hadoop/common/lib/*" HBaseCSVImporter.java
```

### 3. Exécuter le programme

```bash
java -cp ".:/opt/hbase/lib/*:/opt/hadoop/share/hadoop/common/*:/opt/hadoop/share/hadoop/common/lib/*" HBaseCSVImporter
```

---

## ✅ Résultat attendu

- Chaque ligne du fichier CSV est insérée dans HBase sous la table `Accident_Response_Times`
- Une ligne de confirmation s’affiche pour chaque insertion

---

## 🔍 Vérification des données dans HBase

Lancez le shell HBase :

```bash
hbase shell
```

Puis exécutez :

```hbase
scan 'Accident_Response_Times'
```

---

## 🧹 Réinitialisation de la table (facultatif)

Si besoin de supprimer et recréer la table proprement :

```hbase
disable 'Accident_Response_Times'
drop 'Accident_Response_Times'
create 'Accident_Response_Times', 'info'
```

---

## 🧑‍💻 Auteur

**Serge Ravelomahefa**  
*Databoost / Tradebridgeglobal*
