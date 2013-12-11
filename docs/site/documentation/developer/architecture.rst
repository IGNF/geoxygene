
Architecture
#######################


This page describes how GeOxygene Plateform is structured. The figure below shows how all sub-modules fit together.

.. container:: centerside
     
    .. figure:: /documentation/resources/img/architecture/ArchitectureGeoxygene.png
       
       Figure 1 : Architecture de GeOxygene

  
  
GeOxygene Plateform
***********************************

All modules are presented here, functionalities and concepts. 

  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-api        | définition des interfaces permettant de travailler avec de l’information spatiale    |
  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-spatial    | implémentation des principales classes géométriques et topologiques.                 |
  |                       |                                                                                      |
  |                       | Algorithmes spatiaux : géométriques, généralisations, index, …                       |
  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-feature    | implémentation des objets géographiques                                              |
  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-io         | chargement / export des données                                                      |
  |  geoxygene-database   |                                                                                      |
  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-style      |                                                                                      |
  +-----------------------+--------------------------------------------------------------------------------------+
  |  geoxygene-filter     |                                                                                      |
  +-----------------------+--------------------------------------------------------------------------------------+

  
  