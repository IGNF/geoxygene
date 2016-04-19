
Site web GeOxygene 
====================

Introduction
**************

Le site internet dédié au projet GeOxygene est consultable sur internet, à l'adresse suivante: 
\href{http://oxygene-project.sourceforge.net/maven}{http://oxygene-project.sourceforge.net/maven}.

Il est généré automatiquement puis déployé sur internet à partir du code source du projet *geoxygene-docs*, 
grâce à Sphinx. 






Sphinx
*********
Windows
^^^^^^^^^

Linux
^^^^^^^^
Coming soon ....


Code source du site web
**************************

Checkout
^^^^^^^^^^^^

Architecture sur la forge
^^^^^^^^^^^^^^^^^^^^^^^^^^^^


Compiler et tester
********************


Déploiement sur sourceforge
*****************************


Le site internet dédié au projet GeOxygene est consultable sur internet, à l’adresse suivante : 
[http://oxygene-project.sourceforge.net/index.html http://oxygene-project.sourceforge.net/index.html].


Le code source se trouve dans le sous-module '''docs''' de GeOxygene (OpenSource)

<pre>
https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-docs/
</pre>



== Administration  == 
* URL de production : http://oxygene-project.sourceforge.net/
* URL de test : http://rks0911w044.ign.fr:8083/jenkins/job/Geoxygene%20-%20site%20web/ws/target/site/html/index.html

* Langue officielle : Anglais

== Génération des sources du site internet ==
Le site est construit à partir de '''Sphinx'''.

La prcoédure d'installation est décrite ici : [http://techquila.com/tech/2013/05/installing-sphinx-on-windows/ installing-sphinx-on-windows]. Il faut installer successivement python, easy_install et sphinx.

Quelques liens pour écrire les fichiers textes :
* http://openalea.gforge.inria.fr/doc/openalea/doc/_build/html/source/sphinx/rest_syntax.html
* http://sphinx-doc.org/contents.html

== Compilation ==

Le module est indépendant des autres modules GeOxygene.

Il suffit de lancer un compile au niveau du module :
<pre>
mvn compile
</pre>

== Mise en ligne du site internet ==

Le site se met à jour via une connexion au serveur web à partir de FileZilla. 
La procédure est décrite [[Forge_GeOxygene_OpenSource | ici]].


N.B.
***********

  --> Droits commits + déploiement
  
  
/home/project-web/oxygene-project/htdocs
