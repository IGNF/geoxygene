:Date: 12/2014
:Version: 0.6

Installation des outils pour GeOxygene
#######################################

Cette page a pour objectif de guider le développeur dans son installation de la plateforme de développement de GeOxygene.
  

.. note::

  A compter de ce jour (à partir de la version 1.9-SNAPSHOT), la version Java 1.8 est obligatoire.
  
  Eclipse en version >=Mars est recommandé avec les plugins subclipse (>1.10.8) et m2 (>1.5.0), sachant que le plugin m2 est déjà installé à partir d'Eclipse Mars.
  
  **Attention** : le connector subclipse pour Maven doit être installé à la main comme indiqué plus bas et non pas via le marketplace

  
JAVA
*********

GeOxygene est un projet Open Source écrit en JAVA, il faut donc l'installation d'un **JDK**.


Installation
================

#. Télécharger cet environnement sur le site de Sun à l'adresse suivante :

   http://www.oracle.com/technetwork/java/javase/downloads/index.html
   
#. Cliquer sur l'exécutable et accepter l'accord de licence. Par défaut l'installation s'est faites dans :
   
   .. container:: chemin
   
      C:\\Program Files\\Java\\jdk1.8.0\\


Certificate
================

Il faut définir certaines propriétés afin de pouvoir télécharger les librairies java depuis un serveur HTTPS. 

La référence de cette approche est détaillée ici : http://maven.apache.org/guides/mini/guide-repository-ssl.html.

1. Obtention du certificat 

Télécharger le certificat depuis votre navigateur comme ceci : 

.. container:: twocol

   .. container:: leftside


      1.1 Ouvrir votre navigateur et aller sur la page :

          .. container:: svnurl
    
             https://forge-cogit.ign.fr/nexus/#welcome

      1.2 Dans la barre de navigation, cliquer sur le cadenas

      1.3 Cliquer sur "More informations"

      1.4 Cliquer sur "Display certificate"

      1.5 Cliquer sur "détails"

      1.6 Cliquer sur "Export"

      1.7 Sauvegarder votre certificat sur votre disque dur.  
          Par exemple : **E:\\certificat\\forge-cogit.crt**
          
      
      .. warning::
         
         * Le nom du fichier doit être forge-cogit.crt (et non pas forge-cogit.ign.fr.crt)
         
         * Sous Linux, définissez le chemin en absolu (ne pas utiliser ~/certificat/forge-cogit.crt)
      

   .. container:: rightside
   
      .. container:: centerside
     
             .. figure:: /documentation/resources/img/maven/CertificatJava.png
                :width: 400px
       
                Figure 1 : Téléchargement du certificat


2. La ligne de commande suivante va importer le certificat d'autorité dans un fichier trust.jks.

   .. container:: chemin
 
         keytool -v -alias mavensrv -import -file E:\\certificat\\forge-cogit.crt -keystore trust.jks
   
   .. container:: centerside
   
     .. figure:: /documentation/resources/img/maven/ConfigurerCertificat.png 
        :width: 750px
       
        Figure 2 : Importer le certificat dans un trust store


3. Saisir un mot de passe, par exemple "leschiensaboient"

4. Accepter le certificat

  .. container:: centerside
   
     .. figure:: /documentation/resources/img/maven/AccepterCertificat.png 
        :width: 360px
       
        Figure 3 : Accepter le certificat


5. Les variables d'environnement vont être définies plus tard (Eclipse preferences >> JDK).

Refaire la même chose (sauvegarder le certificat depuis le navigateur, et l'importer dans le fichier trust.jks précédemment crée) avec 

.. container:: svnurl
    
	https://repo.maven.apache.org/

en changeant le nom de l'alias, par exemple :

   .. container:: chemin
 
         keytool -v -alias mavenHttps -import -file E:\\certificat\\repo.maven.apache.org.crt -keystore trust.jks

et en entrant le mot de passe précédemment défini (ici c'était "leschiensaboient")




Eclipse installation
*********************************

L'environnement de développement utilisé est celui d'`Eclipse <http://www.eclipse.org/>`_, éditeur très largement utilisé
aujourd'hui pour les développements JAVA.


Installation
================

* Vous pouvez télécharger Eclipse Mars(4.5.2), directement à partir des liens ci-dessous :  
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|`Windows 32-bit <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-java-mars-2-win32.zip>`_                     |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|`Windows 64-bit <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-java-mars-2-win32-x86_64.zip>`_              |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|`Mac OS X(Cocoa 64) <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-java-mars-2-macosx-cocoa-x86_64.tar.gz>`_|
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|`Linux 32-bit <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-java-mars-2-linux-gtk.tar.gz>`_                |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|`Linux 64-bit <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-java-mars-2-linux-gtk-x86_64.tar.gz>`_         |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------+

* Décompresser le fichier téléchargé (Eclipse ne fournit pas d'installeur, juste un répertoire à dézipper).

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

* Configurer Eclipse pour qu'il exécute les programmes java avec un jdk et non pas un jre :

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/JavaConfiguration.png
       :width: 600px
       
       Figure 4 : Java configuration in Eclipse


* Définir les variables d'environnement permettant de télécharger les librairies java depuis un serveur https.

Toujours dans :

.. container:: chemin

   Window >> Preferences >> Java >> Installed JRE >> 
    

Sélectionner le JDK utilisé par défaut et cliquer sur **Edit**. Ajouter la ligne suivante dans **Default VM arguments** en spécifiant bien le répertoire 
où vous avez créé le fichier trust.jks et en remplaçant *leschiensaboient* par votre mot de passe.

   .. container:: chemin

      -Djavax.net.ssl.trustStore=E:\\certificat\\trust.jks -Djavax.net.ssl.keyStorePassword=leschiensaboient


   .. container:: centerside
   
     .. figure:: /documentation/resources/img/maven/CertificatVariableEnvironnement.png 
        :width: 900px
       
        Figure 5 : Variables d'environnement 


Proxy 
---------------

Pour installer des mises à jour, de nouveaux plugins ou des extensions, il faut qu'Eclipse puisse accèder 
à internet pour pouvoir les télécharger.  Si vous êtes derrière un proxy, il vous faut configurer Eclipse afin qu'il en tienne compte.

Pour ce faire, accéder au menu :

.. container:: chemin

   Window >> Preferences >> General >> Network Connection 

Vous pouvez alors séléctionner **Manual** comme **Active Provider**

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/proxy3.png
       :width: 600px
       
       Figure 6 : Eclipse - Network connections
       

Selectionner "HTTP" dans la liste des entrées et cliquer sur le bouton "Edit". 

Entrer les coordonnées de votre proxy. 
Pour l'IGN par exemple, il s'agit de **proxy.ign.fr** avec le port **3128**. 
Ne remplisser pas les champs d'authentification.

Faire la même chose pour "HTTPS" comme indiqué sur la figure 7.
  
.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/proxy1.png
       
       Figure 7 : Edit proxy entry 


Encodage
-------------

Tous les modules de GeOxygene doivent être encodés en UTF-8.
Pour ce faire, dans Eclipse, aller dans :

.. container:: chemin

   Preferences >> General >> Workspace >> Text file encoding >> Other

et choisir UTF-8.

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/encodage.png
       :width: 450px
       
       Figure 8 : Eclipse - Encodage 


Code formatting
---------------------------

Parce qu'on passe plus de temps à lire du code qu’à en écrire, il faut configurer dans Eclipse la convention de programmation adoptée dans GeOxygene. 
Elle s'inspire avant tout de la convention de programmation recommandée pour tous les développements JAVA. 
Cette norme est dérivée de celle proposée par SUN à l’adresse :


1. Télécharger la norme du COGIT :
    
.. container:: svnurl

   https://github.com/IGNF/geoxygene/blob/master/src/main/resources/java_cogit_formatting_conventions_v1.xml
   
   
.. container:: twocol

   .. container:: leftside
   
      2. Aller dans :

         .. container:: chemin

            Preferences >> Java 
                        >> Code Style >> Formatter 
   
         et cliquer sur "Import" :

         .. container:: centerside
     
            .. figure:: /documentation/resources/img/install/ConfigEclipseConventionCodage_1.png
               :width: 400px
       
               Figure 9 : Convention de codage - Import
    
   
   .. container:: rightside
   
      3. Importer ce fichier et choisissez comme "Active profile" : 
                     "Java COGIT Formatting Conventions v1" 

      .. container:: centerside
     
         .. figure:: /documentation/resources/img/install/ConfigEclipseConventionCodage_2.png
            :width: 500px
       
            Figure 10 : Convention de codage - Active profile 


Text editors
------------------

.. container:: twocol

   .. container:: leftside


        1. Aller dans :
        
        .. container:: chemin
         
           Préférences >> General >> Editors >> Text Editors
           
        2. Cocher :
        
           * Insert spaces for tabs
        
           * Check Show line numbers
        
           .. * Check Show whitespace characters (optional)
        
           .. * Check Show print margin and set Print margin column to “100” (optional)
   
   .. container:: rightside
   
      .. container:: centerside
   
           .. figure:: /documentation/resources/img/install/PreferencesTextEditor.png
              :width: 450px
               
              Figure 11 : Eclipse preferences - Text editors  


.. ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????
.. ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????


Eclipse Plugins
*********************************

Maintenant, Eclipse est prêt pour l'installation des plugins nécessaires à GeOxygene.
 
Vous aurez besoin essentiellement des plugins EGit, Subversion (subclipse) et Maven (m2eclipse) ainsi que de ses connecteurs pour git et svn.

À partir d'Eclipse Mars, il ne reste qu'à installer éventuellement Subclipse (si on accès au SVN privé du COGIT), ainsi que les connecteurs m2e pour Subclipse et EGit.


Connector EGit for Maven
========================================

Pour installer le connector EGit on pourra faire dans le menu d'Eclipse

.. container:: chemin

	File >> Import

.. container:: twocol

	.. container:: leftside
	
	   Puis dans la fenêtre qui s'ouvre
	
	   .. container:: chemin
		
	      Maven >> Check out Maven Projects from SCM 
	   Faire "Next"
	   
	.. container:: rightside     
	       
	   .. container:: centerside
		      
	      .. figure:: /documentation/resources/img/install/fileImport.png
	         :width: 350px
		      
	         Figure 25 : Import 
	      
.. container:: twocol

	.. container:: leftside
	
	   Cliquer sur "Find more SCM connectors in the m2e Marketplace"
	  
	.. container:: rightside     
	       
	   .. container:: centerside
		      
	      .. figure:: /documentation/resources/img/install/m2eMarketplace.png
	         :width: 350px
		      
	         Figure 26 : m2e Marketplace 

.. container:: twocol

	.. container:: leftside
	
	   Chercher dans **m2e Team providers** le connecteur **m2e-egit** et le sélectionner, puis faire "Finish"
	  
	.. container:: rightside     
	       
	   .. container:: centerside
		      
	      .. figure:: /documentation/resources/img/install/m2e-egit.png
	         :width: 350px
		      
	         Figure 27 : m2e-egit connector

.. container:: centerside

	Cliquer sur **OK**, cocher le composant à installer et finir l'installation comme habituellement (securité, licenses, restart Eclipse...).


**Si SVN n'est pas utilisé, on pourra passer à la partie suivante, Maven Preferences**
 
Plugin Subclipse (optionnel pour la partie publique de GeOxygene)
======================

Subclipse est un plugin Eclipse permettant d'utiliser Subversion (SVN) directement depuis votre éditeur préféré.

.. container:: twocol

   .. container:: leftside

      **Etape 1** : Cliquer dans le menu d'Eclipse :

	     .. container:: chemin
	
	           Help >> Eclipse Marketplace ...
	           
	     1. dans la zone de recherche, saisir **subclipse**
	     
	     2. clicker sur **go** afin de rechercher le plugin
	     
	     3. dans la liste des plugins, clicker sur **install** du plugin Subclipse 1.10.13 (au moment de  l'écriture de cette doc)
   
   .. container:: rightside     
       
       .. container:: centerside
	      
	      .. figure:: /documentation/resources/img/install/LunaSubclipse.png
	         :width: 350px
	      
	         Figure 12 : Subclipse 1.10.8 in Marketplace 
         
.. container:: twocol

   .. container:: leftside
            
      **Etape 2** : Confirmer la sélection des composants à installer.

   .. container:: rightside     
   
      .. container:: centerside
      
         .. figure:: /documentation/resources/img/install/LunaSubclipse_02.png
            :width: 500px
       
            Figure 13 : Confirm Selected Features  

.. container:: twocol

   .. container:: leftside

      **Etape 3** : Accepter les termes de la licence de subeclipse sur la page "Review Licences" et cliquer sur "Finish" pour commencer l'installation.
   
   .. container:: rightside
      
      .. container:: centerside
      
         .. figure:: /documentation/resources/img/install/LunaSubclipse_03.png
            :width: 450px
         
            Figure 14 : Review Licenses
   
.. container:: twocol
   
   .. container:: leftside
   
      **Etape 4** : Vous allez recevoir un message d'alerte "Security Warning" parce que les jars de subeclipse 
      ne sont pas signés. Cliquer sur "OK" pour continuer l'installation.
   
   .. container:: rightside
      
      .. container:: centerside
      
         .. figure:: /documentation/resources/img/install/LunaSubclipse_04.png
            :width: 350px
         
            Figure 15 : Security warning
         

.. container:: twocol

   .. container:: leftside
   
      **Etape 5** : Une fois l'installation terminée, préférer "Restart Now" dans la prochaine boite de dialogue. 
      
      Le plugin subeclipse sera opérationnel après le redémarrage d'Eclipse.
   
   .. container:: rightside
      
      .. container:: centerside
      
         .. figure:: /documentation/resources/img/install/pluginRestart.png
            :width: 350px

            Figure 16 : Restart

.. container:: twocol

   .. container:: leftside
   
      **Etape 6** : Une fois le plugin installé, configurer dans Eclipse l'interface **SVNKit**. 
      En effet, celle-ci semble mieux fonctionner que celle par défaut (JavaHL). 
      Pour cela, dans le menu d'Eclipse :
      
      .. container:: chemin
      
         Window >> Preferences >> Team >> SVN
      
      sélectionner l’interface SVNKit.
   
   .. container:: rightside
      
      .. container:: centerside
      
         .. figure:: /documentation/resources/img/install/subeclipseInterface.png
            :width: 450px

            Figure 17 : SVN interface



Connector Subclipse for Maven (optionnel pour la partie publique de GeOxygene)
========================================

Il faut installer le connector subclipse pour Maven si on veut pouvoir importer directement le code du SVN privé du COGIT depuis Eclipse. 

**Attention** : le connector n'est pas à jour dans le catalogue du Marketplace d'Eclipse.


#. Télécharger le fichier suivant et décompresser-le dans un répertoire sur votre disque local.
      
   .. container:: chemin

      http://ignf.github.io/geoxygene/m2e-subclipse/org.sonatype.m2e.subclipse.feature-0.13.0-SNAPSHOT-site.zip
         
      
#. Dans Eclipse :
   
   .. container:: twocol

	   .. container:: leftside
	
	      Dans le menu d'Eclipse, aller à :
	
		     .. container:: chemin
		
		           Help >> Install New Software
	   
	   .. container:: rightside     
	       
	       .. container:: centerside
		      
		      .. figure:: /documentation/resources/img/install/pluginInstall.png
		         :width: 350px
		      
		         Figure 22 : Install new Software 
   
   
  
   .. container:: twocol

	   .. container:: leftside
	            
	      Cliquer sur **Add ...** 
	
	   .. container:: rightside     
	   
	      .. container:: centerside
	      
	         .. figure:: /documentation/resources/img/install/pluginNewUrl.png
	            :width: 500px
	       
	            Figure 23 : Add new update site 


   .. container:: twocol

	   .. container:: leftside
	            
	      Cliquer dans la boite de dialogue sur **Local ...** et choisir le répertoire où a été dézippé le fichier téléchargé précédement.  
	      
	   .. container:: rightside     
	   
	      .. container:: centerside
	      
	         .. figure:: /documentation/resources/img/install/ConnectorM2eSubclipse.png
	            :width: 500px
	       
	            Figure 24 : Add new local site
	            
        
   .. container:: centerside

	  Cliquer sur **OK**, cocher le composant à installer et finir l'installation comme habituellement (securité, licenses, ...).
	






Maven preferences
********************

Eclipse Preferences for Maven
=================================

Afin de voir la javadoc ou les sources des dépendances de vos projets, sélectionner :

* Download Artifact Sources

* Download Artifact Javadoc

.. container:: centerside
     
    .. figure:: /documentation/resources/img/install/MavenConfiguration.png
       :width: 500px
       
       Figure 29 : Eclipse preferences pour Maven


Tout est en place pour l'installation de GeOxygene.


Settings
============

Si vous êtes derrière un proxy, la dernière étape consiste à configurer Maven pour utiliser le proxy. 
Pour ce, il faut ajouter un fichier **settings.xml** à la racine de Maven (même niveau que le repository maven). 
Ce répertoire est situé à l'endroit suivant :

  ==================  ========================================================
     PLATFORM           LOCAL REPOSITORY
  ==================  ========================================================
     Windows XP:      :file:`C:\\Documents and Settings\\Augusta\\.m2\\`
     Windows:         :file:`C:\\Users\\Augusta\\.m2\\`
     Linux and Mac:   :file:`~/.m2/`
  ==================  ========================================================


Voici un exemplaire d'un fichier settings.xml que vous pouvez utiliser à l'IGN :

.. literalinclude:: /documentation/resources/code_src/settings.xml
        :language: xml





  
