
Données OSM dans GeOxygene
#################################

Cette page documente le chargement de données OpenStreetMap dans GeOxygene implémenté dans le module **geoxygene-osm**.


Utilisation via le plugin OSM
************************************

La première entrée du menu OSM permet d'importer un fichier de données .osm. Ce format est dérivée de XML et 
contient les données OSM sous forme de triplets RDF. Parser ce fichier et convertir les informations est un processus 
long (plusieurs minutes pour de gros fichiers), le déroulement du chargement s'affiche dans la barre 
de progression ci-dessous. L'étape la plus longue est la lecture de l'ensemble des points du fichiers, 
puis le chargeur lit les lignes et les relations, et enfin convertit toute l'information parsée en objets CartAGen. 

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/OsmProgressBar.png
      :width: 330px
       
      Figure 1 : OSM progress bar in GeOxygene application


Ces objets sont affichés après chargement avec un style par défaut qui reproduit le style OpenStreetMap 
par défaut (voir exemple ci-dessous). Ce style par défaut n'est pas complet, et il est possible de le compléter 
en modifiant les fichiers sld présents dans le répertoire resources/sld.

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/Style_osm.png
      :width: 690px
       
      Figure 2 : OSM Style


Structure d'un fichier .osm 
*************************************
Le fichier commence par lister l'ensemble des points de la zone, notamment ceux qui constituent les objets 
linéaires et surfaciques (voir exemples ci-dessous). Les coordonnées sont données en **WGS84**.

.. literalinclude:: /documentation/resources/code_src/osm/example01.osm
      :language: xml

Ces nœuds peuvent également être "taggés" (on leur a ajouté des propriétés par un tag RDF) s'ils 
représentent un objet géographique ponctuel comme le repère géodésique ci-dessous. Ces tags correspondent 
la plupart du temps aux recommendations du projet OpenStreetMap que l'on peut retrouver sur ce 
`wiki <http://wiki.openstreetmap.org/wiki/Map_Features>`_. Dans cet exemple, on retrouve des tags de nature 
métadonnées comme "source" et d'autres de nature attributaire comme "ele" qui donne l'altitude du point.

.. literalinclude:: /documentation/resources/code_src/osm/example02.osm
      :language: xml

Une fois tous les points de la zone décrits, le fichier contient la liste de toutes les 
lignes avec leurs tags associés. Un polygone est ici une ligne dont le point final est identique 
au point initial. Les points sont listés par la identifiant unique (voir exemple ci-dessous). 
Les données géographiques ont en général un tag principal (dont la liste est disponible sur le wiki, 
voir plus haut), qui correspond au nom de la classe dans une modélisation relationnelle ou orientée-objet. 
Dans notre exemple c-dessous, le tag principal est "highway" (qui correspond aux routes) 
avec la valeur "track" : c'est une route de type piste.

.. literalinclude:: /documentation/resources/code_src/osm/example03.osm
      :language: xml

Enfin, le fichier contient la liste des relations, qui sont des relations sémantiques reliant deux (ou plus) 
objets (nœud ou ligne) du fichier. L'exemple ci-dessous montre une relation précisant que deux lignes forment 
le cours principal du cours d'eau appelé "Le Soussouéou Gave". Les autres cours d'eau portant ce nom sont 
alors considérés comme bras annexes du cours d'eau. Ces relations permettent aussi de définir des partages 
topologiques. Pour l'instant, les relations sont parsées et chargées, mais elles ne sont pas utilisées 
dans la construction des objets géographiques.

.. literalinclude:: /documentation/resources/code_src/osm/example04.osm
           :language: xml

Schéma de données de CartAGen
*************************************

Dans CartAGen, les données géographiques sont gérées de manière plus complexe que dans une utilisation 
simple de GeOxygene. Les bâtiments sont placés dans une classe de bâtiments qui implémente l'interface 
des bâtiments qui modélise l'ensemble des propriétés et relations classiques d'un bâtiment dans une BD 
géographique (par exemple, sa hauteur, sa nature et l'îlot auquel il appartient). Ainsi, cela autorise 
les traitements sur les données géographiques qui sont spécifiques aux bâtiments de prendre en paramètre 
cette interface, et toutes les implémentations possibles de cette interface vont pouvoir utiliser ce traitement.

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/Architecture_donnees_cartagen.png
      :width: 600px
       
      Figure 3 : Architecture de stockage des données dans CartAGen

Dans le cas d'OSM, une implémentation spécifique de toutes ces interfaces géographiques (voir figure ci-dessous) 
a été réalisée et les données OSM sont chargées dans ces classes implémentant les interfaces de CartAGen 
(OsmBuilding implémente IBuilding).

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/Cartagen_schema_gene_simplifie.png
      :width: 800px
       
      Figure 4 : Version simplifiée du schéma de données de généralisation de CartAGen


Le chargeur va s'occuper de ces classes de manière transparente et l'utilisateur aura une couche par classe nommée par le nom de la population associée à la classe (par exemple "buildings pour les objets qui implémentent IBuilding).

Principes du chargement
*****************************

Mapping OSM/CartAGen
--------------------------
Le schéma de données CartAGen est un schéma orienté-objets avec des classes géographiques prédéfinies, 
chacune possédant un type de géométrie, alors que le schéma OSM se contente de mettre des tags sur des points 
ou des lignes. Le passage de l'un à l'autre n'est donc pas direct. En effet, suivant ni les types de géométries, 
ni le tag principal ne permettent d'apparier directement à des classes : le tag highway peut décrire une route 
linéaire (à mettre dans la classe OsmRoadLine) ou une parking surfacique (à mettre dans la classe OsmRoadArea) 
suivant le type de géométrie taguée. Nous proposons donc pour réaliser cet appariement d'utiliser un objet 
Mapping composant d'une liste d'appariement (objets de la classe OsmMatching) dont le diagramme de classe 
UML est présenté ci-dessous. Un "matching" énumère les combinaisons de tag permettant de peupler une classe du schéma CartAGen.

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/MappingOsmCartagen.png
      :width: 670px
       
      Figure 5 

Un mapping par défaut est fourni dans le module mais il ne gère pas encore tous les types 
d'objets présents dans OSM et doit donc être complété au besoin. Un extrait ci-dessous montre 
comment on décrit l'appariement pour la classe WaterArea.

.. literalinclude:: /documentation/resources/code_src/osm/OsmMatching.java
           :language: java


Factory d'objets Cartagen "OSM"
------------------------------------

Chaque implémentation du schéma de données CartAGen est muni d'une factory (au sens des design patterns) qui permet de créer les objets géographiques de manière générique. On ne fait pas new OsmRoadLine(line) mais getSchemaFactory().createRoadLine(line) en récupérant la factory de l'implémentation courante, stockée sur le plugin CartAGen. Si on charge des données OSM, la factory courante du plugin sera bien celle d'OSM qui créera de manière transparente un objet de la classe OsmRoadLine.
Dans le cas du chargement OSM, la factory est dotée d'une méthode createGeneObj(...) qui est capable d'appeler la bonne méthode de création à partir des informations parsées (les nœuds et les tags OSM) et des informations du mapping (le nom de la classe CartAGen et le type de géométrie).

.. literalinclude:: /documentation/resources/code_src/osm/OsmGeneObj.java
           :language: java


Conservation des tags OSM
-----------------------------------
Si certains tags sont utilisés pour remplir des attributs des objets Java, tous sont conservés dans une map 
définie sur la classe OsmGeneObj, ce qui permet de les interroger. L'appel à la méthode générique des 
IFeature getAttribute(String name) va chercher les valeurs dans ces tags pour les objets OSM contrairement 
au fonctionnement générique de la méthode.

De plus, il est possible de visualiser tous les tags des objets sélectionnés en cliquant sur l'entrée "Browse tags" 
du menu ajouté par le plugin OSM.

.. container:: centerside
     
   .. figure:: /documentation/resources/img/osm/OsmTagBrowser.png
      :width: 650px
       
      Figure 6 : OSM Tag Browser


Attention, pour l'instant, le chargeur convertit les coordonnées en Lambert93 et ne projette donc correctement 
que des données situées en France métropolitaine. Une version améliorée du chargeur gérant plusieurs projections 
est prévue car des données OSM sont disponibles partout dans la monde.




