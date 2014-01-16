

Feature
#########

GeOxygene propose un schéma logique générique pour l'exploitation des données. Il est basé sur le concept OGC d'objets géographiques
(FT_Feature) et sur les concepts de jeu de données (DataSet), de populations d'objets d'un même type (Population), de collections d'objets
quelconques (FT_FeatureCollection). La dimension spatiale des objets géographiques repose sur la large gamme de primitives géométriques 
et topologiques spécifiée oar la norme ISO 19107. 


                                         

  
.. .. container:: centerside
     
..    .. figure:: /documentation/resources/img/feature/DC-DefaultFeature.png
..       :width: 1000px
       
..       Figure 1 : Diagramme de classe Feature


.. container:: centerside

   .. figure:: /documentation/resources/img/feature/DC-FeatureType.png
      :width: 800px
       
       Figure 2 : Diagramme de classe Feature


Définitions
================

Feature
^^^^^^^^^^

Un *Feature* est un objet géographique. Définition d'un feature (ISO 19101) : 

A feature is an abstraction of a real world phenomenon; it is a geographic feature if it is associated with a location relative to the Earth. 

A feature is quite simply something that can be drawn on a map

Références OGC : 
* http://www.opengeospatial.org/standards/sfa - Simple Feature Access - Part 1: Common Architecture
* http://www.opengeospatial.org/standards/as - Topic 5 - Features

Par exemple : troncon_hydrographique, noeud_hydrographique, cours_d_eau, toponyme_d_hydrographie_surfacique, point_d_eau_isole, laisse


.. container:: centerside

   .. figure:: /documentation/resources/img/feature/TronconRoute.png
      :width: 360px
       
       Figure 2 : Feature *Tronçon de route*



FeatureType
^^^^^^^^^^^^^^
définit d'un modèle

FeatureType provides metadata model describing the represented information. This is considered “metadata” as it is a description of the information stored in the features.

FeatureType is used when:

    Accessing information as a description of the available attribute names when making a Expression
    Creating a new feature you can check to ensure your values are valid
 
 
Occasionally you have two features that have a lot in common. You may have the LAX airport in Los Angeles and the SYD airport in Sydney. 
Because these two features have a couple of things in common it is nice to group them together - in Java we would create a Class called Airport. 
On a map we will create a Feature Type called Airport.



.. literalinclude:: /documentation/resources/code_src/feature/FeatureType.xml
      :language: xml


FT_Feature
^^^^^^^^^^^^

 est la classe mère des classes géographiques. Des *FT_Feature* peuvent s'agréger en *FT_FeatureCollection*, 
classe qui représente donc un groupe de *FT_Feature* et qui porte des méthodes d'indexation spatiale.


4. Property et Attribute

Attributes of (either contained in or associated to) a feature describe measurable or describable properties about this entity.


5. FeatureCollection

6. Population

7. DataSet

8. *SchemaConceptuelJeu* : schéma conceptuel d'un jeu de données. Correspond à la notion "Application schema" dans les normes ISO, 
qui n'est pas définie par  un type de données formel. Nous définissons ici ce type comme un ensemble de classes et de 
relations (associations et héritage) comportant des proprietés (attributs, rôles, opérations) et des contraintes.

Attention dans GeoTools "schema" designe la structure d'un feature et non pas d'un jeu de données.

*SchemaDefaultFeature* : Description du schéma logique d'un DefaultFeature (table de SGBD). 
Ce schéma contient le nom de la table (ou du fichier GML ou autre...) et une lookup table indiquant le nom des attributs 
et leur emplacement dans la table attributes[] du defaultFeature. 

Dans le cas où une métadonnée de structure était disponible (soit stockée quelque part soit donnée par l'utilisateur lors du chargement), 
ce schéma contient aussi une référence vers le schéma conceptuel : le featureType correspondant au DefaultFeature.






AssociationType
^^^^^^^^^^^^^^^^^^
.. literalinclude:: /documentation/resources/code_src/feature/AssociationType.xml
      :language: xml


Module *geoxygene-feature*
==============================
Ce module contient l'implémentation des objets géographiques. 

Les classes dite géographiques héritent soit de « FT_Feature » (routes, rivières…) soit de « DefaultFeature »


.. container:: centerside

   .. figure:: /documentation/resources/img/feature/DC-Feature.png
      :width: 350px
       
       Figure 2 : Diagramme de classe Feature


Trucs et astuces
--------------------

Afficher des attributs 
^^^^^^^^^^^^^^^^^^^^^^^

Afficher un attribut :

.. literalinclude:: /documentation/resources/code_src/feature/AfficheAttribut.java
      :language: java

Afficher une liste d'attributs :

.. literalinclude:: /documentation/resources/code_src/feature/AfficheListeAttributs.java
      :language: java


Création d'une collection de features (pour un export en shapefile ou un affichage dans l'interface graphique)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. literalinclude:: /documentation/resources/code_src/feature/CreateCollection.java
      :language: java   


Ajout d'un attribut dans un DefaultFeature (sans cohérence globale au niveau du schéma de la collection)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
L'export de la collection est possible si les objets ont les mêmes attributs

.. literalinclude:: /documentation/resources/code_src/feature/AjoutAttribut.java
      :language: java 





