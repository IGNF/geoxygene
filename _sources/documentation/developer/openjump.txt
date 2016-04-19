
Installation des plugins OpenJump GeOxygene
################################################

Les plugins GeOxygene pour OpenJump sont dans un module dédié et dans un projet différent de celui de GeOxygene.
Ils ne nécessitent pas forcément l'installation du projet GeOxygene ni du logiciel OpenJump.

Importer le projet geoxygene-ojplugin
****************************************
Suivez les mêmes étapes que l'import du projet GeOxygene. En revanche, les URLS du serveur SVN sont à choisir parmi celles-ci :

Si vous êtes enregistré sur `Sourceforge <http://sourceforge.net/>`_  et si vous avez des droits en tant que développeur ou administrateur sur le projet geoxygene : 

.. container:: svnurl
    
   https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-extension/geoxygene-ojplugin/ 

Sinon :

.. container:: svnurl
   
   http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-extension/geoxygene-ojplugin/


Compilation
**************
Suivez les mêmes étapes que la compilation du projet GeOxygene. Dans la nouvelle fenêtre "Run configuration" configurer :
         
  .. container:: field

     **Name** : geoxygene-ojplugin
        
     **Base directory** : saisir le chemin d'installation de geoxygene-ojplugin 
                              (c'est celui de votre Workspace auquel il faut ajouter geoxygene-ojplugin)
         
     **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)


Lancement des plugins Geoxygene d'OpenJump à partir d'Eclipse 
******************************************************************

Cette méthode ne nécessite pas d'avoir installé OpenJump sur sa machine.

1. Click droit dans l'explorateur d'Eclipse, puis "Run As", puis "Java Application"

2. Dans le premier onglet saisissez comme MainClass :

   .. container:: chemin

      com.vividsolutions.jump.workbench.JUMPWorkbench


.. container:: centerside

   .. figure:: /documentation/resources/img/launching/LancerOJEclipse01.png
      :width: 600px
      

3. Dans le second onglet, ajouter comme Program arguments de la ligne de commande :

   .. container:: chemin

      -properties ./src/main/resources/workbench-properties.xml
      -I18n fr

   
.. container:: centerside
   
   .. figure:: /documentation/resources/img/launching/LancerOJEclipse02.png
      :width: 600px
             

4. Pour lancer les plugins "quality", ceux-ci nécessitent la librairie java3d. Une version des dll est stockée dans le module "geoxygene-sig3d".

   Ajouter comme VM arguments de la ligne de commande :

   .. container:: chemin

      -Xmx1536M
      -Djava.library.path=D:/Workspace/geoxygene/geoxygene-sig3d/lib/native_libraries/windows-i586/


5. Clicker sur le bouton "Run" et openjump se lance, avec les plugins GeOxygene.



