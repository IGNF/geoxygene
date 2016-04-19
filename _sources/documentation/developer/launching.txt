.. _launchinggeox:

Lancement des interfaces graphiques
#######################################

Lancement de l'interface graphique 2D depuis Eclipse 
*****************************************************

Fichier de configuration des plugins
=======================================

Créer un fichier *geoxygene-configuration.xml* depuis le fichier template *geoxygene-appli/conf/geoxygene-configuration-template.xml* 
et placez-le à la racine du module *geoxygene-appli* :

   .. container:: chemin
        
      E:\\Workspace\\GeOxygene >> copy geoxygene-appli/conf/geoxygene-configuration-template.xml geoxygene-appli/geoxygene-configuration.xml


Ce fichier contient l'ensemble des plugins lancés au démarrage de l'application. Pour en ajouter ou supprimer il suffit de modifier ce nouveau fichier.

   .. literalinclude:: /documentation/resources/code_src/geoxygene-configuration.xml
           :language: xml


Lancement
===========

1. Dans le menu, cliquer sur 
      
   .. container:: chemin
      
      Run >> Run Configurations


2. Sélectionner comme type de run "Java Application", puis faire un click droit et sélectionner "New"

3. Configurer les éléments suivants :

   3.1 Dans la partie "Main"

   .. container:: field
     
      **Name** : GeOxygeneApplication

      **Project** : geoxygene-appli

      **Main class** : fr.ign.cogit.geoxygene.appli.GeOxygeneApplication


   .. container:: centerside
      
      .. figure:: /documentation/resources/img/launching/GeOxygeneAppliRunAs.png
         :width: 700px
       
         Figure 1 - Lancement de l'interface graphique 2D


  3.2 Dans la partie "Arguments", pour la machine virtuelle :
 
  .. container:: field
  
     **VM arguments** : -Djava.library.path=dll\win64 -Xms512M -Xmx1G
     
  où *dll\win64* définit l'emplacement où sont stockées vos librairies logicielles système (*.DLL, *.SO)


  .. container:: centerside
     
      .. figure:: /documentation/resources/img/launching/GeOxygeneAppliRunAs02.png
         :width: 700px
       
         Figure 2 - Lancement de l'interface graphique 2D


4. Cliquer sur **Run**, l'interface de GeOxygene 2D est lancée !


.. container:: centerside
     
    .. figure:: /documentation/resources/img/launching/GeOxygene2D.png
       :width: 700px
       
       Figure 3 - Interface graphique GeOxygene 2D 



Lancement de l'interface graphique 3D
***************************************

1. Dans le menu, cliquer sur 
      
   .. container:: chemin
      
      Run >> Run Configurations


2. Sélectionner comme type de run "Java Application", puis faire un click droit et sélectionner "New"


3. Configurer les éléments suivants :

   3.1 Dans la partie "Main"

   .. container:: field
     
      **Name** : GeOxygeneApplication3D

      **Project** : geoxygene-sig3d

      **Main class** : fr.ign.cogit.geoxygene.sig3d.gui.MainWindow


   .. container:: centerside
      
      .. figure:: /documentation/resources/img/launching/GeOxygene3DAppliRunAs.png
         :width: 700px
       
         Figure 4 - Lancement de l'interface graphique 3D


  3.2 Dans la partie "Arguments", pour la machine virtuelle :
 
  .. container:: field
  
     **VM arguments** : 
         
         **Win (32bits)** : 
               -Xms1156m -Xmx1156m -XX:PermSize=256m -XX:MaxPermSize=256m -Djava.library.path=./lib/native_libraries/windows-i586/
         
         **Win (64bits)** : 
               -Xms1156m -Xmx1156m -XX:PermSize=256m -XX:MaxPermSize=256m -Djava.library.path=./lib/native_libraries/windows-amd64/
     
  .. container:: centerside
     
      .. figure:: /documentation/resources/img/launching/GeOxygene3DAppliRunAs02.png
         :width: 700px
       
         Figure 5 - Lancement de l'interface graphique 3D


4. Cliquer sur **Run**, l'interface de GeOxygene 3D est lancée !


.. container:: centerside
     
    .. figure:: /documentation/resources/img/launching/GeOxygene3D.png
       :width: 600px
       
       Figure 6 - Interface graphique GeOxygene 3D




