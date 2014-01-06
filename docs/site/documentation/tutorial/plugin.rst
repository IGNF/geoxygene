

Plugin dans l'interface graphique 2D.
######################################

   ... ou comment ajouter de nouvelles fonctionnalités dans l'interface graphique de GeOxygene.

Les contributions des développeurs ne sont pas inclues dans le module de l'interface graphique 2D, 
elles sont implémentées à l'aide de plugins. 

Actuellement les plugins sont regroupés suivant les thèmes de recherche du laboratoire :
sémiologie, généralisation ou appariement de données. 


Plugins GeOxygene
*******************

Il existe deux types de plugins :

- les GeOxygeneApplicationPlugin, qui permettent par exemple d'ajouter des menus à l'interface principale (c'est ceux qui existaient avant).
- les ProjectFramePlugin, qui permettent d'ajouter des éléments graphiques (toolbar, slider, ...) à un projectFrame afin de le modifier.

L'initialisation des plugins se fait à différent moment suivant le type choisi. 
Les plugins classiques (GeOxygeneApplicationPlugin) sont initialisés au chargement de l'interface graphique, 
et les plugins de type ProjectFrame sont initialisés à chaque nouvelle création d'un projectFrame.

Ci-dessous le diagramme de classe des plugins implémentés dans le module geoxygene-appli :

.. container:: centerside
     
   .. figure:: /documentation/resources/img/plugin/class-diagram.png
      :width: 1000px
       
      Figure 1 : Diagramme de classe des plugins dans GeOxygene


En général les plugins sont implémentés dans un module Maven dédié à un thème. 

Les plugins sont configurés dans un fichier **plugins.xml** stocké dans un répertoire **conf** à la racine du module.

.. literalinclude:: /documentation/resources/code_src/plugin/plugins-geox.xml
        :language: xml

Il est possible d'ajouter des arguments au plugin de type clé-valeur, comme par exemple le plugin SemioToolbarPlugin 
(le paramètre configure l'affichage ou non de la toolbar).


Quickstart
************

Ce tutoriel explique comment créer un plugin GeOxygene depuis un nouveau module Maven à partir d'un exemple. 
Nous voulons ici ajouter un nouveau bouton dans la barre de menu (notre plugin sera donc de type GeOxygeneApplicationPlugin) 
qui permet de lancer un filtre gaussien (lissage) sur un réseau linéaire.

1. Tout d'abord il faut ajouter la librairie "geoxygene-appli" à notre projet. Cette librairie contient les classes de l'interface graphique GeOxygene 2D.

Ouvrir le fichier pom.xml de votre module Maven et ajouter une dépendance au module "geoxygene-appli" :

.. literalinclude:: /documentation/resources/code_src/plugin/pom-appli.xml
        :language: xml


2. Créer une nouvelle classe qui implémente GeOxygeneApplicationPlugin, par exemple GaussianFilterPlugin.java :

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin01.java
        :language: java


3. Puis il faut surcharger la méthode "initialize" qui initialise le plugin. 
Dans notre exemple, on ajoute un nouveau bouton et un évènement sur celui-ci.

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin02.java
        :language: java

4. Enfin on définit notre processus dans la fonction qui traite l'évènement : 

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin03.java
        :language: java

5. Fichier de configuration

Dans votre module, ajouter à la racine du projet un fichier geoxygene-configuration.xml qui contient ces lignes :

.. literalinclude:: /documentation/resources/code_src/plugin/plugins.xml
        :language: xml

6. Lancement de l'interface

Enfin, exécuter une nouvelle java application en spécifiant :

.. container:: chemin
    
      **Name** : GaussianFilterPlugin
  
      **Project** : monProjet
      
      **Main class** : fr.ign.cogit.geoxygene.appli.GeOxygeneApplication

.. container:: centerside
     
    .. figure:: /documentation/resources/img/plugin/LancementInterfaceGeoxAutreModule.png
       :width: 650px
       
       Figure 1 : Lancement de l'interface GeOxygene avec votre nouveau plugin

Ce qui donne :

.. container:: centerside
     
    .. figure:: /documentation/resources/img/plugin/GaussianFilterPluginGUI.png
       :width: 650px
       
       Figure 2 : Gaussian Filter Plugin
  




