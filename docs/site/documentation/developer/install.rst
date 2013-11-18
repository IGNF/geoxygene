:Date: 11/2013
:Version: 0.6

Developer’s Setup Guide
####################################

Cette page a pour objectif de guider le développeur dans son installation de la plateforme GeOxygene 
sous Windows ou sous Linux.
  
  
JAVA
*********

GeOxygene est un projet Open Source écrit en JAVA, il faut donc l'installation d'une **JDK** dont la version est au moins supérieure à la version 6. 
Il est possible de télécharger cet environnement sur le site de Sun à l'adresse suivante : `java.sun.com <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_


Eclipse installation
*********************************

L'environnement de développement utilisé est celui d'`Eclipse <http://www.eclipse.org/>`_, éditeur très largement utilisé
aujourd'hui pour les développements JAVA.


Installation
================

* Vous pouvez télécharger la dernière version d'Eclipse directement à partir du lien : `Eclipse Java EE IDE for Web Developers <http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr1>`_
  Actuellement la dernière version est Kepler (4.3)
   
* Décompresser le fichier téléchargé (Eclipse ne fournit pas d'installeur, juste un répertoire à dézipper).

* Editer le fichier eclipse.ini situé à la racine du répertoire d'Eclipse. Configurer Eclipse pour qu'il s'exécute avec une JDK et non une JRE. Pour ce faire, 
  rajouter les lignes suivantes au début du fichier :

   .. literalinclude:: /documentation/resources/code_src/eclipse.ini
           :language: xml

* Lancer Eclipse :
   
   .. container:: chemin
    
      **Windows** : eclipse.exe
  
      **Linux** : ./eclipse

* Lors du premier lancement, une boite de dialogue vous demandera de sélectionner le répertoire racine de vos projets Eclipse. 
  Soit vous sélectionnez celui proposé, auquel cas il sera créé ou, vous pouvez en choisir un personnalisé.


Eclipse preferences
========================

JDK
-------


.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/JavaConfiguration.png
       
       Figure 5 : Java configuration in Eclipse



Proxy 
---------------

Pour installer des mises à jour, de nouveaux plugins ou des extensions, il faut qu'Eclipse puisse accèder 
au net pour pouvoir les télécharger.  Si vous êtes derrière un proxy, il vous faut configurer Eclipse afin qu'il en tienne compte.

Pour ce faire, accéder au menu :

.. container:: chemin

   Window >> Preferences >> General >> Network Connection 

Vous pouvez alors séléctionner <Manual> comme <Active Provider>

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/proxy3.png
       
       Figure 1 : aaa
       

Selectionner "HTTP" dans la liste des entrées et cliquer sur le bouton "Edit"

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/proxy4.png
       
       Figure 2 : bbb
       

Entrer les coordonnées de votre proxy. Pour l'IGN par exemple, il s'agit de <proxy.ign.fr> avec le port <3128>. 
Ne remplisser pas les champs d'authentification.
  
.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/proxy1.png
       
       Figure 3 : ccc 


Encodage
-------------

Tous les modules de GeOxygene doivent être encodés en UTF-8.
Pour ce faire, dans Eclipse, aller dans :

.. container:: chemin

   Preferences >> Workspace >> Text file encoding >> Other

et choisir UTF-8.

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/encodage.png
       
       Figure 4 : ddd 


Code formatting
---------------------------

Parce qu'on passe plus de temps à lire du code qu’à en écrire, il faut configurer dans Eclipse la convention de programmation adoptée dans GeOxygene. 
Elle s'inspire avant tout de la convention de programmation recommandée pour tous les développements JAVA. 
Cette norme est dérivée de celle proposée par SUN à l’adresse :


    http://www.oracle.com/technetwork/java/codeconvtoc-136057.html


Aller dans "Preferences => Java => Code Style => Formatter" et cliquer sur "Import". 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/ConfigEclipseConventionCodage_1.png
       
       Figure 17 : -----


Le template de la convention de nommage du COGIT «java_cogit_formatting_conventions_v1.xml» se trouve dans le code source du projet GeOxygene :


geoxygene =>  src =>  main =>  resources =>  java_cogit_formatting_conventions_v1.xml





Importer ce fichier et choisissez comme "Active profile" : "Java COGIT Formatting Conventions v1" 


.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/ConfigEclipseConventionCodage_2.png
       
       Figure 18 : -----



Text editors
------------------


Eclipse Plugins
================

Maintenant, Eclipse est prêt pour l'installation des plugins nécessaires à GeOxygene. 
Vous aurez besoin des plugins Maven (m2eclipse) et subversion (subclipse). 

Plugin M2E
------------------
Sur les dernières versions d'Eclipse Java, le plugin m2eclipse est déjà installé.

Sinon : http://www.eclipse.org/m2e/download/


When downloading jars maven makes use of a "local repository" to store jars.

  ==================  ========================================================
     PLATFORM           LOCAL REPOSITORY
  ==================  ========================================================
     Windows XP:      :file:`C:\\Documents and Settings\\Jody\\.m2\\repository`
     Windows:         :file:`C:\\Users\\Jody\\.m2\repository`
     Linux and Mac:   :file:`~/.m2/repository`
  ==================  ========================================================

To download jars maven makes use of public maven repositories on the internet where projects
such as GeoTools publish their work.


Si vous êtes derrière un proxy, la dernière étape consiste à configurer Maven pour utiliser le proxy. 
Pour ce, il faut ajouter un fichier \emph{settings.xml} dans la racine de Maven. 
Ce répertoire est situé à l'endroit suivant :

    * sous Linux&\verb|~/.m2|\\
    * sous Windows&\verb|C:\Documents and Settings\%USER%\.m2|

Voici un exemplaire d'un fichier settings.xml que vous pouvez utiliser à l'IGN :

.. literalinclude:: /documentation/resources/code_src/settings.xml
        :language: xml
        

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/MavenConfiguration.png
       
       Figure 5 : eee


Plugin Subclipse
------------------


.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/connector_subclipse01.png
       
       Figure 5 : eee 

     
    .. figure:: /documentation/resources/img/install/connector_subclipse02.png
       
       Figure 6 : fff


    .. figure:: /documentation/resources/img/install/connector_subclipse03.png
       
       Figure 6 : ggg




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


* 1er cas : vous utilisez l'option de compilation automatique

.. container:: twocol

   .. container:: leftside

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/geoxygeneBuildAutomatically.png
            :width: 410px
       
            Figure 20 : Option build automatically


   .. container:: rightside

      Vous n'avez rien à faire, la compilation se lance automatiquement

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/geoxygeneAutoBuild.png
            :width: 500px
       
            Figure 21 : Maven build automatically


* 2ème cas : lancer un maven build manuellement.

Pour cela :


    1. Sélectionner dans l'explorateur à droite, le projet "geoxygene". Puis dans le menu, cliquer sur Run => Run Configurations.
      
      .. container:: centerside
     
          .. figure:: /documentation/resources/img/install/geoxygeneRunEtape1.png
       
    2. Sélectionner comme type de run "Maven", puis cliquer dans le menu en haut sur "New launch configuration"
      
      .. container:: centerside
     
          .. figure:: /documentation/resources/img/install/geoxygeneRunEtape2.png

    3. Dans la nouvelle fenêtre "Run configuration" configurer :
         
         **Name** : geoxygene
         **Base directory** : saisir le chemin d'installation de GeOxygene (c'est celui de votre Workspace auquel il faut ajouter geoxygene)
         **Goal** : clean install. Vous définissez la phase du cycle (clean, install, package, compile, test, site, ...)
         ** d'autres options peuvent être configurées dans cette interface si vous le souhaitez.
   
      .. container:: centerside
     
          .. figure:: /documentation/resources/img/install/geoxygeneRunEtape3.png




Si tout se passe bien, Maven devrait récupérer tous les jars nécessaires et compiler le projet. 


Lancement de l'interface graphique
========================================

Une application exemple peut être exécutée: 
**fr.ign.cogit.geoxygene.appli.GeOxygeneApplication**.



Pour cela ouvrir le fichier GeOxygeneApplication.java qui se trouve dans le module : 

   geoxygene-appli => src/main/java =>  fr.ign.cogit.geoxygene => appli





Dans la fenêtre de l'éditeur de la classe, faire un click droit de la souris. Puis cliquer sur :

"Run As" => "Java Application"


.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/GeOxygeneAppliRunAs.png
       
       Figure 15 : -----


L'interface de GeOxygene est lancée !












  