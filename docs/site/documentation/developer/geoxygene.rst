:Date: 11/2013
:Version: 0.6

Setup GeOxygene 
####################################


GeOxygene
********************

.. --------------------------------------------------------------------------------------------------------------
..   Third Part : GEOXYGENE
.. --------------------------------------------------------------------------------------------------------------

Importer le projet GeOxygene
============================================

Dans Eclipse la création d'un nouveau projet s'effectue via l'assistant "Import". 
Celui-ci offre en effet une pléthore de modèles. Il suffit donc pour importer GeOxygene de choisir 
celui qui va extraire un projet Maven depuis un SCM (dans notre cas SVN). 

Comme décrits dans les deux captures d’écran ci-dessous, cliquer :

.. container:: twocol

   .. container:: leftside

      1. D'abord sur : 

      .. container:: chemin

         File >> Import   
         
      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/Import_01.png
            :width: 350px
       
            Figure 1 : Import Project


   .. container:: rightside

      2. Puis sur : 
      
      .. container:: chemin

         Maven >> Checkout Maven Projects from SCM

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/Import_02.png
            :width: 350px
       
            Figure 2 : Check out Maven Projects from SCM


3. Ensuite comme l'indique la figure suivante, sélectionner **svn** dans la première liste comme SCM URL et indiquer l'adresse du svn de GeOxygene :

.. container:: centerside
     
   .. figure:: /documentation/resources/img/install/geoxygeneEtape3.png
       
      Figure 3 : SCM URL for check out GeOxygene

* Si vous êtes enregistré sur `Sourceforge <http://sourceforge.net/>`_  et si vous avez des droits en tant que développeur ou administrateur sur le projet geoxygene : 

  .. container:: svnurl
    
     https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene 

* Sinon :

  .. container:: svnurl
   
     http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/ 


4. puis cliquer sur "Next".
 
   
.. container:: twocol

   .. container:: leftside
   
      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/geoxygeneEtape4.png
            :width: 420px
       
            Figure 4 : Configure your import
   
   .. container:: rightside

      5. Dans le panneau suivant, vous pouvez:

      * sélectionner le répertoire où sera stocké votre projet (par défaut dans le workspace courant), 

      * ajouter le projet à un working set (c'est à dire à un groupe de projets)

      * modifier le nom du (ou des) projet(s) récupéré (s) (dans Advanced). 
      Cette dernière option est utile si vous souhaitez ajouter à tous les projets récupérés un préfixe, un suffixe au nom du projet. 
      Par exemple **geox-[artifactId]** vous créera, pour geoxygene, n projets nommés geox-xxxx.


6. Cliquez ensuite sur **Finish**.


7. Certains connectors vont peut-être se mettre à jour ou pas durant cette phase. Cliquer sur **OK** si vous avez un message 
   d'avertissement ou d'erreur de type *Maven Goal Execution*, 

   .. container:: centerside
     
         .. figure:: /documentation/resources/img/geoxygene/IncompleteMaven.png
            :width: 500px
       
            Figure 5 : Configure your import


Compilation
==================

Lancer un maven build manuellement. Pour cela :


1. Dans le menu, cliquer sur 
      
  .. container:: chemin
      
     Run >> Run Configurations
    
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape1.png
         :width: 600px
          
         Figure 6
       
2. Sélectionner comme type de run "Maven", puis cliquer dans le menu en haut sur "New launch configuration"
      
  .. container:: centerside
   
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape2.png
         :width: 350px
             
         Figure 7

3. Dans la nouvelle fenêtre "Run configuration" configurer :
         
   .. container:: field
   
      **Name** : geoxygene
         
      **Base directory** : saisir le chemin d'installation de GeOxygene 
                              (c'est celui de votre Workspace auquel il faut ajouter geoxygene)
         
      **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)
         
  
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape3.png
         :width: 600px
             
         Figure 8


Si tout se passe bien, Maven devrait récupérer tous les jars des librairies nécessaires et compiler le projet. 

Cette opération peut prendre un certain temps !


Fichier de configuration des plugins
========================================

Créer un fichier *geoxygene-configuration.xml* depuis le fichier template *geoxygene-appli/conf/geoxygene-configuration-template.xml* 
et placez-le à la racine du module *geoxygene-appli* :

   .. container:: chemin
        
      E:\\Workspace\\GeOxygene >> copy geoxygene-appli/conf/geoxygene-configuration-template.xml geoxygene-appli/geoxygene-configuration.xml


Ce fichier contient l'ensemble des plugins lancés au démarrage de l'application. Pour en ajouter ou supprimer il suffit de modifier ce nouveau fichier.

   .. literalinclude:: /documentation/resources/code_src/geoxygene-configuration.xml
           :language: xml


Lancement des interfaces graphiques 
=================================================================
Le guide de lancement des interfaces graphiques est décrit sur la : :ref:`page suivante <launchinggeox>`.


Plugins GeOxygene pour OpenJump
****************************************

Les plugins GeOxygene pour OpenJump sont dans un module dédié et dans un projet différent de celui de GeOxygene.
Ils ne nécessitent pas forcément l'installation du projet GeOxygene.

Importer le projet GeOxygene-ojplugin
=======================================
Suivez les mêmes étapes que l'import du projet GeOxygene. En revanche, les URLS du serveur SVN sont à choisir parmi celles-ci :

Si vous êtes enregistré sur `Sourceforge <http://sourceforge.net/>`_  et si vous avez des droits en tant que développeur ou administrateur sur le projet geoxygene : 

.. container:: svnurl
    
   https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-ojplugin 

Sinon :

.. container:: svnurl
   
   http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-ojplugin


Compilation
==============
Suivez les mêmes étapes que la compilation du projet GeOxygene. Dans la nouvelle fenêtre "Run configuration" configurer :
         
  .. container:: field

     **Name** : geoxygene-ojplugin
        
     **Base directory** : saisir le chemin d'installation de geoxygene-ojplugin 
                              (c'est celui de votre Workspace auquel il faut ajouter geoxygene-ojplugin)
         
     **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)


Lancement des plugins Geoxygene d'OpenJump à partir d'Eclipse 
=================================================================
Cette méthode ne nécessite pas d'avoir installé OpenJump sur sa machine.

1. Click droit dans l'explorateur d'Eclipse, puis "Run As", puis "Java Application"

2. Dans le premier onglet saisissez comme MainClass :

   .. container:: chemin

      com.vividsolutions.jump.workbench.JUMPWorkbench


.. container:: centerside

   .. figure:: /documentation/resources/img/geoxygene/LancerOJEclipse01.png
      :width: 600px
      

3. Dans le second onglet, ajouter comme Program arguments de la ligne de commande :

   .. container:: chemin

      -properties ./src/main/resources/workbench-properties.xml
      -I18n fr

   
.. container:: centerside
   
   .. figure:: /documentation/resources/img/geoxygene/LancerOJEclipse02.png
      :width: 600px
             

4. Pour lancer les plugins "quality", ceux-ci nécessitent la librairie java3d. Une version des dll est stockée dans le module "geoxygene-sig3d".

   Ajouter comme VM arguments de la ligne de commande :

   .. container:: chemin

      -Xmx1536M
      -Djava.library.path=D:/Workspace/geoxygene/geoxygene-sig3d/lib/native_libraries/windows-i586/


5. Clicker sur le bouton "Run" et openjump se lance, avec les plugins GeOxygene.



