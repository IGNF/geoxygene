.. _filtre:

Expression de filtres
#######################

Introduction
**************

Le module **Filter** permet l'expression de filtres sur une collection de features pour le choix d'éléments d'une collection.
Il est utilisé par exemple pour :

- appliquer des styles différents à des Features d'une même FeatureCollection.
- définir des règles à appliquer aux agents


Implémentation dans GeOxygene
*********************************

.. container:: centerside
  
   .. figure:: /documentation/resources/img/filter/DC_Filter.png
      :width: 950px
       
      Figure 1 : DC Filter dans GeOxygene

Dans la description du filtre, le choix des éléments peut porter sur : 

- la valeur d'un attribut (PropertyIsEqualTo)
- une propriété géométrique (intersects)
- la valeur d'une expression (+, -, *, /, etc)
- la combinaison d'expressions (et, ou, etc.)


Application d'un filtre dans un SLD
************************************

Par exemple on voudrait filtrer une feature collection, par un attribut **nature** : si la valeur est égale à **carre jaune** ou si la valeur est égale à **carre gris**. 

.. container:: centerside
  
   .. figure:: /documentation/resources/img/filter/carre.png


.. literalinclude:: /documentation/resources/code_src/filter/carre.java
           :language: java



Références
*************
* OGC, `Filter Encoding <http://www.opengeospatial.org/standards/filter>`_
