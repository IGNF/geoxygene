.. _motif:


:Author: Charlotte Hoarau
:Version: 0.1
:License: Create Commons with attribution
:Date: 30 Septembre 2013

Style : motifs & poncifs 
#########################
                                           
Introduction
*************

Il existe plusieurs façons de décrire un motif selon la norme SLD et de l'appliquer 
à un objet géographique dans GeOxygene qui implémente cette norme.
Cette page a pour objectif d'illustrer le plus exhaustivement possible les différents cas :

- avec différentes méthodes (répétition le long d'un contour ou distribution dans une forme),
- selon le mode d'implantation des objets (point, ligne, polygone),
- selon les différents formats de motifs (image png ou gif, image svg, forme SLD)


Méthodes de rendu
*******************

La classe GraphicFill
======================
La classe GraphicFill modélise la méthode de rendu qui consiste à remplir une forme par répétition régulière d'un motif.

Cela peut être réalisé de façon très classique à l'intérieur d'un polygone, mais également à l'intérieur d'un contour (de point, de ligne ou de polygone).

Il n'y a pas pour l'instant de paramètre d'agencement des motifs à l'intérieur de la forme (ni prévu par la norme ni implémenté dans GeOxygene). 
Cependant il est possible de remplir la forme par une image du motif afin de :

* gérer l'espacement entre motifs (en ajoutant plus de blanc autour du motif)
* créer un motif irégulier (en ajoutant plus d'un blanc d'un côté du motif)
* réaliser un motif en quinconce


Plus d'explication en images:

  +-----------------------+------------------------------------------------------------------------------------+-------------------------------------------------------------------------------------+
  |  Motif élémentaire    |   .. figure:: /documentation/resources/img/motif/circle.png                        |   .. figure:: /documentation/resources/img/motif/circles-green.png                  |
  |                       |      :width: 100px                                                                 |      :width: 100px                                                                  |
  +-----------------------+------------------------------------------------------------------------------------+-------------------------------------------------------------------------------------+
  | Rendu sur un polygone |   .. figure:: /documentation/resources/img/motif/Motif_GraphicFill_circle.png      |   .. figure:: /documentation/resources/img/motif/Motif_GraphicFill_circles_vert.png |
  |                       |      :width: 150px                                                                 |      :width: 150px                                                                  |
  |                       |                                                                                    |                                                                                     |
  +-----------------------+------------------------------------------------------------------------------------+-------------------------------------------------------------------------------------+

  .. literalinclude:: /documentation/resources/code_src/motif/GraphicFillGreen.xml
   		:language: xml

      
   


La classe GraphicStroke
========================
La classe GraphicStroke modélise la méthode de rendu qui consiste à répéter un motif le long d'un objet linéaire.

Cela peut donc être réalisé sur une ligne, mais également sur le contour d'un polygone ou d'un point.

.. Modes d'implantation
.. *********************

.. Point
.. ======

.. Ligne
.. ======

.. Polygone
.. =========


Implémentation dans GeOxygene
*******************************

.. container:: centerside
     
    .. figure:: /documentation/resources/img/motif/DC_GeOxygene_SE.png
       :width: 800px
       
       Figure 1 : Diagramme de classes des classes Symbolizer dans GeOxygene

Des exemples de constructions de tels styles sont disponibles dans la classe 
`SLDDemoApplication.java <http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/geoxygene-appli/src/main/java/fr/ign/cogit/geoxygene/appli/example/SLDDemoApplication.java>`_

Des fichiers SLD avec des exemples de styles sont également disponibles dans les ressources du module appli (dossier sld).

Un exemple de GraphicFill sur un LineSymbolizer :

.. literalinclude:: /documentation/resources/code_src/motif/GraphicFillLine.java
    :language: java


Un exemple de GraphicStroke sur un PolygonSymbolizer :

.. literalinclude:: /documentation/resources/code_src/motif/GraphicStrokePolygon.java
    :language: java



Exemples de rendus
*******************

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



Bibliographie
***************

* `OpenGIS Styled Layer Descriptor Profile of the Web Map Service Implementation Specification, Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC)
  <http://www.opengeospatial.org/standards/sld>`_

* `OpenGIS Symbology Encoding Implementation Specification , Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC) 
  <http://www.opengeospatial.org/standards/se>`_







