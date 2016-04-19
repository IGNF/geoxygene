:Date: 11/2013
:Version: 0.6

Installation de GeOxygene 
##########################


.. --------------------------------------------------------------------------------------------------------------
..   Third Part : GEOXYGENE
.. --------------------------------------------------------------------------------------------------------------

Importer le projet GeOxygene
*********************************

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
     
         .. figure:: /documentation/resources/img/geoxygene/Import_01.png
            :width: 350px
       
            Figure 1 : Import Project


   .. container:: rightside

      2. Puis sur : 
      
      .. container:: chemin

         Maven >> Checkout Maven Projects from SCM

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/geoxygene/Import_02.png
            :width: 350px
       
            Figure 2 : Check out Maven Projects from SCM


3. Ensuite comme l'indique la figure suivante, sélectionner **svn** dans la première liste comme SCM URL et indiquer l'adresse du svn de GeOxygene :

.. container:: centerside
     
   .. figure:: /documentation/resources/img/geoxygene/geoxygeneEtape3.png
       
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
     
         .. figure:: /documentation/resources/img/geoxygene/geoxygeneEtape4.png
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
***************

Lancer un maven build manuellement. Pour cela :


1. Dans le menu, cliquer sur 
      
  .. container:: chemin
      
     Run >> Run Configurations
    
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/geoxygene/geoxygeneRunEtape1.png
         :width: 600px
          
         Figure 6
       
2. Sélectionner comme type de run "Maven", puis cliquer dans le menu en haut sur "New launch configuration"
      
  .. container:: centerside
   
      .. figure:: /documentation/resources/img/geoxygene/geoxygeneRunEtape2.png
         :width: 350px
             
         Figure 7

3. Dans la nouvelle fenêtre "Run configuration" configurer :
         
   .. container:: field
   
      **Name** : geoxygene
         
      **Base directory** : saisir le chemin d'installation de GeOxygene 
                              (c'est celui de votre Workspace auquel il faut ajouter geoxygene)
         
      **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)
         
  
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/geoxygene/geoxygeneRunEtape3.png
         :width: 600px
             
         Figure 8


Si tout se passe bien, Maven devrait récupérer tous les jars des librairies nécessaires et compiler le projet. 

Cette opération peut prendre un certain temps !


Lancement des interfaces graphiques 
*****************************************

Si vous voulez utiliser l'interface graphique avec un rendu OpenGL, il reste une configuration à faire, décrite :ref:`ici <rendugeoxogl>`.

Sinon le guide de lancement des interfaces graphiques est décrit sur la : :ref:`page suivante <launchinggeox>`.







