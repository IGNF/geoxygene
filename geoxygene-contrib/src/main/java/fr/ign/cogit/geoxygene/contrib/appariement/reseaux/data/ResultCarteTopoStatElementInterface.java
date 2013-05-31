package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

/**
 * 
 * 
 *
 */
public interface ResultCarteTopoStatElementInterface {

    /**
     * NB_BRUTS.
     *  Nombre d'éléments en entrée.
     */
    int NB_BRUTS = 1;
    
    /**
     * Nombre d'éléments importés.
     */
    int NB_IMPORT = 2;
    
    /**
     * Nombre d'éléments après l'indexation spatiale.
     */
    int NB_INDEXATION = 3;
    
    /**
     * Nombre d'éléments après le graphe planaire.
     */
    int NB_PLANAIRE = 4; 

    /**
     * Nombre d'éléments après fusion des noeuds proches.
     */
    int NB_NOEUDS_PROCHES = 5;
    
    /**
     * Nombre d'éléments après suppression des noeuds isolés.
     */
    int NB_NOEUDS_ISOLES = 6;

    
}