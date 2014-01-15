
Jeux de données
===================


1. Mode développeur
    => 2 accès : classpath ou système de fichiers

2. Mode utilisateur
    => système de fichiers
    

- GEOXYGENE-DATA-DIR
- DATA-DIR 


Tous les jeux de données (SHP, SLD, CSV, GML, ...) doivent être chargées via le système de fichier , surtout pas par le CLASSPATH.
Il y a la possibilité de passer par une configuration 


Creating a New Data Directory
--------------------------------

If GeoServer is running in Standalone mode the data directory is located at <installation root>/data_dir

On Windows systems the <installation root> is located at C:\Program Files\GeoServer <VERSION>.


Setting the Data Directory
----------------------------



Structure of the Data Directory
-----------------------------------
The following figure shows the structure of the GeoServer data directory::

   <data_directory>/
   
      properties.xml  (null au depart puis lastDirectory + database)
      plugins.xml
      log4j.properties
      
      dataset/
      logs/
      styles/
      
 