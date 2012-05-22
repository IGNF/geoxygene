Package dédié aux schémas conceptuels.
Le modèle utilisé est celui des normes ISO. Les interfaces correspondantes sont dans le
package interfacesISO.

Deux implémentations sont proposées : une pour les schémas de jeu 
(notion de ApplicationSchema dans ISO) et une pour les schémas conceptuels
(notion de feature catalogue dans ISO).

Le package d'utilitaires contient des outils pour la persistence des schémas, 
pour leur visualisation sous forme de diagramme (à completer) et pour leur 
exploration dans de simples browser swing (à completer).


Produits et leurs métadonnées.
S'il s'agit d'une base de données vecteur, un produit est notamment décrit par 
un schema conceptuel de produit.

Un produit est identifié par un entier (classique et pratique pour l'acès par OJB)
et par une chaine de caractères unique de la forme PRODUIT_EE_RR où EE est le numéro 
d'édition, RR, le numéro de version de l'édition. Cet identifiant est aussi utilisé au 
SIEL.