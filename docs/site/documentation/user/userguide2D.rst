
User Guide 2D
================

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/ScrennshotGeoxygene1.png
       :width: 500px
 
       Figure 1 : Interface graphique Geoxygene 2D


Pour réaliser les premiers tests appelée JDD (il s'agit ainsi de fixer les données et la légende associée)


Chargement d'un jeu de données géographiques
------------------------------------------------

.. container:: chemin

   Fichier >> Ouvrir un fichier

Une fenêtre s'ouvre, sélectionnez, par exemple les fichiers fournis dans le SVN/data. 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/ouvrir.png
       :width: 400px
    
       Figure 2 : Ouvrir une couche de données (format Shapefile)

Pour l'instant, on ne peut charger les couches qu'une à une.

Les couches s'affichent dans l'ordre de leur sélection. 
Le bloc à gauche de l'interface cartographique fournit un gestionnaire de couches (Cf. Figure \ref{fig:gestion}) : 
on sélectionne une couche en cliquant dessus (sur la Figure \ref{fig:gestion} la couche Route est sélectionnée), 
on peut ensuite modifier différentes propriétés de cette couche : la rendre sélectionnable 
(= pouvoir sélectionner les objets dans l'interface carto ou dans la table attributaire), 
modifiable (=éditer, modifier les objets), gérer son niveau de transparence, 
modifier son style et son nom. Les flèches en haut de ce bloc permettent d'ordonner 
correctement les couches, afin qu'elles soient visibles. 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/gestion_couches.png
       :width: 250px
    
       Figure 3 : Gestionnaire de couches - *ProjectFrame* et *LayerLegendPanel*



Pour visualiser les données attributaires d'une couche, on clique sur le "i" Information : 
on accède à l'ensemble des couches et à leurs tables attributaires (Cf. Figure \ref{fig:table}). 
Il est possible d'éditer les attributs (si la couche correspondante est rendue modifiable), 
on peut sélectionner un objet et faire zoomer dessus, ou faire afficher uniquement des objets sélectionnés.

.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/table.png
       :width: 700px
    
       Figure 4 : Tables attributaires - *AttributeTable*


Une fois les couches chargées et superposées dans le bon ordre pour être visualisées, 
on peut modifier leur symbolisation par défaut : il suffit de cliquer sur le carré de la colonne Styles 
devant le nom de la couche à représenter (Cf. Figure \ref{fig:style}). Le premier onglet concerne la 
symbologie de la couche ; un deuxième onglet permet de gérer l'affichage des toponymes.


.. container:: centerside
     
    .. figure:: /documentation/resources/img/userguide/style.png
       :width: 200px
    .. figure:: /documentation/resources/img/userguide/toponymes.png
       :width: 200px
       
       Figure 5 : Modifier le style de la couche Commune  (onglet symbologie et toponymes) - *StyleEditionFrame*


On peut obtenir la visualisation suivante Cf. Figure \ref{fig:affichage}.

.. container:: centerside

	.. figure:: /documentation/resources/img/userguide/affichage_data.png
	       :width: 700px
	    
	       Figure 6 : Interface de GeOxygene
       


Enregistrer
------------------
On peut sauver la visualisation sous la forme d'une image PNG :

.. container:: chemin

	Fichier >> Sauver comme image

.. container:: centerside

	.. figure:: /documentation/resources/img/userguide/ImageRecordGeOxygene.png
	       :width: 700px
	    
	       Figure 7 : Image de la visu



   
