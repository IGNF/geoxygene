

Création d'un plugin dans l'interface 2D GeOxygene
###########################################################

Les plugins GeOxygene permettent d'ajouter de nouvelles fonctionnalités que l'on exécute depuis l'interface de GeOxygene.


Ce tutoriel explique comment créer un plugin GeOxygene depuis un module Maven. 


Le plugin ajoute un nouveau bouton dans la barre de menu qui permet de calculer un filtre gaussien sur la couche sélectionnée. 



pom.xml
**************
Tout d'abord il faut ajouter la librairie "geoxygene-appli" à votre projet. Cette librairie contient les classes de l'interface graphique 2D.

Ouvrir le fichier pom.xml de votre module et ajouter une dépendance au module "geoxygene-appli" :

.. literalinclude:: /documentation/resources/code_src/plugin/pom-appli.xml
        :language: xml


Plugin java
******************

Créer une nouvelle classe qui implémente GeOxygeneApplicationPlugin, par exemple GaussianFilterPlugin.java :

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin01.java
        :language: java


Puis il faut surcharger la méthode initialize qui initialise le plugin. 
Dans notre exemple, on ajoute un nouveau bouton et un évènement sur celui-ci.

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin02.java
        :language: java


Enfin on définit notre processus dans la fonction qui traite l'évènement : 

.. literalinclude:: /documentation/resources/code_src/plugin/GaussianFilterPlugin03.java
        :language: java
        

Fichier de configuration
*********************************

Dans votre module, ajouter à la racine du projet un fichier geoxygene-configuration.xml qui contient ces lignes :

.. literalinclude:: /documentation/resources/code_src/plugin/plugins.xml
        :language: xml



Lancement de l'interface
*********************************
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
  




