

Plugins GeOxygene for OpenJump
########################################


  
Introduction
************************

Cette version contient deux plugins, un plugin permettant d'utiliser l'appariement de réseaux implémentés dans GeOxygene
et un second qui permet de manipuler  la carte topologique (Création de la carte topologique (noeuds, arcs, faces), 
Recherche de faces circulaires dans une carte topologique, 
création d'une carte topologique pour cartographiée le degré des noeuds.

GeOxygene library is used by these plugins

  * Licence GPL :
  * Date de publication :
  * Version : 1.0

Installation
***********************

Set up OpenJump as a prerequisite.

1. Download the plugin 

.. container:: chemin

   http://sourceforge.net/projects/oxygene-project/files/GeOxygene%20Plugin%20for%20OpenJUMP/0.1/geoxygene-plugin-0.1.zip/download
     
2. Copy the whole folder contents into your <myOpenjumpFolder>\lib\ext

3. Start openjump.
 
4. You will now see a new item menu in "Extensions" called "GeOxygene" 


Examples
************************

Network matching
============================
* {TO USE DEMO DATA (EDGES.SHP)}

1. Download demo data :
   Network 1 : http://
   Network 2 : http://
2.
   
2. Export shapefile to postgreSQL database using OpenJUMP or shp2pgsql tools.
3. Start OpenJUMP.
4. Select menu "pg Routing-->shortest Path".
5. Give the connection string with the database containing the demo data edges.shp.
6. Click connect.
7. If postgreSQL is running and connection strings are correct you get the source and target column populated.
8. Select the target and "Find Route".
9. the demo shapefile and shortest path will be displayed on mapwindow.
10. Finish.


Reference
--------------
  
  * Mustière S., Devogele T., 2008, <<{{{http://www.informaworld.com/smpp/1673074808-66010030/content~db=all~content=a902412390}
  Matching networks with different levels of detail}}>>, GeoInformatica, Vol.12 n°4, pp 435-453

  * La carte topologique dans GeOxygene
  
  
  
  
Currently  for doing routing based on pgRouting you need to give connection string and select the source and 
target nodes. The plugin will send these parameters to postgis table and create a table containing the 
geometry of features which makes the shortest path.

* {Features implemented}
1. Interactive source and destination selection
2. Allowing to load a shapefile directly and do network data matching


