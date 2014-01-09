package fr.ign.cogit.geoxygene.contrib.quality.comparison.parameters;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 *            Parameters used for linestrings matching
 * 
 * @author JFGirres
 */
public class LineStringMatchingParameters {

    @SuppressWarnings("unchecked")
    public static ParametresApp parametresDefaut(IFeatureCollection<? extends IFeature> populationsArcs1,
            IFeatureCollection<? extends IFeature> populationsArcs2) {

        ParametresApp param = new ParametresApp();

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES
        // ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        // Liste des classes d'arcs de la BD 1 (la moins détaillée) concernés
        // par
        // l'appariement
        param.populationsArcs1.add((IFeatureCollection<IFeature>) populationsArcs1);
        // Liste des classes de noeuds de la BD 1 (la moins détaillée) concernés
        // par
        // l'appariement
        // param.populationsNoeuds1.add(populationsNoeuds1);
        // Liste des classes d'arcs de la BD 2 (la plus détaillée) concernés par
        // l'appariement
        param.populationsArcs2.add((IFeatureCollection<IFeature>) populationsArcs2);
        // Liste des classes de noeuds de la BD 2 (la plus détaillée) concernés
        // par
        // l'appariement
        // param.populationsNoeuds2.add(populationsNoeuds2);

        param.populationsArcsAvecOrientationDouble1 = true;
        param.populationsArcsAvecOrientationDouble2 = true;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////////// TAILLES DE RECHERCHE ///////////////////////
        // /////////////// Ecarts de distance autorisés ///////////////////////
        // /////////////// CE SONT LES PARAMETRES PRINCIPAUX
        // ///////////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.distanceNoeudsMax = 2000; // 90
        param.distanceNoeudsImpassesMax = -1;
        param.distanceArcsMax = 2000; // 90
        param.distanceArcsMin = 30; // 30

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// TRAITEMENTS TOPOLOGIQUES A L'IMPORT /////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.topologieSeuilFusionNoeuds1 = -1;
        param.topologieSeuilFusionNoeuds2 = -1;
        param.topologieSurfacesFusionNoeuds1 = null;
        param.topologieSurfacesFusionNoeuds2 = null;
        param.topologieElimineNoeudsAvecDeuxArcs1 = false;
        param.topologieElimineNoeudsAvecDeuxArcs2 = false;
        param.topologieGraphePlanaire1 = false;
        param.topologieGraphePlanaire2 = false;
        param.topologieFusionArcsDoubles1 = false;
        param.topologieFusionArcsDoubles2 = false;

        // ///////////////////////////////////////////////////////////////////////////////
        // //////// TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT
        // ///////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.projeteNoeuds1SurReseau2 = false;
        param.projeteNoeuds1SurReseau2DistanceNoeudArc = 0;
        param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;
        param.projeteNoeuds1SurReseau2ImpassesSeulement = false;
        param.projeteNoeuds2SurReseau1 = false;
        param.projeteNoeuds2SurReseau1DistanceNoeudArc = 0;
        param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 0;
        param.projeteNoeuds2SurReseau1ImpassesSeulement = false;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// VARIANTES DU PROCESSUS GENERAL //////////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.varianteForceAppariementSimple = false;
        param.varianteRedecoupageArcsNonApparies = false;
        param.varianteRedecoupageNoeudsNonApparies = false;
        param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;
        param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;
        param.varianteFiltrageImpassesParasites = false;
        param.varianteChercheRondsPoints = false;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// OPTIONS D'EXPORT ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.exportGeometrieLiens2vers1 = true;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// OPTIONS DE DEBUG ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.debugAffichageCommentaires = 1;
        param.debugBilanSurObjetsGeo = true;
        param.debugTirets = true;
        param.debugPasTirets = 50;
        param.debugBuffer = false;
        param.debugTailleBuffer = 10;

        return param;
    }

    @SuppressWarnings("unchecked")
    public static ParametresApp parametresAvecNoeudsDefaut(IFeatureCollection<? extends IFeature> populationsArcs1,
            IFeatureCollection<? extends IFeature> populationsNoeuds1,
            IFeatureCollection<? extends IFeature> populationsArcs2,
            IFeatureCollection<? extends IFeature> populationsNoeuds2) {

        ParametresApp param = new ParametresApp();

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES
        // ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        // Liste des classes d'arcs de la BD 1 (la moins détaillée) concernés
        // par
        // l'appariement
        param.populationsArcs1.add((IFeatureCollection<IFeature>) populationsArcs1);
        // Liste des classes de noeuds de la BD 1 (la moins détaillée) concernés
        // par
        // l'appariement
        param.populationsNoeuds1.add((IFeatureCollection<IFeature>) populationsNoeuds1);
        // Liste des classes d'arcs de la BD 2 (la plus détaillée) concernés par
        // l'appariement
        param.populationsArcs2.add((IFeatureCollection<IFeature>) populationsArcs2);
        // Liste des classes de noeuds de la BD 2 (la plus détaillée) concernés
        // par
        // l'appariement
        param.populationsNoeuds2.add((IFeatureCollection<IFeature>) populationsNoeuds2);

        param.populationsArcsAvecOrientationDouble1 = true;
        param.populationsArcsAvecOrientationDouble2 = true;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////////// TAILLES DE RECHERCHE ///////////////////////
        // /////////////// Ecarts de distance autorisés ///////////////////////
        // /////////////// CE SONT LES PARAMETRES PRINCIPAUX
        // ///////////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.distanceNoeudsMax = 2000; // 150
        param.distanceNoeudsImpassesMax = -1;
        param.distanceArcsMax = 2000; // 100
        param.distanceArcsMin = 30; // 30

        // Paramètres de distance (contrainte sur la distance max à aller
        // chercher)
        // param.distanceArcsMax = 20; //valeur par défaut = 100
        // param.distanceArcsMin = 10; //valeur par défaut = 30
        // param.distanceNoeudsMax = 20; //valeur par défaut pour noeuds non
        // existants = 150
        // param.distanceNoeudsImpassesMax = 30;//valeur par défaut = -1

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// TRAITEMENTS TOPOLOGIQUES A L'IMPORT /////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.topologieSeuilFusionNoeuds1 = -1;
        param.topologieSeuilFusionNoeuds2 = -1;
        param.topologieSurfacesFusionNoeuds1 = null;
        param.topologieSurfacesFusionNoeuds2 = null;
        param.topologieElimineNoeudsAvecDeuxArcs1 = false;
        param.topologieElimineNoeudsAvecDeuxArcs2 = false;
        param.topologieGraphePlanaire1 = false;
        param.topologieGraphePlanaire2 = false;
        param.topologieFusionArcsDoubles1 = false;
        param.topologieFusionArcsDoubles2 = false;

        // ///////////////////////////////////////////////////////////////////////////////
        // //////// TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT
        // ///////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.projeteNoeuds1SurReseau2 = false;
        param.projeteNoeuds1SurReseau2DistanceNoeudArc = 0;
        param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;
        param.projeteNoeuds1SurReseau2ImpassesSeulement = false;
        param.projeteNoeuds2SurReseau1 = false;
        param.projeteNoeuds2SurReseau1DistanceNoeudArc = 0;
        param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 0;
        param.projeteNoeuds2SurReseau1ImpassesSeulement = false;

        // On projete les noeuds du réseau de référence sur les arcs du réseau
        // de
        // comparaison
        // param.projeteNoeuds1SurReseau2 = true; // true pour final
        // param.projeteNoeuds1SurReseau2_DistanceNoeudArc = 10; //valeur par
        // défaut
        // = 0
        // param.projeteNoeuds1SurReseau2_DistanceProjectionNoeud = 20; //valeur
        // par
        // défaut = 0
        // param.projeteNoeuds1SurReseau2_ImpassesSeulement= false; // true pour
        // final
        // param.projeteNoeud2surReseau1 = true; // true pour final
        // param.projeteNoeud2surReseau1_DistanceNoeudArc = 10; //valeur par
        // défaut
        // = 0
        // param.projeteNoeud2surReseau1_DistanceProjectionNoeud = 20; //valeur
        // par
        // défaut = 0
        // param.projeteNoeud2surReseau1_ImpassesSeulement= false; // true pour
        // final

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// VARIANTES DU PROCESSUS GENERAL //////////////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.varianteForceAppariementSimple = false;
        param.varianteRedecoupageArcsNonApparies = false;
        param.varianteRedecoupageNoeudsNonApparies = false;
        param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;
        param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;
        param.varianteFiltrageImpassesParasites = false;
        param.varianteChercheRondsPoints = false;

        // //Lance un surdécoupage du réseau pour les arcs du réseau de
        // référence
        // non encore appariés
        // param.varianteRedecoupageArcsNonApparies = true;
        // param.projeteNoeud2surReseau1_DistanceProjectionNoeud = 20;
        // //param.projeteNoeud2surReseau1_DistanceNoeudArc = 10;
        // param.varianteChercheRondsPoints=true;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// OPTIONS D'EXPORT ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.exportGeometrieLiens2vers1 = true;

        // ///////////////////////////////////////////////////////////////////////////////
        // /////////// OPTIONS DE DEBUG ////////////
        // ///////////////////////////////////////////////////////////////////////////////

        param.debugAffichageCommentaires = 1;
        param.debugBilanSurObjetsGeo = true;
        param.debugTirets = true;
        param.debugPasTirets = 50;
        param.debugBuffer = false;
        param.debugTailleBuffer = 10;

        // //Options de debug
        // param.debugTirets = true;//on représente les liens entre arcs par des
        // tirets
        // param.debugBuffer = true;//on représente les liens entre des objets
        // et un
        // noeud par un buffer
        // param.debugPasTirets = 20;//espacement entre les tirets
        // param.debugAffichageCommentaires = 2;//on affiche tous les messages
        // de
        // commentaires
        // param.debugBilanSurObjetsGeo = false;//bilan au niveau des arcs des
        // cartes topo

        return param;
    }

}
