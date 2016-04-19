.. _contour:


:Author: Charlotte Hoarau
:Version: 0.1
:License: Create Commons with attribution
:Date: 26 Avril 2011 

Style : gestion des contours 
##############################
                                           
Introduction
*************

Dans GeOxygene, c’est la norme SLD qui est implémentée pour représenter les objets (géo-)graphiques.

L’implémentation est réalisée en Java, la représentation se fait donc grâce à java2D.

Deux attributs sont utilisés pour définir la forme des contours : Join et Cap. 


L’attribut Join
*****************

Join est utilisé pour définir les jointures entre segments. Il a trois valeurs possibles :


  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+
  |   **Mitre - BasicStroke.JOIN_MITER = 0**                                                        |   **Round - BasicStroke.JOIN_ROUND = 1**                                                     |   **Bevel - BasicStroke.JOIN_BEVEL = 2**                                                     |
  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+
  |   .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeJOINMITER0_.png    | .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeJOINMITER1.png    |   .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeJOINMITER2.png  |
  |      :width: 250 px                                                                             |    :width: 250px                                                                             |      :width: 250px                                                                           | 
  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+


L’attribut Cap
****************

Cap est utilise pour définir les extrémités des segments. Il a trois valeurs possibles :

  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+-----------------------------------------------------------------------------------------------+
  |   **Butt - BasicStroke.CAP_BUTT = 0**                                                           |   **Round - BasicStroke.CAP_ROUND = 1**                                                      |   **Square - BasicStroke.CAP_SQUARE = 2**                                                     |
  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+-----------------------------------------------------------------------------------------------+
  |   .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeCAP_BUTT=0.png     | .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeCAP_ROUND=1.png   |   .. figure:: /documentation/resources/img/contour/ContourPolygoneBasicStrokeCAP_SQUARE=2.png |
  |      :width: 250 px                                                                             |    :width: 250px                                                                             |      :width: 250px                                                                            | 
  +-------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------+-----------------------------------------------------------------------------------------------+



Cas des polygones
*******************

Le paramètre Cap est défini en fonction de la valeur du paramètre Join pour que le point de « fermeture » du polygone soit géré correctement même 
s’il n’est pas utilisé en pratique pour afficher le contour du polygone qui est considéré comme une forme fermée, sans extrémité donc.

  ================== ==================
    Valeur de Join     Valeur de Cap
  ================== ==================
    Mitre  (=0)       Square  (=2)
  	Round  (=1)       Round   (=1)
    Bevel  (=2)       Butt    (=0)
  ================== ==================


Cas des lignes
****************
 
Pour les lignes, il doit être possible de modifier les deux paramètres Join et Cap afin de disposer des six variantes possibles.


  ============================ ==================================================================================== ==================================================================================== ====================================================================================
               Join              Mitre                                                                               Round                                                                                Bevel
    Round
  ============================ ==================================================================================== ==================================================================================== ====================================================================================
   Square                         .. figure:: /documentation/resources/img/contour/ContourPolygoneSquareMitre.png     .. figure:: /documentation/resources/img/contour/ContourPolygoneSquareRound.png     .. figure:: /documentation/resources/img/contour/ContourPolygoneSquareBevel.png
   Butt                           .. figure:: /documentation/resources/img/contour/ContourPolygoneButtMitre.png       .. figure:: /documentation/resources/img/contour/ContourPolygoneButtRound.png       .. figure:: /documentation/resources/img/contour/ContourPolygoneButtBevel.png
   Round                          .. figure:: /documentation/resources/img/contour/ContourPolygoneRoundMitre.png      .. figure:: /documentation/resources/img/contour/ContourPolygoneRoundRound.png      .. figure:: /documentation/resources/img/contour/ContourPolygoneRoundBevel.png
  ============================ ==================================================================================== ==================================================================================== ====================================================================================
  
  

Cas des points
******************

A traiter ...   ->  Gestion différente si le symbole est plutôt « polygonale » (carré, étoile…) ou « linéaire » (tirets, croix) ??


Implémentation dans GeOxygene
*******************************

Tout cela est implémenté dans la classe Stroke de GeOxygene qui est commune au PolygonSymbolizer et au LineSymbolizer. 

Le Stroke est en fait définit dans la classe AbstractSymboliser.


L’implémentation utilise Java2D et son système d’objet graphique avec contour.

  +--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+------------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  |   .. figure:: /documentation/resources/img/contour/ContourPolygoneShape.png    | .. figure:: /documentation/resources/img/contour/ContourPolygoneShapeFill.png    |   .. figure:: /documentation/resources/img/contour/ContourPolygoneShapeStroke.png  | .. figure:: /documentation/resources/img/contour/ContourPolygoneShapeOutline.png |
  |      :width: 97 px                                                             |    :width: 106px                                                                 |      :width: 107px                                                                 |    :width: 106px                                                                 | 
  +--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+------------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  |  Shape pentagon = createRegularPolygon(5,75);                                                                                                                     |  BasicStroke wideline = new BasicStroke(10.0f);                                                                                                                       |
  |                                                                                                                                                                   |                                                                                                                                                                       |
  |                                                                                                                                                                   |  Shape outline = wideline.createStrokedShape(pentagon);                                                                                                               |
  +--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+------------------------------------------------------------------------------------+----------------------------------------------------------------------------------+
  | g.draw(pentagon);                                                              |  g.fill(pentagon);                                                               | g.draw(outline);                                                                   |  g.fill(outline);                                                                |
  +--------------------------------------------------------------------------------+----------------------------------------------------------------------------------+------------------------------------------------------------------------------------+----------------------------------------------------------------------------------+



`Source : flylib.com <http://flylib.com/books/en/2.428.1.134/1/>`_
 

Bibliographie
***************

* `OpenGIS Styled Layer Descriptor Profile of the Web Map Service Implementation Specification, Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC)
  <http://www.opengeospatial.org/standards/sld>`_

* `OpenGIS Symbology Encoding Implementation Specification , Version 1.1.0, Norme de l’Open Geospatial Consortium (OGC) 
  <http://www.opengeospatial.org/standards/se>`_







