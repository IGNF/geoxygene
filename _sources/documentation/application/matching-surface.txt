:Author: Sébastien Mustière
:Version: 1.0
:License: Create Commons with attribution

Appariement de surface 
#########################

Package concerné : **fr.ign.cogit.geoxygene.contrib.appariement.surfaces**

.. literalinclude:: /documentation/resources/code_src/matching/depedencies.xml
   :language: xml

Lancement du processus
=======================
Le processus se lance par la méthode appariementSurfaces de la classe
AppariementSurfaces, après avoir fixé les paramètres en créant un objet
ParametresAppSurfaces.

Principe général
=================
Le processus d’appariement de surfaces a été conçu dans une optique d’évaluation
de la "qualité de données" où une des deux bases à apparier est considérée comme
une référence, de meilleure qualité que l'autre. L'appariement est utilisé comme
étape préliminaire à la comparaison des objets dans [Bel Hadj Ali 2001]. Les liens
d'appariement sont qualifiés plus finement par la suite (e.g. avec des mesures de
forme des objets), mais ceci n’est pas disponible dans le code présent.


Ces outils ont été mis au point pour apparier principalement des surfaces isolées
(e.g. "bâtiments") plus que des partitions ou des surfaces connectées (e.g.
"occupation du sol"), même si des tests relativement fructueux ont été effectués sur
ce type d'objets.


Les principes généraux de l’approche sont les suivants:

* Apparier des surfaces en comparant les surfaces elles-mêmes plus que les lignes contours.

* Le choix des mesures utilisées s'appuie sur des principes probabilistes.

* L'optique générale est 1/ apparier avec des mesures simples (intersections de
  surfaces) puis 2/ qualifier ensuite avec des mesures plus fines (et éventuellement
  rejeter alors l'appariement), mais cette deuxième étape n’est pas disponible pour
  l’instant. Ceci explique la simplicité des mesures utilisées lors de l'appariement
  présenté ici.

* Ne tient compte que de la géométrie; les éventuelles relations de connexion entre
  surfaces sont ignorées.


Description du processus
=========================
1. Pré-appariement: Les surfaces des BD1 et BD2 sont associées en faisant des
   comparaisons 1-1 entre les objets des deux jeux de données. Deux objets sont
   associés si ils respectent la mesure d'association (voir définition des mesures cidessous).

2. Regroupement: des liens entre groupes de bâtiments (liens n-m) sont créés à
   partir des liens d'association (i.e. on identifie les parties connexes du graphe
   reliant les entités grâce au lien d'association)

3. Affinement des liens n-m. Pour 2 groupes G1 et G2 de bâtiments reliés, on
   recherche les meilleurs sous-groupes SG1 et SG2 de G1 et G2 maximisant soit la
   somme Exactitude(SG1,SG2) + Complétude(SG1, SG2), soit minimisant Distance
   surfacique (SG1, SG2) (cette dernière option est conseillée par l'auteur en cas de
   bases avec des résolutions similaires). Des heuristiques sont proposées par
   l'auteur pour ne pas tester toutes les configurations possibles.

4. Vérification des liens. Ces appariements entre sous-groupes ne sont acceptés
   que si l'exactitude et la complétude dépassent un certain seuil (ou la distance
   surfacique). Ce seuil est fixé empiriquement par analyse des données dans les
   tests réalisés, de l'ordre de 0,5 pour l'exactitude et la complétude, ou 0,6 pour la
   distance surfacique.


Mesures utilisées

Soient A et B deux surfaces (chacune connexe ou non) :

* Association(A,B) = 

  - Vrai si Surface(A ∩ B) > min(R1,R2) ; 
        avec R1 et R2 les résolutions des BDs contenant A et B
        et Surface(A ∩ B) > Surface(A) x 0,2
        et Surface(A ∩ B) > Surface(B) x 0,2
  
  - Faux sinon

* Distance surfacique = (Surface(A ∪ B) – Surface(A ∩ B)) / Surface(A ∪ B)

* Exactitude(A,B) = Surface(A ∩ B) / Surface(A)

* Complétude(A,B) = Surface(A ∩ B) / Surface(B)


Références
============
* [Bel Hadj Ali 2001] :


