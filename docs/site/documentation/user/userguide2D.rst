.. _userguide2D:

Prise en main de l'interface graphique 2D
==========================================

.. .. container:: centerside
     
..    .. figure:: /documentation/resources/img/userguide/ScrennshotGeoxygene1.png
..       :width: 500px
 
..       Figure 1 : Interface graphique Geoxygene 2D




Chargement d'un jeu de données géographiques shape
----------------------------------------------------

Il existe 2 méthodes pour charger des fichiers shapes dans l'interface :

1. En sélectionnant les fichiers sur votre système
   
   .. container:: chemin
          
      Fichier >> Ouvrir un fichier

   Une fenêtre s'ouvre, sélectionnez des fichiers sur votre système. Par exemple vous pouvez prendre les fichiers fournis 
   dans le dépôt GeOxygene-data (:ref:`jeu de données <datadirectory>`). 
    
       
   .. container:: centerside
     
      .. figure:: /documentation/resources/img/userguide/ouvrir.png
         :width: 400px
    
         Figure 1 : Ouvrir une couche de données (format Shapefile)
     
2. En les pré-chargeant au démarrage du lancement de l'interface.
      
   Modifier le fichier **geoxygene-configuration.xml** à la racine du repertoire d'installation de GeOxygene et ajouter sous les **plugins** :
       
   .. literalinclude:: /documentation/resources/code_src/userguide/preload.xml
        :language: xml

   où **D:\\ign-echantillon** est le répertoire où vous avez téléchargé les fichiers (:ref:`jeu de données <datadirectory>`).


Les couches s'affichent dans l'ordre de leur sélection. 

.. container:: centerside
     
   .. figure:: /documentation/resources/img/userguide/affichage_data.png
      :width: 700px
       
      Figure 2 : Interface de GeOxygene


Gestionnaire de couches
------------------------

Le bloc à gauche de l'interface cartographique fournit un gestionnaire de couches (Cf. Figure ci-dessous) : 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/IMG05.png
       :width: 250px
    
       Figure 3 : Gestionnaire de couches - *LayerLegendPanel*

On sélectionne une couche en cliquant dessus, on peut ensuite modifier différentes propriétés de cette couche : 

- la rendre sélectionnable (= pouvoir sélectionner les objets dans l'interface carto ou dans la table attributaire), 
- modifiable (=éditer, modifier les objets), 
- gérer son niveau de transparence, 
- modifier son style et son nom. 

Les flèches flèches en haut de ce bloc permettent d'ordonner correctement les couches, afin qu'elles soient visibles. 

Pour visualiser les données attributaires d'une couche, on clique sur la l'icone *table* (dernière à droite du bloc). 
On accède à l'ensemble des couches et à leurs tables attributaires : 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/table.png
       :width: 700px
    
       Figure 4 : Tables attributaires - *AttributeTable*

Il est possible d'éditer les attributs (si la couche correspondante est rendue modifiable), 
on peut sélectionner un objet et faire zoomer dessus, ou faire afficher uniquement des objets sélectionnés.


Style
-------

Une fois les couches chargées et superposées dans le bon ordre pour être visualisées, 

1. on peut modifier leur symbolisation par défaut : il suffit de cliquer sur le carré de la colonne Styles 
   devant le nom de la couche à représenter. Le premier onglet concerne la symbologie de la couche; 
   un deuxième onglet permet de gérer l'affichage des toponymes.


   .. container:: twocol

      .. container:: leftside
   
         .. container:: centerside
     
            .. figure:: /documentation/resources/img/userguide/IMG02.png
               :width: 400px
   
   
      .. container:: rightside
   
         .. container:: centerside
    
            .. figure:: /documentation/resources/img/userguide/IMG04.png
               :width: 400px
       
   .. container:: centerside

      Figure 5 : Modifier le style de la couche Commune  (onglet symbologie et toponymes) - *StyleEditionFrame*


2. Fichier SLD

   On peut modifier le style directement par un fichier SLD (dans le menu ou 3ème icone dans le bloc *LayerLegendPanel*
       
   Pour le jeu de données *échantillon BD TOPO® IGN* vous pouvez essayer le style suivant :

   .. container:: chemin

      https://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene/geoxygene-appli/src/main/resources/sld/style_topo_basique.xml


   .. container:: centerside

    .. figure:: /documentation/resources/img/userguide/IMG01.png
           :width: 700px
        
           Figure 6 : Style très basique IGN dans l'interface GeOxygene 2D 


Le style peut être sauvegardé en cliquant dans le menu *Exporter SLD*  


Sauvegarde
------------
On peut sauver la visualisation sous la forme d'une image PNG :

.. container:: chemin

	Fichier >> Sauver comme image

.. container:: centerside

	.. figure:: /documentation/resources/img/userguide/ImageRecordGeOxygene.png
	       :width: 700px
	    
	       Figure 7 : Image de la carte sauvegardée



Chargement d'un jeu de données OSM
------------------------------------

A venir, sinon le guide du développeur est accessible :ref:`ici <osm>`


