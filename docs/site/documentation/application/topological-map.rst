
:Author: Olivier Bonin, Sébastien Mustière – IGN / Laboratoire COGIT
:Version: 1.0
:Date: 11 décembre 2008
:License: Create Commons with attribution

La Carte Topologique
######################
                                           
                                
Objectif de la carte topologique
========================================
L'objectif de la carte topologique est de fournir un schéma simple que les utilisateurs
peuvent éventuellement étendre par héritage pour :

* Créer facilement des classes d'objets géographiques structurées avec une
  topologie de réseau ou une topologie de carte topologique. L'utilisateur accède
  alors directement à la topologie entre les objets de la carte topologique (ex: un
  noeud a des arcs entrants).
* Coder et récupérer dans ce schéma commun tous les algorithmes qui s'appuient
  sur de telles structures, en entrée comme en sortie. Exemples : plus court chemin
  entre deux noeuds, décomposition d'un graphe en composantes connexes,
  création d'une triangulation de Delaunay, appariement entre deux graphes.

Les objectifs de la carte topologique sont volontairement restreints pour privilégier la
simplicité qui est suffisante à beaucoup d'applications. En particulier, le but n'est
pas :

* De gérer la multi-représentation.
* De permettre l'utilisation de primitives géométriques relativement complexes
  comme les surfaces à trou, ou les combinaisons de segments et d'arcs de cercle.

Elle est cependant extensible, par le mécanisme classique d'héritage.


Grands principes
=========================

.. container:: centerside

        .. figure:: /documentation/resources/img/cartetopo/cartetopo-grand-principe.png
       

Dans GeOxygene, les classes dite géographiques (qui héritent de « FT_Feature » :
routes, rivières…) ne sont pas structurées topologiquement. Ces classes
géographiques, issues des bases de données utilisées ne portent pas ou peu de
relations entre elles, et contiennent des objets de géométrie non typée : chaque objet
est relié à un objet de type GM_Object.

Pour développer une application géographique avec la carte topologique, l'utilisateur
peut s'appuyer sur la structure de carte topologique existante ou commence par
créer par héritage un schéma applicatif de type carte topologique (voir la carte
topologique du package carteTopo et l'exemple de la triangulation qui est une carte
topologique particulière). L'utilisateur instancie alors une sa carte topologique à partir
de ses données géographiques. Une classe « chargeur » permet de réaliser des
chargements typiques (ex : à chaque ligne des données géographiques de base correspond un arc de la carte topologique). 
La présence à la fois de noeuds, d'arcs et
de faces n'est pas obligatoire, et les relations topologiques ne sont pas forcément
toutes renseignées. Des outils permettent de copier une carte topologique avec
toutes ses relations, ou d'effacer une carte.
  
Les avantages de charger les données dans un schéma de type carte topologique, et
ensuite de coder tous les algorithmes dans ce schéma, sont multiples :

- Les classes rattachées à la carte topologique ont une géométrie qui est typée :
  les classes de type Noeud ont une géométrie de GM_Point, celles de type Arc ont
  une géométrie de type GM_LineString, et celles de type Face ont une géométrie
  de GM_Polygon. On évite ainsi dans le code de nombreux transtypages, et on
  rend le code plus robuste en imposant la vérification des types à la compilation.
  De plus, l'utilisateur n'a besoin de connaître qu'une partie très réduite du modèle
  ISO du noyau de la plate-forme.
- La topologie porte directement sur les objets de la carte topologique. Un Arc a un
  noeud initial de type Noeud, et la fonction qui renvoie son noeud initial renvoie un
  objet correctement typé.
- Les fonctions utilisant la topologie peuvent être codées de manière claire et
  générique, et organisées logiquement. Un calcul de plus court chemin peut ainsi
  être codée sur la carte topologique, et réutilisé par tous.
- L'API de la carte topologique est très compacte, assez intuitive, et reprend un
  mode de représentation classique de la topologie (l'ISO propose également un
  modèle de description de la topologie à travers les « TP_Object » ; ce modèle est
  prévu dans GéOxygène, mais en pratique il s'est révélé trop complexe à
  manipuler pour beaucoup d'applications).


Le package Carte Topologique
===================================
Le schéma UML en fin de ce document donne les principes de la carte topologique.
Pour retrouver les relations en Java, voir la documentation du code et la
documentation de l'API.

Ce package contient les classes nécessaires à l'utilisateur pour qu'il puisse
manipuler la topologie et y raccrocher ses propres classes applicatives si il veut
raffiner la carte topologique.

Classe CarteTopo
------------------------

C'est la classe qui représente l'ensemble de la carte topologique. Cette carte peut
n'être qu'un réseau (pas de faces) ou un graphe. Notons que l'on peut gérer des
graphes orientés grâce à l'attribut "orientation" des arcs.
Cette classe est le point d'entrée de toute carte topologique. Elle contient des
méthodes qui permettent de la gérer : "efface", "copie"...

Cette classe contient également des méthodes d'analyse spatiale qui s'appliquent
sur toute une carte topo, et non sur un élément particulier (ex : décomposer en
groupes connexes).

Relations arcs/noeuds/faces
-----------------------------------

La topologie modélisée est la topologie classique d'une carte topologique : un arc a
des noeuds initiaux et finaux, une face à droite et une face à gauche, une face est
entourée d'arcs, etc.
Pour simplifier le schéma UML on a regroupé plusieurs relations topologiques en une
seule. Ainsi, à partir d'un noeud on peut retrouver :

- soit tous les arcs entrants [resp. sortants] d'un noeud au sens de la géométrie
  (logique de stockage),
- soit tous les arcs entrants [resp. sortants] d'un noeuds au sens de l'orientation
  (logique de circulation),
- soit tous les arcs en une fois (entrants et sortants).

De plus, ces arcs peuvent être soit retournés en vrac, soit ordonnés selon leur
géométrie de manière à tourner autour du noeud.

Chargeur
------------------
Le chargeur permet de remplir une carte topo à partir des instances d'une classe
géographique. Par exemple le chargeur crée un "noeud" pour chacune des instances
de la classe "bâtiment ponctuel BDTopo".

Pour faire des chargements plus spécifiques, on peut créer son propre chargeur.
Exemple : créer un noeud pour chacune des instances des classes géo A et B qui ont
tel attribut avec telle valeur. Le chargeur de la carte topologique gère les cas
simples, et a valeur d'exemple.

.. container:: centerside

        .. figure:: /documentation/resources/img/cartetopo/ModeleTopologique.png

           Figure 2 : Modèle topologique raffiné spécifié par l’utilisateur (éventuellement)        
        