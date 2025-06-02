# Analyse et Répartition du Dataset FARS 2022 - Accidents

Source:  https://geodata.bts.gov/datasets/usdot::fatality-analysis-reporting-system-fars-2022-accidents/explore?location=38.615244%2C-112.477350%2C3.77&showTable=true


---

## 1. Description Générale 

Le dataset FARS 2022 contient des informations sur les accidents mortels aux États-Unis en 2022, compilées par le Bureau of Transportation Statistics (BTS). Il inclut des détails sur les circonstances des accidents, les véhicules impliqués, les conditions environnementales, et les localisations géographiques.


* **Nombre total d'enregistrements** : 39 681 lignes, représentant 39 681 accidents mortels distincts.
* **Nombre total de colonnes** : 60 colonnes
* **Source** : US DOT - FARS 2022 Accidents (via l'API ArcGIS).

---

Ce dataset a été divisé en 7 datasets. 
Pourquoi?

* Performance :

Requêtes plus rapides car Au lieu de scanner 60 colonnes pour 39 681 lignes à chaque fois, les requêtes se concentrent sur des tables plus petites et ciblées. Si vous cherchez juste la localisation, vous n'interrogez que la table Accident_Locations.

* Facilitation de l'Analyse Spécifique :
Chaque table regroupe des informations cohérentes thématiquement. C'est plus facile pour les analystes de se concentrer sur un aspect précis (ex: juste les conditions météo, ou juste les caractéristiques routières) sans être noyés par toutes les autres colonnes.

* Flexibilité et Scalabilité :

- On peut faire évoluer chaque partie indépendamment.
- Gestion de la Croissance : Si arrive de grandes volumes de nouveaux données, gérer cela, sans impacter la performance des autres types de données.

* Simplicité de la Gestion du Schéma

cela simplifie les modifications futures. Si une nouvelle information liée aux conditions environnementales doit être ajoutée, elle n'impacte que la table Accident_Environmental_Conditions et non une table massive de 60 colonnes.

* Les données sont plus faciles à comprendres car ils sont séparés en différents datasets reqroupés par leurs logiques.

---
Vous trouverez dans le dossier "datasets" la répartition du dataset dans les différents bases en csv.


## 2. Explication Détaillée des 7 datasets

Le dataset FARS 2022 contient 60 colonnes, organisées en plusieurs catégories clés pour une compréhension complète des accidents.


## 1. `Accidents_Core_Facts`

Ce dataset  contient les informations de base sur chaque accident mortel, y compris les décomptes et les indicateurs primaires.

* **`OBJECTID`** : Identifiant unique de l'accident (clé primaire).
* **`ST_CASE`** : Numéro de cas unique par État.
* **`FATALS`** : Nombre de personnes décédées dans l'accident.
* **`PERSONS`** : Nombre total de personnes impliquées.
* **`VE_TOTAL`** : Nombre total de véhicules impliqués.
* **`VE_FORMS`** : Nombre de formulaires de véhicules renseignés.
* **`PEDS`** : Nombre de piétons impliqués.
* **`PERNOTMVIT`** : Nombre de personnes non-motorisées impliquées (cyclistes, etc.).
* **`SCH_BUS`** : Indicateur d'implication d'un bus scolaire (0 = Non, 1 = Oui).
* **`RAIL`** : Indicateur de passage à niveau ferroviaire (0000000 = Non).
* **`WRK_ZONE`** : Code de l'indicateur de zone de travaux routiers (clé étrangère vers `Ref_Work_Zone_Status`).

---

## 2. `Accident_Event_Details`

Ce dataset détaille les circonstances et les types d'événements spécifiques à chaque accident.

* **`OBJECTID`** : Identifiant unique de l'accident (clé étrangère, lié à `Accidents_Core_Facts`).
* **`HARM_EV`** : Code de l'événement principal ayant causé le décès.
* **`HARM_EVNAME`** : Nom de l'événement principal ayant causé le décès.
* **`MAN_COLL`** : Code du type de collision.
* **`MAN_COLLNAME`** : Nom du type de collision.
* **`TYP_INT`** : Code du type d'intersection.
* **`TYP_INTNAME`** : Nom du type d'intersection.
* **`REL_ROAD`** : Code de la position relative par rapport à la route.
* **`REL_ROADNAME`** : Nom de la position relative par rapport à la route.

---

## 3. `Ref_Work_Zone_Status`

Petite table de référence pour les statuts des zones de travaux routiers.

* **`WRK_ZONE`** : Code unique de l'indicateur de zone de travaux (clé primaire).
* **`WRK_ZONENAME`** : Nom lisible de l'indicateur de zone de travaux (ex: "Not Applicable").

---

## 4. `Accident_Locations`

Ce dataset regroupe toutes les informations géographiques et administratives de l'emplacement de l'accident.

* **`OBJECTID`** : Identifiant unique de l'accident (clé étrangère, lié à `Accidents_Core_Facts`).
* **`STATE`** : Code numérique de l'État.
* **`STATENAME`** : Nom de l'État.
* **`COUNTY`** : Code numérique du comté.
* **`COUNTYNAME`** : Nom du comté.
* **`CITY`** : Code numérique de la ville.
* **`CITYNAME`** : Nom de la ville.
* **`LATITUDE`** : Coordonnée latitudinale de l'accident.
* **`LONGITUD`** : Coordonnée longitudinale de l'accident.
* **`x`** : Coordonnée projetée en X.
* **`y`** : Coordonnée projetée en Y.
* **`RUR_URB`** : Code du type de zone (1 = Rural, 2 = Urbain).
* **`RUR_URBNAME`** : Nom du type de zone (Rural/Urbain).

---

## 5. `Accident_Environmental_Conditions`

Ce dataset contient les données relatives à la date, à l'heure et aux conditions environnementales au moment de l'accident.

* **`OBJECTID`** : Identifiant unique de l'accident (clé étrangère, lié à `Accidents_Core_Facts`).
* **`YEAR`** : Année de l'accident (2022).
* **`MONTH`** : Mois de l'accident (numérique).
* **`MONTHNAME`** : Nom du mois.
* **`DAY`** : Jour du mois.
* **`DAYNAME`** : Nom du jour de la semaine.
* **`HOUR`** : Heure de l'accident.
* **`HOURNAME`** : Plage horaire de l'accident.
* **`MINUTE`** : Minute exacte de l'accident.
* **`LGT_COND`** : Code des conditions d'éclairage.
* **`LGT_CONDNAME`** : Nom des conditions d'éclairage.
* **`WEATHER`** : Code des conditions météorologiques.
* **`WEATHERNAME`** : Nom des conditions météorologiques.

---

## 6. `Accident_Road_Characteristics`

Ce dataset spécifie les caractéristiques de l'infrastructure routière impliquée dans l'accident.

* **`OBJECTID`** : Identifiant unique de l'accident (clé étrangère, lié à `Accidents_Core_Facts`).
* **`TWAY_ID`** : Nom de la première route impliquée.
* **`TWAY_ID2`** : Nom de la deuxième route impliquée (si applicable).
* **`ROUTE`** : Code du type de route.
* **`ROUTENAME`** : Nom du type de route.
* **`FUNC_SYS`** : Code de la classification fonctionnelle de la route.
* **`FUNC_SYSNAME`** : Nom de la classification fonctionnelle.
* **`RD_OWNER`** : Code du gestionnaire de la route.
* **`RD_OWNERNAME`** : Nom du gestionnaire de la route.
* **`NHS`** : Indicateur si la route fait partie du National Highway System.
* **`NHSNAME`** : Nom de l'indicateur NHS.

---

## 7. `Accident_Response_Times`

Ce dataset contient les informations relatives aux temps de réponse des services d'urgence et d'arrivée à l'hôpital.

* **`OBJECTID`** : Identifiant unique de l'accident (clé étrangère, lié à `Accidents_Core_Facts`).
* **`ARR_HOUR`** : Heure d'arrivée des secours.
* **`ARR_MIN`** : Minute d'arrivée des secours.
* **`HOSP_HR`** : Heure d'arrivée à l'hôpital.
* **`HOSP_MN`** : Minute d'arrivée à l'hôpital.