
Architecture
#######################


This page describes how GeOxygene Plateform is structured. The figure below shows how all sub-modules fit together.

.. container:: centerside
     
    .. figure:: /documentation/resources/img/architecture/ArchitectureGeoxygene.png
       :width: 700px
       
       Figure 1 : GeOxygene Architecture

  
  
GeOxygene Plateform
***********************************

All modules are presented here, functionalities and concepts. 

  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-api        | Définition des interfaces permettant de travailler avec de l’information spatiale    |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-spatial    | Implémentation des principales classes géométriques et topologiques.                 |
  |                |                       |                                                                                      |
  |                |                       | Algorithmes spatiaux : géométriques, généralisations, index, …                       |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-feature    | Implémentation des objets géographiques                                              |
  + Noyau          +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-io         |                                                                                      |
  |                |                       | Chargement et export des données                                                     |
  |                |  geoxygene-database   |                                                                                      |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-style      | Le package style permet de décrire des styles cartographiques dans le formalisme     |
  |                |                       |                                                                                      |
  |                |                       | des normes OGC SLD (Styled Layer Descriptor) et SE (Symbology Encoding).             |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-filter     |  Le package filter permet de filtrer pour produire un nouveau jeu de résultats       |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-schemageo  | Schéma géographique des objets                                                       |
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-semio      | Module dédié aux travaux autour de la légende d'une carte                            |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-contrib    | Module dédié à l'appariement, la conflation, la qualité des données                  |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  | Contributions  |  geoxygene-cartagen   | Module dédié à la généralisation de données géographiques                            |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-osm        | Module dédié aux manipulations des données OSM                                       |
  +                +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-sig3d      | Module dédié à la visualisation et à la manipulation de données 3D                   |
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  | Viewer         |  geoxygene-appli      | Application graphique 2D                                                             |
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-ojplugin   | Plugins GeOxygene pour OpenJump : appariement de réseaux, indicateur de qualité.     |
  + Extensions     +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-wps        | Web services pour GeoServer : appariement de réseaux, réseaux topologiques           |
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-geopensim  | http://geopensim.ign.fr                                                              |
  + Applications   +-----------------------+--------------------------------------------------------------------------------------+
  |                |  geoxygene-pearep     |                                                                                      |
  +----------------+-----------------------+--------------------------------------------------------------------------------------+
   