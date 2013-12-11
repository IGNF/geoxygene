:Date: 11/2013
:Version: 0.6

Setup Guide : GeOxygene 
####################################


GeOxygene
********************

Tout est en place pour l'installation de GeOxygene.


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

      D'abord sur : 

      .. container:: chemin

         File >> Import   
         
      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/Import_01.png
            :width: 350px
       
            Figure 10 : Import Project


   .. container:: rightside

      Puis sur : 
      
      .. container:: chemin

         Maven >> Checkout Maven Projects from SCM

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/Import_02.png
            :width: 350px
       
            Figure 11 : Check out Maven Projects from SCM


.. container:: twocol

   .. container:: leftside

      Ensuite comme l'indique la figure suivante, sélectionner **svn** dans la première liste comme SCM URL et indiquer l'adresse du svn de GeOxygene :

      * Si vous êtes enregistré sur `Sourceforge <http://sourceforge.net/>`_  et si vous avez des droits en tant que développeur ou administrateur sur le projet geoxygene : 

      .. container:: svnurl
    
         https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene 

      * Sinon :

      .. container:: svnurl
   
         http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/ 

      puis cliquer sur "Next".
 
   
   .. container:: rightside

      .. container:: centerside
     
          .. figure:: /documentation/resources/img/install/geoxygeneEtape3.png
       
             Figure 12 : SCM URL for check out GeOxygene 


.. container:: twocol

   .. container:: leftside
   
      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/geoxygeneEtape4.png
            :width: 420px
       
            Figure 13 : Configure your import
   
   .. container:: rightside

      Dans le panneau suivant, vous pouvez:

      * sélectionner le répertoire où sera stocké votre projet (par défaut dans le workspace courant), 

      * ajouter le projet à un working set (c'est à dire à un groupe de projets)

      * modifier le nom du (ou des) projet(s) récupéré (s) (dans Advanced). 
      Cette dernière option est utile si vous souhaitez ajouter à tous les projets récupérés un préfixe, un suffixe au nom du projet. 
      Par exemple **geox-[artifactId]** vous créera, pour geoxygene, n projets nommés geox-xxxx.


Cliquez ensuite sur Finish.



Compilation
==================

Lancer un maven build manuellement. Pour cela :


1. Dans le menu, cliquer sur 
      
  .. container:: chemin
      
     Run >> Run Configurations
    
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape1.png
         :width: 600px
          
         Figure 22
       
2. Sélectionner comme type de run "Maven", puis cliquer dans le menu en haut sur "New launch configuration"
      
  .. container:: centerside
   
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape2.png
         :width: 350px
             
         Figure 23

3. Dans la nouvelle fenêtre "Run configuration" configurer :
         
     **Name** : geoxygene
         
     **Base directory** : saisir le chemin d'installation de GeOxygene (c'est celui de votre Workspace auquel il faut ajouter geoxygene)
         
     **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)
         
  
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/install/geoxygeneRunEtape3.png
         :width: 600px
             
         Figure 24


Si tout se passe bien, Maven devrait récupérer tous les jars des librairies nécessaires et compiler le projet. 


Fichier de configuration des plugins
========================================
Créer le fichier **geoxygene-configuration.xml** depuis le fichier template:

   .. container:: chemin
        
      $geoxygene >> copy geoxygene-appli/conf/geoxygene-configuration-template.xml geoxygene-appli/geoxygene-configuration.xml


Ce fichier contient l'ensemble des plugins lancés au démarrage de l'application. Pour en ajouter ou supprimer il suffit de modifier le fichier.

   .. literalinclude:: /documentation/resources/code_src/geoxygene-configuration.xml
           :language: xml


Lancement de l'interface graphique 2D
*********************************************
1. Dans le menu, cliquer sur 
      
   .. container:: chemin
      
      Run >> Run Configurations


2. Sélectionner comme type de run "Java Application", puis faire un click droit et sélectionner "New"


3. Configurer les éléments suivants :

  3.1 Dans la partie "Main"

     * **Name** : geoxygene

     * **Project** : geoxygene-appli

     * **Main class** : fr.ign.cogit.geoxygene.appli.GeOxygeneApplication


  .. container:: centerside
      
      .. figure:: /documentation/resources/img/install/GeOxygeneAppliRunAs.png
         :width: 700px
       
         Figure 25 - Lancement de l'interface graphique


  3.2 Dans la partie "Arguments", pour la machine virtuelle :
 
     * **VM arguments** : -Djava.library.path=dll\win64 -Xms512M -Xmx1G
     
       où *dll\win64* définit l'emplacement où sont stockées vos librairies logicielles système (*.DLL, *.SO)


  .. container:: centerside
     
      .. figure:: /documentation/resources/img/install/GeOxygeneAppliRunAs02.png
         :width: 700px
       
         Figure 26 - Lancement de l'interface graphique 


4. Cliquer sur **Run**, l'interface de GeOxygene est lancée !


.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/GeOxygene2D.png
       :width: 700px
       
       Figure 26 - Interface graphique GeOxygene 2D 



Lancement de l'interface graphique 3D
***************************************************

A venir



Plugins GeOxygene pour OpenJump
****************************************

A venir


