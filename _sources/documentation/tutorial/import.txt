.. _import:


:Version: 0.1
:License: Create Commons with attribution
:Date: 11 août 2015

Import et export de données dans GeOxygene
###########################################
                                           
Les fonctions d'import et d'export de données dans GeOxygene sont centralisées dans les modules *geoxygene-io* et *geoxygene-database*.

Importer des données depuis un fichier
****************************************

ShapefileReader
===================

La classe ShapefileReader permet de lire des shapefiles et de créer une population de DefautFeatures. Le schéma et le FeatureType associés sont créés au passage. 

Il existe deux principales possibilités pour l'utiliser :

* de façon *asynchrone*. Pour ce, il faut créer un objet ShapefileReader et exécuter la méthode read dessus. Cela lance un nouveau processus qui lit les features et ajoute les objets à la population au fur et à mesure.

* de façon *synchrone*. Pour ce, il faut utiliser une des méthodes statiques : read ou chooseAndReadShapefile.

.. code-block:: java

   IPopulation<IFeature> reseauRoutier = ShapefileReader.read("D:\\DATA\\bdtopo_routier.shp");


GPSTextfileReader
===================

A partir d'un fichier texte dont les 4 colonnes sont séparées par une tabulation :

.. code-block:: bash

   Date (UTC)   Time (UTC)  Latitude        Longitude       
   17-Jan-2009  20:27:37    47.66748333     -122.1070833        

on peut importer les données dans une population GeOxygene :

.. code-block:: java

   Population<DefaultFeature> gpsPop = GPSTextfileReader.read("D:\\data\\gps\\Seattle\\gps_data.txt");


Importer des données depuis une base de données
************************************************ 

Connection Postgis via JDBC
============================ 

Dans le module *geoxygene-database*, la classe PostgisReader permet de créer une population de DefaultFeature en spécifiant un nom de table :

.. code-block:: java

   Map<String,String> params = new HashMap<String,String>();
   params.put("dbtype", "postgis");
   params.put("host", "localhost");
   params.put("port", "5433");
   params.put("database", "bduni");
   params.put("schema", "public");
   params.put("user", "test");
   params.put("passwd", "test");

   IPopulation<IFeature> popReseauRoutier = PostgisReader.read(params, "troncon_de_route", "Réseau routier", null, false);



Il est possible d'ajouter un filtre (clause where) à la requête. Pour cela il faut notifier l'expression de filtre avec la norme CQL et la spécifier à la méthode _read(...)_ :

.. code-block:: java

   IPopulation<IFeature> popDimSpatiale = PostgisReader.read(params, NOM_TABLE, NOM_TABLE, null, false, null, "taille is null");


Connection persistante avec Hibernate
======================================= 
*Plus tard*


Exporter des données dans un shapefile
*****************************************

Pour exporter une population de feature dans un shapefile :

.. code-block:: java

   CoordinateReferenceSystem crs = CRS.decode("EPSG:3035");
   ShapefileWriter.write(reseau1.getPopArcs(), "D:\\Data\\troncon.shp", crs);





