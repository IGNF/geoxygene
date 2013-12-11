:Author: Charlotte Hoarau
:Version: 0.1
:License: Create Commons with attribution
:Date: 30 Septembre 2013

Motifs & Poncifs dans GeOxygene
#############################################                                
                                           
Introduction
****************************

Il existe plusieurs façons de décrire un motif selon la norme SLD et de l'appliquer à un objet géographique dans GeOxygene qui implémente cette norme.

Cette page a pour objectif d'illustrer le plus exhaustivement possible les différents cas :

* avec différents méthodes (répétition le long d'un contour ou distribution dans une forme),
* selon le mode d'implantation des objets (point, ligne, polygone),
* selon les différents formats de motifs (image png ou gif, image svg, forme SLD)


Méthodes de rendu
****************************
La classe GraphicFill
=========================
La classe GraphicFill modélise la méthode de rendu qui consiste à remplir une forme par répétition régulière d'un motif.

Cela peut être réalisé de façon très classique à l'intérieur d'un polygone, mais également à l'intérieur d'un contour (de point, de ligne ou de polygone).

Il n'y a pas pour l'instant de paramètre d'agencement des motifs à l'intérieur de la forme (ni prévu par la norme ni implémenté dans GeOxygene). 
Mais il est possible de prévoir une image de remplissage adéquate pour :

* gérer l'espacement entre motif (en ajoutant plus de blanc autour du motif)
* créer un motif irégulier (en ajoutant plus d'un blanc d'un côté du motif)
* réaliser un motif en quinconce


Plus d'explication en images:

  +-----------------------+-------------------------------------------------------------------------------+--------------------------------------------------------------------------------+
  |  Motif élémentaire    |   .. figure:: /documentation/resources/img/motif/circle.png                   |   .. figure:: /documentation/resources/img/motif/circles.png                   |
  |                       |      :width: 100px                                                            |      :width: 100px                                                             |
  +-----------------------+-------------------------------------------------------------------------------+--------------------------------------------------------------------------------+
  | Rendu sur un polygone |   .. figure:: /documentation/resources/img/motif/Motif_GraphicFill_circle.png |   .. figure:: /documentation/resources/img/motif/Motif_GraphicFill_circles.png |
  |                       |      :width: 250px                                                            |      :width: 250px                                                             |
  +-----------------------+-------------------------------------------------------------------------------+--------------------------------------------------------------------------------+


La classe GraphicStroke
===========================
La classe GraphicStroke modélise la méthode de rendu qui consiste à répéter un motif le long d'un objet linéaire.

Cela peut donc être réalisé sur une ligne, mais également sur le contour d'un polygone ou d'un point.

Modes d'implantation
****************************

Point
=========

Ligne
======

Polygone
===========


Implémentation dans GeOxygene
***********************************

.. container:: centerside
     
    .. figure:: /documentation/resources/img/motif/Motifs_GeOxygene_UML.png
      



Exemples de rendus
****************************

  +-----------------------+--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  |                       | LineSymbolizer                                                                 | PolygonSymbolizer                                                                                                                                                   |
  +-----------------------+--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  |                       | Stroke                                                                         | Stroke                                                                           | Fill                                                                             |
  +-----------------------+--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  |  GraphicFill          |   .. figure:: /documentation/resources/img/motif/GraphicFill_Stroke_Line.png   | .. figure:: /documentation/resources/img/motif/GraphicFill_Stroke_Polygon.png    |   .. figure:: /documentation/resources/img/motif/GraphicFill_Fill_Polygon.png    |
  |                       |      :width: 200px                                                             |    :width: 200px                                                                 |      :width: 200px                                                               |
  +-----------------------+--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  | GraphicStroke         |   .. figure:: /documentation/resources/img/motif/GraphicStroke_Stroke_Line.png | .. figure:: /documentation/resources/img/motif/GraphicStroke_Stroke_Polygon.png  |                                                                                  |
  |                       |      :width: 200px                                                             |    :width: 200px                                                                 |                                                                                  |
  +-----------------------+--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+----------------------------------------------------------------------------------+



Des exemples de constructions de tels styles sont disponibles dans la classe 
[http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/geoxygene-appli/src/main/java/fr/ign/cogit/geoxygene/appli/example/SLDDemoApplication.java SLDDemoApplication.java].

Des fichiers SLD avec des exemples de styles sont également disponibles dans les ressources du module appli (dossier sld).

.. literalinclude:: /documentation/resources/code_src/SLDDemoApplication.java
        :language: java



Bibliographie
****************************
*OpenGIS Styled Layer Descriptor Profile of the Web Map Service Implementation Specification, Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC)
*OpenGIS Symbology Encoding Implementation Specification , Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC)






