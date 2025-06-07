# Fichiers partager pour TP-M

## Utilisation
- `analyse/` pour le script python et les requetes utiliser pour effectuer l'analyse des donnees
- `connexions/` pour les connexions entres composants
- `installs/` pour les installations des composants
- `datasets/` pour les données en formats .csv
- `scripts/` pour les programmes java utiliser pour injecter les sources de données.

---

- Naviguez directement depuis github dans le repertoire du composant que vous souhaitez installer.

> exemple : pour Apache Nifi, naviguer dans `installs/Apache Nifi` 
 
- Le contenu d'un fichier `README.md` contenant les instructions va s'afficher, deja bien formatter, pour faciliter la lecture.
- dans `connexions/` vous aurez un fichier `.xml`, en template à importer dans Nifi.

## Prerequis

* Installation vagrant VM (celle du cours)
* Configuration DNS dans la VM
```
sudo nano /etc/resolv.conf
```

* Rajoutter cette ligne
```
nameserver 8.8.8.8
```
