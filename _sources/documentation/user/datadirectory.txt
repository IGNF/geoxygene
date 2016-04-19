.. _datadirectory:

Jeux de données
===================


Des échantillons des bases de données de l'IGN sont téléchargeables ici : 


.. container:: chemin
    
      **BD TOPO®** : 
          
          http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-data/ign-echantillon/bdtopo/72_BDTOPO_shp/echantillon-bdtopo-x062-ed111.zip
  
      **BD CARTO®** : 
         
          http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-data/ign-echantillon/bdcarto/echantillon-bdcarto-x072-ed111.zip


Dézipper les fichiers après leurs téléchargements sur votre disque dur.

C'est tout bon, vous avez 2 jeux de données pour vos tests.


.. 1. « Check out »
.. svn checkout http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-data/


.. 1. Mode développeur
..    => 2 accès : classpath ou système de fichiers
.. 2. Mode utilisateur
..    => système de fichiers
    
.. - GEOXYGENE-DATA-DIR
.. - DATA-DIR 


.. Tous les jeux de données (SHP, SLD, CSV, GML, ...) doivent être chargées via le système de fichier , surtout pas par le CLASSPATH.
.. Il y a la possibilité de passer par une configuration 


.. Creating a New Data Directory
.. --------------------------------

.. If GeoServer is running in Standalone mode the data directory is located at <installation root>/data_dir

.. On Windows systems the <installation root> is located at C:\Program Files\GeoServer <VERSION>.


.. Setting the Data Directory
.. ----------------------------



.. Structure of the Data Directory
.. -----------------------------------
.. The following figure shows the structure of the GeoServer data directory::

..   <data_directory>/
   
..      properties.xml  (null au depart puis lastDirectory + database)
..      plugins.xml
..      log4j.properties
      
..      dataset/
..      logs/
..      styles/
      
 