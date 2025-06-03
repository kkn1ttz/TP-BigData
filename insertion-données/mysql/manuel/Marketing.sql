create database concessionnaire_automobile;

USE concessionnaire_automobile;

CREATE TABLE `concessionnaire_automobile`.`Marketing` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `age` INT NOT NULL,
  `sexe` CHAR(1) NOT NULL,
  `taux` INT NOT NULL,
  `situationFamiliale` VARCHAR(25) NULL,
  `nbEnfantsAcharge` INT NOT NULL,
  `2emeVoiture` BOOLEAN NOT NULL,
  PRIMARY KEY (`id`));

----permettre l utilisation de l importation des donnees par fichier
SET GLOBAL local_infile=1;

LOAD DATA LOCAL INFILE '/vagrant/TpBigData/tpmysql/manuel/Marketing.csv' 
INTO TABLE Marketing 
CHARACTER SET latin1 -----mdefinir n encodage an ilay fichier
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS (`age`,`sexe`,`taux`,`situationFamiliale`,`nbEnfantsAcharge`,`2emeVoiture`); ----- ignorerna lay premier ligne satria nom an ilay colonne