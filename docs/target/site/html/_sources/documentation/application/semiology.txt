.. _semiology:

:Author: Charlotte Hoarau
:Version: 0.4
:License: Create Commons with attribution

Semiology Tools
================================

 A documentation is available concerning these tools and application for users and developers, 
 :download:`here </documentation/resources/doc/geoxygene-semiology-0.4.pdf>`,  in PDF file format.
  
  
A model of legend
------------------------
  
 A base model is proposed to represent the legend of a map as a tree. This model 
 describes symbolization and allows manipulating map features symbolised with 
 those symbols. More originally, this model describes metadata on the legend, 
 such as semantic relationships between map layers (order, association or 
 difference) related to Bertin’s work [B67].

 In the proposed framework (see Figure 1), the legend is thought of as a mediator 
 between what has to be represented (which features?), how they are represented 
 (which signs?), and what is the intended/perceived relations between represented 
 features (which message?).
 
 .. container:: twocol

   .. container:: leftside

      .. figure:: /documentation/resources/img/semiology/modele-legende-overview.png
      
         Figure 1 - Overview of the model

   .. container:: rightside

      This theoretical framework is organized in four main packages: 
      
      * The main package is legendContent: it describes which components are contained
        in the map legend of the map (names and symbol choices of each line of the key),
        and how they are organized (which lines are gathered  in a theme). 
      
      * The package mapContent describes, for a given map, the set of features that are
        to be represented (features organized in feature collections).
      
      * The package symbol describes the symbols used to draw features on the map (color, size…).
      
      * The package metadata describes messages to be conveyed by the map (concretely
        restricted to relations between set of symbolised features for the time being,
        like ‘association’, ‘difference’ or ‘order’). It could be extended after time
        (Cf. paragraph 1.6 Foreseen extensions).
      
 
.. container:: centerside

    Main elements of the model are described in Figure 2 :

        .. figure:: /documentation/resources/img/semiology/modele-legende-detaille.png
       
           Figure 2 : Main classes of the model


Chromatic Wheels and Color Contrasts
-------------------------------------------
 
 A color Reference system have been designed at COGIT laboratory, dedicated to 
 map design. User tests have been proceed to evaluate hue and lightness contrasts 
 between the colors of this color reference system.
 
 .. container:: centerside
     
    .. figure:: /documentation/resources/img/semiology/cercles-geoxygene.png
       
       Figure 3 : COGIT Chromatic Wheels
 

Application : Automatic Contrast Improvement of a map
----------------------------------------------------------

The goal of the contrast improvement process is to modify colors of a given 
legend in order to improve contrasts between objects on a given map. The global 
idea is that two features close enough on the map should have sufficiently different 
colors to be distinguished. Another idea guiding the process is that relations between 
colors (‘same hue but darker’, ‘same lightness but different hue’…) should be consistent 
with relations between legend leaves that the map may convey (e.g. : colors of phenomena 
‘low risk’, ‘medium risk’ and ‘high risk’ should have similar hue but different lightness 
to express an order relationship).

Documentation
----------------------
 
A documentation is available concerning these tools and application for users and developers, 
:download:`here </documentation/resources/doc/geoxygene-semiology-0.4.pdf>`,  in PDF file format.

The ‘semiology’ module originates from various works at COGIT lab. Most of the module 
originates from the works of Elodie Buard and Elisabeth Chesneau on the improvement 
of contrasts between symbolised features in maps. Other works on map legends have 
influenced the module, even if the related codes are not yet included in this version 
of the module. 

Main publications directly linked to the module :

* Buard E., Ruas A., 2009, `Processes for improving the colours of topographic maps 
  in the context of Map-on-Demand <http://icaci.org/documents/ICC_proceedings/ICC2009/html/refer/30_2.pdf>`_,
  24th International Cartographic Conference (ICC'09), 15-21 november, Santiago (Chile) 

* Buard E., Ruas A., 2007, `Evaluation of colour contrasts by means of expert knowledge for on-demand mapping 
  <http://cartography.tuwien.ac.at/ica/documents/ICC_proceedings/ICC2007/documents/doc/THEME%203/oral%201-2/3.1-2.4%20EVALUATION%20OF%20COLOUR%20CONTRASTS%20BY%20MEANS%20OF%20EXPERT%20KN.doc>`_,
  23rd International Cartographic Conference (ICC'07), 4-10 August, Moscow (Russia) 

* Chesneau E., 2007, `Improvement of Colour Contrasts in Maps : Application to Risk Maps 
  <http://people.plan.aau.dk/~enc/AGILE2007/PDF/32_PDF.pdf>`_, 
  10th International Conference on Geographic Information Science (AGILE'07), 8-11 may, Aalborg (Denmark)

* Chesneau E., 2007, `Etude des contrastes de couleur pour améliorer la lisibilité des cartes
  <http://thema.univ-fcomte.fr/theoq/pdf/2007/TQ2007%20ARTICLE%2061.pdf>`_, 
  8èmes rencontres ThéoQuant, 10-12 janvier, Besançon (France)

* Chesneau E. 2006, `Modèle d'amélioration automatique des contrastes de couleur en cartographie
  <http://recherche.ign.fr/labos/cogit/pdf/THESES/CHESNEAU/These_Chesneau_2006.zip>`_,
  Application aux cartes de risques. PhD, Univ. Marne-La-Vallée.

 

  