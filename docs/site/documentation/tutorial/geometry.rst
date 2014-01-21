

Geometrie : structure et manipulation
######################################

Introduction
**************

Dans GeOxygene la dimension spatiale des objets géographiques repose sur la large gamme de primitives géométriques 
et topologiques spécifiée par la norme ISO 19107.

La géométrie est implémentée dans le package **geoxygene-spatial**, et les interfaces sont implémentées dans **geoxygene-api**


Implémentation dans GeOxygene
*******************************

- GM_Object est la classe mère des objets géographiques. Un GM_Object peut-être soit une primitive (GM_Primitive), 
  un agrégat (GM_Agregate) ou un complexe (GM_Complex). Agrégats et complexes sont des collections de primitives 
  plus ou moins particulières (cf. plus bas).


- La géométrie est implémentée dans le package fr.ign.cogit.geoxyegene.spatial lui-même découpé en sous-packages.
  
    - Le package fr.ign.cogit.geoxyegene.spatial.geomroot ne contient que la classe mère GM_Object.
    - Le package fr.ign.cogit.geoxyegene.spatial.geomaggr contient les classes géométriques d’agrégats.
    - Le package fr.ign.cogit.geoxyegene.spatial.geomcomp contient les classes de complexes géométriques.
    - Le package fr.ign.cogit.geoxyegene.spatial.geomprim contient les classes de primitives qui ne stockent 
      pas directement de coordonnées (dont GM_Point, GM_Curve et GM_Surface).
    - Le package fr.ign.cogit.geoxyegene.spatial.coordgeom contient les classes de primitives qui stockent 
      des coordonnées (dont DirectPosition, GM_LineString, GM_Polygon).

- Un agrégat est une collection de GM_Object sans aucune structure interne. 
  On peut trouver des agrégats hétérogènes (GM_Aggregate) c’est-à-dire composés de primitives de différents types, 
  ou des agrégats homogènes (GM_MultiPrimitive et ses sous-classes) c’est-à-dire composés de primitives 
  du même type (point, ligne, surface).

- Un complexe est une collection structurée de GM_Primitive. On impose en particulier aux primitives d’être connectées. 
  La classe GM_Complex rassemble des primitives hétérogènes ; on ne l’utilise pas pour le moment. 
  On utilise la sous-classe GM_Composite et ses sous-classes : c’est une collection de primitives, homogène à une primitive. 
  Ceci signifie :

    - Un GM_CompositePoint est homogène à un GM_Point, donc est composé d’un et d’un seul point (ça ne sert pas à grand chose ! ☺)
    - Une GM_CompositeCurve est homogène à une courbe : elle est composée de primitives linéaires connectées telles que le point final 
      de l’une soit le point initial de la suivante.
    - Un GM_Ring est une GM_CompositeCurve particulière : elle se referme (point initial de la première primitive = point final de la dernière primitive).
    - Une GM_CompositeSurface est homogène à une surface : elle est composée de primitives surfaciques adjacentes qui ne se recouvrent pas.
    - Un GM_Shell est une GM_CompositeSurface particulière : elle se referme.

- La primitive linéaire de base s’appelle GM_Curve. Une GM_Curve est composée d’un ou plusieurs GM_CurveSegment. 
  Un GM_CurveSegment peut être une polyligne (GM_LineString), une suite d’arcs de cercles (GM_ArcString), 
  une spline (GM_SplineCurve), etc… Un nombre conséquent de GM_CurveSegment est prévu par le modèle.

- Le seul GM_CurveSegment implémenté dans GeOxygene est la polyligne GM_LineString. En effet, les SGBD et SIG actuels 
  n’offrent pas beaucoup d’autres alternatives pour le stockage des primitives linéaires. 
  La GM_LineString est de surcroît une GM_Curve particulière, composée d’un et d’un seul segment qui est elle-même (extension de la norme ISO). 
  Cette extension à la norme permet de travailler directement et facilement sur les instances de la classe GM_LineString, qui est le cas le plus courant. 
  Malgré tout, la classe GM_Curve existe et est utilisable.

- La frontière d’un GM_Polygon est un GM_Ring.

- On retrouve, pour les surfaces, une modélisation analogue à celle sur les linéaires. 
  La primitive surfacique de base s’appelle dans la norme GM_Surface. Elle est composée de GM_SurfacePatch. 
  Un GM_SurfacePatch peut être un polygone (GM_Polygon) ou des choses plus compliquées pour permettre de travailler 
  en 3D (cylindre, sphère, etc.). GM_Polygon est ici une GM_Surface particulière composée d’un et d’un seul GM_SurfacePatch qui est lui-même. 
  Ceci permet là aussi de travailler directement sur GM_Polygon. En pratique, dans la version actuelle de GeOxygene, 
  seule la classe GM_Polygon est utilisable (GM_Surface ne l’est pas).

- Il existe des classes de primitives orientées (GM_OrientableCurve, GM_OrientableSurface). 
  On appelle « primitive » la primitive orientée positivement (GM_Curve, GM_Polygon). 
  Chaque primitive orientée est liée à sa primitive orientée positivement via le lien primitive. 
  En fait, la primitive orientée positivement et la primitive sont le même objet. 
  C’est elle qui est éventuellement liée à une primitive topologique. 
  La primitive est liée à ses deux primitives orientées via le lien proxy (sachant que la primitive orientée positivement est elle-même …).

- La classe DirectPosition représente un tableau de X,Y,Z, avec certaines méthodes associées (non détaillées ici). 
  Si on travaille en 2D, le Z n’est pas renseigné. On ne parle pas des primitives 3D dans ce document.

- A noter qu’il existe des classes qui sont des structures pour représenter les frontières des objets géométriques 
  (GM_CurveBoundary pour représenter la frontière d’une GM_Curve, GM_SurfaceBoundary pour représenter la frontière d’un GM_Polygon). 
  Leur utilisation n’est pas fondamentale.





Mise en pratique 
******************

Apprendre par l'exemple !

.. container:: chemin

   Problème : on voudrait réaliser une carte à l'échelle du monde et la centrer sur une longitude particulière 
   dans l'interface graphique 2D de GeOxygene. 
   Par exemple, à partir de la projection WGS84, on va 
   « déplacer » vers l'est les Etats-Unis, afin d'avoir une emprise complète entièrement à gauche de la carte .... 


Le code source de cet exemple est disponible dans GeOxygene 
`ici <http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/geoxygene-appli/src/main/java/fr/ign/cogit/geoxygene/appli/example/ChangementProjectionShape.java>`_


.. container:: centerside

   .. figure:: /documentation/resources/img/geometry/ChangementProjectionShape.png
      :width: 500px


.. literalinclude:: /documentation/resources/code_src/geometry/TestChangementProjectionShape.java
      :language: java



Références
***********

- Quelques compléments sur le modèle géométrique :
    o Se fier à la description technique du modèle et à la norme ISO 19107.
