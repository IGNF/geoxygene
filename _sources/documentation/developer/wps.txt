

Installation de l'extension WPS GeOxygene
########################################### 

Les Web Processing Services de GeOxygene sont des extensions de `GeoServer <http://geoserver.org/>`_ . 
Plus précisément ce sont des librairies GeOxygene (mises sous forme de java archive) que l'on va déposer dans le répertoire d'installation de GeoServer. 

Les extensions WPS GeOxygene sont implémentées dans un module dédié **geoxygene-wps** et versionnées dans un dépôt différent du noyau de GeOxygene.



L'environnement de développement des extensions WPS GeOxygene nécessite :

* l'installation du projet GeOxygene.

* l'installation de GeoServer et de son extension WPS.


Installation de GeoServer et de l'extension WPS
************************************************

Suivre les instructions pour l'installation de

1. GeoServer ici :

   .. container:: chemin

      http://geoserver.geo-solutions.it/edu/en/install_run/gs_install.html

  
2. l'extension WPS de GeoServer ici :

   .. container:: chemin

      http://geoserver.geo-solutions.it/edu/en/install_run/gs_extensions.html



Importer le projet geoxygene-wps
************************************
Suivre les mêmes étapes que l'import du projet GeOxygene. 


En revanche, les URLS du serveur SVN sont à choisir parmi celles-ci. 
Si vous êtes enregistré sur `Sourceforge <http://sourceforge.net/>`_  et si vous avez des droits en tant que développeur  
sur le projet geoxygene : 

.. container:: svnurl
    
   https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-extension/geoxygene-wps/ 

Sinon :

.. container:: svnurl
   
   http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-extension/geoxygene-wps/
   

Compilation du module geoxygene-wps
************************************

1. Stopper le serveur GeoServer.

2. Avant de compiler le module *geoxygene-wps*, modifier les variables *path.geoxygene.noyau* et *path.geoserver* dans le fichier **pom.xml** du module **geoxygene-wps** :

.. literalinclude:: /documentation/resources/code_src/wps/pom.xml
        :language: xml
        

* La variable *path.geoxygene.noyau* doit contenir le chemin d'installation du noyau de GeOxygene

* La variable *path.geoserver* doit contenir le chemin d'installation de GeoServer.

       
3. Puis compiler le module *geoxygene* si ce n'est pas déjà fait, afin de créer tous les jars nécessaires.

   .. container:: chemin
   
      mvn install

4. Compiler ensuite le module *geoxygene-wps* en spécifiant le profil **geoserver** comme présenté ici :
         
.. container:: centerside

	.. figure:: /documentation/resources/img/wps/MavenGeoxWps.png
		:width: 700px
		       
		Figure 1 : GeOxygene-Wps Compile

5. Lancer GeoServer. Les services GeOxygene sont prêts ! 


Tester les services GeOxygene WPS dans GeoServer  
**************************************************

L'interface web de GeoServer permet, grâce au **Demo Builder**, de lancer très facilement des requêtes aux WPS services.  

1. Aller via votre navigateur favori, sur la page du *Demo Builder* :

   .. container:: chemin
   
      http://localhost:8083/geoserver/web/?wicket:bookmarkablePage=:org.geoserver.wps.web.WPSRequestBuilder. 
      
   
   Cette page est aussi accessible en passant par les liens :
   
   .. container:: chemin

      Demos > WPS Request Builder 


   .. container:: centerside
   
      .. figure:: /documentation/resources/img/wps/GeoServerDemoBuilder.png
		 :width: 800px
		       
		 Figure 2 : Page d'accueil du GeoServer Demo Builder
   
   
2. Les WPS GeOxygene sont accessibles ! 

   .. container:: centerside

	  .. figure:: /documentation/resources/img/wps/GeOxygeneProcess.png
		  :width: 600px
		       
		  Figure 3 : WPS GeOxygene
		