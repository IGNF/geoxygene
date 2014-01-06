
User Guide
===============

Chargement d'un jeu de données géographiques
------------------------------------------------

Fichier -> Ouvrir un fichier : 
une fenêtre s'ouvre, sélectionnez, par exemple les fichiers fournis dans le SVN/data. 
Pour info, le format Shapefile est associé à quatre fichiers (.shp, .shx, .dbf, .prj) 
fonctionnant ensemble (Cf. Figure \ref{fig:ouvrir}). Pour l'instant, on ne peut charger les couches qu'une à une.


\textbf{NB : Sur le SVN, il y a un jeu de données complet appelé Plancoët\_SHP et une sélection suffisante à utiliser pour réaliser les premiers tests appelée JDD (il s'agit ainsi de fixer les données et la légende associée)
}
\begin{figure}[htbp]
	\centering
		\includegraphics[width=10cm]{images/ouvrir2.png}
	\caption{Ouvrir une couche de données (format Shapefile)}
	\label{fig:ouvrir}
\end{figure}

Les couches s'affichent dans l'ordre de leur sélection. Le bloc à gauche de l'interface cartographique fournit un gestionnaire de couches (Cf. Figure \ref{fig:gestion}) : on sélectionne une couche en cliquant dessus (sur la Figure \ref{fig:gestion} la couche Route est sélectionnée), on peut ensuite modifier différentes propriétés de cette couche : la rendre sélectionnable (= pouvoir sélectionner les objets dans l'interface carto ou dans la table attributaire), modifiable (=éditer, modifier les objets), gérer son niveau de transparence, modifier son style et son nom. Les flèches en haut de ce bloc permettent d'ordonner correctement les couches, afin qu'elles soient visibles. 

\begin{figure}[htbp]
	\centering
		\includegraphics[width=5cm]{images/gestion_couches.png}
	\caption{Gestionnaire de couches - \textit{ProjectFrame} et \textit{LayerLegendPanel}}
	\label{fig:gestion}
\end{figure}

Pour visualiser les données attributaires d'une couche, on clique sur le "i" Information : on accède à l'ensemble des couches et à leurs tables attributaires (Cf. Figure \ref{fig:table}). Il est possible d'éditer les attributs (si la couche correspondante est rendue modifiable), on peut sélectionner un objet et faire zoomer dessus, ou faire afficher uniquement des objets sélectionnés.

\begin{figure}[htbp]
	\centering
		\includegraphics[width=10cm]{images/table.png}
	\caption{Tables attributaires - \textit{AttributeTable}}
	\label{fig:table}
\end{figure}

 Une fois les couches chargées et superposées dans le bon ordre pour être visualisées, on peut modifier leur symbolisation par défaut : il suffit de cliquer sur le carré de la colonne Styles devant le nom de la couche à représenter (Cf. Figure \ref{fig:style}). Le premier onglet concerne la symbologie de la couche ; un deuxième onglet permet de gérer l'affichage des toponymes.
\begin{figure}[htbp]
	\includegraphics[width=7cm]{images/style.png}
	\includegraphics[width=5cm]{images/toponymes.png}
	\caption{Modifier le style de la couche Commune  (onglet symbologie et toponymes) - \textit{StyleEditionFrame}}
	\label{fig:style}
\end{figure}


 On peut obtenir la visualisation suivante Cf. Figure \ref{fig:affichage}.
\begin{figure}[htbp]
	\centering
		\includegraphics[width=8cm]{images/affichage_data.png}
	\caption{Interface de GeOxygène}
	\label{fig:affichage}
\end{figure}

Enregistrer
------------------
On peut sauver la visualisation sous la forme d'une image PNG :

 \textit{Fichier -> Sauver comme image}

\begin{figure}[htbp]
	\centering
		\includegraphics[width=8cm]{images/test.png}
	\caption{Image de la visu}
	\label{fig:image}
\end{figure}

   
