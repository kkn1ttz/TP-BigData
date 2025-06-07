# Fichiers partager pour TP-M

## Utilisation
- `installs/` pour les installations des composants
- `connexions/` pour les connexions entres composants
- `datasets/` pour les données en formats .csv
- `analyse/` pour le script python et les requetes utiliser pour effectuer l'analyse des donnees

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
