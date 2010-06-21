/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Utilitary class for importing and exporting data and matching them.
 * <p>
 * Méthodes d'import et export pour l'appariement sur des données Géographiques
 * quelconques (création des réseaux, lancement de l'appariement, export des
 * résultats).
 *
 * @author Mustiere - IGN / Laboratoire COGIT
 */
public final class AppariementIO {
    /**
     * Private constructor. Not used: this is a utilitary class.
     */
    private AppariementIO() {
    }
    /**
     * Static logger.
     */
    private static final Logger LOGGER =
        Logger.getLogger(AppariementIO.class.getName());
    /**
     * Lancement de l'appariement de réseaux sur des objets Géographiques :
     * 1- Transformation des données initales en deux graphes, en fonction des
     * paramètres d'import,
     * 2- Lancement du calcul d'appariement générique sur les deux réseaux,
     * 3- Analyse et export des résultats éventuellement.
     *
     * @return  L'ensemble des liens en sortie (de la classe EnsembleDeLiens).
     *
     * @param paramApp Les paramètres de l'appariement (seuls de distance,
     * préparation topologique des données...)
     *
     * @param cartesTopo Liste en entrée/sortie qui permet de Récupèrer en
     * sortie les graphes intermédiaires créés pendant le calcul (de type
     * Reseau_App, spécialisation de CarteTopo).
     * - Si on veut Récupèrer les graphes : passer une liste vide - new
     * ArrayList() - mais non nulle.
     *    Elle contient alors en sortie 2 éléments : dans l'ordre les cartes
     *    topo de référence et comparaison.
     *    Elle peut contenir un 3eme élément: le graphe ref recalé sur comp
     *    si cela est demandé dans les paramètres.
     * - Si on ne veut rien Récupèrer : passer Null
     */
    public static EnsembleDeLiens appariementDeJeuxGeo(
            final ParametresApp paramApp, final List<ReseauApp> cartesTopo) {

        switch(paramApp.debugAffichageCommentaires) {
        case 0 : LOGGER.setLevel(Level.ERROR); break;
        case 1 : LOGGER.setLevel(Level.INFO); break;
        default : LOGGER.setLevel(Level.DEBUG); break;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(""); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.MatchingStart")); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.MatchingInfo")); //$NON-NLS-1$
            LOGGER.info(""); //$NON-NLS-1$
        }
        ////////////////////////////////////////////////
        // STRUCTURATION
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(I18N.getString(
            "AppariementIO.DataStructuring")); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.TopologicalStructuing")); //$NON-NLS-1$
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(I18N.getString(
            "AppariementIO.StructuringStart" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
            LOGGER.debug(I18N.getString(
            "AppariementIO.Network1Creation" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
        }
        ReseauApp reseauRef = importData(paramApp, true);
        if (cartesTopo != null) { cartesTopo.add(reseauRef); }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.Network2Creation" //$NON-NLS-1$
        ) + (new Time(System.currentTimeMillis())).toString());
        }
        ReseauApp reseauComp = importData(paramApp, false);
        if (cartesTopo != null) { cartesTopo.add(reseauComp); }

        // NB: l'ordre dans lequel les projections sont faites n'est pas neutre
        if (paramApp.projeteNoeuds2SurReseau1) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.ProjectionOfNetwork2OnNetwork1" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
            }
            reseauRef.projete(reseauComp,
                    paramApp.projeteNoeuds2SurReseau1DistanceNoeudArc,
                    paramApp.projeteNoeud2surReseau1DistanceProjectionNoeud,
                    paramApp.projeteNoeud2surReseau1ImpassesSeulement);
        }
        if (paramApp.projeteNoeuds1SurReseau2) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.ProjectionOfNetwork1OnNetwork2" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
            }
            reseauComp.projete(reseauRef,
                    paramApp.projeteNoeuds1SurReseau2DistanceNoeudArc,
                    paramApp.projeteNoeuds1SurReseau2DistanceProjectionNoeud,
                    paramApp.projeteNoeuds1SurReseau2ImpassesSeulement);
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.AttributeFilling" //$NON-NLS-1$
        ) + (new Time(System.currentTimeMillis())).toString());
        }
        reseauRef.instancieAttributsNuls(paramApp);
        reseauComp.initialisePoids();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(I18N.getString(
            "AppariementIO.StructuringFinished")); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.Network1") //$NON-NLS-1$
            + reseauRef.getPopArcs().size() + I18N.getString(
            "AppariementIO.Edges") //$NON-NLS-1$
            + reseauRef.getPopNoeuds().size() + I18N.getString(
            "AppariementIO.Nodes")); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.Network2") //$NON-NLS-1$
            + reseauComp.getPopArcs().size() + I18N.getString(
            "AppariementIO.Edges") //$NON-NLS-1$
            + reseauComp.getPopNoeuds().size() + I18N.getString(
            "AppariementIO.Nodes")); //$NON-NLS-1$
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.StructuringEnd") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        // APPARIEMENT
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(""); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.NetworkMatching")); //$NON-NLS-1$
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.NetworkMatchingStart") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
        }
        EnsembleDeLiens liens = Appariement.appariementReseaux(reseauRef,
                reseauComp, paramApp);
        if (LOGGER.isInfoEnabled()) { LOGGER.info(I18N.getString(
        "AppariementIO.NetworkMatchingFinished")); //$NON-NLS-1$
        LOGGER.info("  " + liens.size() + I18N.getString(//$NON-NLS-1$
        "AppariementIO.MatchingLinksFound")); //$NON-NLS-1$
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.NetworkMatchingEnd") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        // EXPORT
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(""); //$NON-NLS-1$
            LOGGER.info(I18N.getString(
            "AppariementIO.Conclusion")); //$NON-NLS-1$
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.ExportStart") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        if (paramApp.debugBilanSurObjetsGeo) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.LinkTransformation") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
            }
            EnsembleDeLiens liensGeneriques =
                LienReseaux.exportLiensAppariement(liens, reseauRef, paramApp);
            Appariement.nettoyageLiens(reseauRef, reseauComp);
            if (LOGGER.isInfoEnabled()) { LOGGER.info(I18N.getString(
            "AppariementIO.MatchingEnd")); //$NON-NLS-1$
            }
            return liensGeneriques;
        }
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.LinkGeometry") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        LienReseaux.exportAppCarteTopo(liens, paramApp);
        if (LOGGER.isInfoEnabled()) { LOGGER.info(I18N.getString(
        "AppariementIO.MatchingEnd")); //$NON-NLS-1$
        }
        return liens;
    }
    /**
     * Création d'une carte topo à partir des objets Géographiques initiaux.
     *
     * @param paramApp Les paramètres de l'appariement (seuls de distance,
     * préparation topologique des données...)
     * @param ref
     * true = on traite le réseau de référence
     * false = on traite le réseau de comparaison
     * @return Le réseau créé
     */
    private static ReseauApp importData(final ParametresApp paramApp,
            final boolean ref) {
        switch(paramApp.debugAffichageCommentaires) {
        case 0 : LOGGER.setLevel(Level.ERROR); break;
        case 1 : LOGGER.setLevel(Level.INFO); break;
        default : LOGGER.setLevel(Level.DEBUG); break;
        }
        FT_FeatureCollection<?> popGeo;
        Population<?> popArcApp, popNoeudApp;
        ArcApp arc;
        ReseauApp reseau = null;
        if (ref) { reseau = new ReseauApp(I18N.getString(
        "AppariementIO.ReferenceNetwork")); //$NON-NLS-1$
        }  else { reseau = new ReseauApp(I18N.getString(
        "AppariementIO.ComparisonNetwork")); //$NON-NLS-1$
        }
        popArcApp = reseau.getPopArcs();
        popNoeudApp = reseau.getPopNoeuds();
        ///////////////////////////
        // import des arcs
        Iterator<FT_FeatureCollection<? extends Arc>> itPopArcs = null;
        if (ref) { itPopArcs = paramApp.populationsArcs1.iterator();
        } else { itPopArcs = paramApp.populationsArcs2.iterator(); }
        while (itPopArcs.hasNext()) {
            popGeo = itPopArcs.next();
            //import d'une population d'arcs
            for (FT_Feature element : popGeo.getElements()) {
                arc = (ArcApp) popArcApp.nouvelElement();
                GM_LineString ligne = new GM_LineString(
                        (DirectPositionList) element.getGeom().coord()
                        .clone());
                arc.setGeometrie(ligne);
                if (paramApp.populationsArcsAvecOrientationDouble) {
                    arc.setOrientation(2);
                } else { arc.setOrientation(1); }
                arc.addCorrespondant(element);
                // Le code ci-dessous permet un import plus fin mais a été
                // réalisé pour des données spécifiques et n'est pas encore
                // codé très générique.
                // Il est donc commenté dans cette version du code.
                // element = (FT_Feature)itElements.next();
                // if ( ref && paramApp.filtrageRef ) {
                // if ( filtrageTroncon(element) ) continue;
                // }
                // if ( !ref && paramApp.filtrageComp ) {
                // if ( filtrageTroncon(element) ) continue;
                // }
                // arc = (Arc_App)popArcApp.nouvelElement();
                // GM_LineString ligne = new GM_LineString((DirectPositionList)
                // element.getGeom().coord().clone());
                // arc.setGeometrie(ligne);
                // if ( paramApp.orientationConstante) {
                // if (paramApp.orientationDouble) arc.setOrientation(2);
                // else arc.setOrientation(1);
                // }
                // else arc.setOrientation(orientationTroncon(element));
                // arc.addCorrespondant(element);
            }
        }
        // import des noeuds
        Iterator<?> itPopNoeuds = null;
        if (ref) {
            itPopNoeuds = paramApp.populationsNoeuds1.iterator();
        } else { itPopNoeuds = paramApp.populationsNoeuds2.iterator(); }
        while (itPopNoeuds.hasNext()) {
            popGeo = (Population<?>) itPopNoeuds.next();
            //import d'une population de noeuds
            for (FT_Feature element : popGeo.getElements()) {
                NoeudApp noeud = (NoeudApp) popNoeudApp.nouvelElement();
                //noeud.setGeometrie((GM_Point)element.getGeom());
                noeud.setGeometrie(new GM_Point(
                        (DirectPosition) ((GM_Point) element.getGeom())
                        .getPosition().clone()));
                noeud.addCorrespondant(element);
                noeud.setTaille(paramApp.distanceNoeudsMax);
                // Le code ci-dessous permet un import plus fin mais a été
                // réalisé pour des données spécifiques et n'est pas encore
                // codé très générique.
                // Il est donc commenté dans cette version du code.
                // if ( paramApp.distanceNoeudsConstante )
                // noeud.setTaille(paramApp.distanceNoeuds);
                // else noeud.setTaille(tailleNoeud(element, paramApp));
            }
        }
        // Indexation spatiale des arcs et noeuds
        // On crée un dallage régulier avec en moyenne 20 objets par case
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.SpatialIndexing") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        int nb = (int) Math.sqrt(reseau.getPopArcs().size() / 20);
        if (nb == 0) { nb = 1; }
        reseau.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
        reseau.getPopNoeuds().initSpatialIndex(reseau.getPopArcs()
                .getSpatialIndex());
        // Instanciation de la topologie
        // 1- création de la topologie arcs-noeuds, rendu du graphe planaire
        if ((ref && paramApp.topologieGraphePlanaire1)
                || (!ref && paramApp.topologieGraphePlanaire2)) {
            // cas où on veut une topologie planaire
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.PlanarGraph") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
            }
            // Debut Ajout
            reseau.creeTopologieArcsNoeuds(0);
            reseau.creeNoeudsManquants(0);
            reseau.filtreDoublons(0);
            reseau.filtreArcsDoublons();
            // Fin Ajout
            reseau.rendPlanaire(0);
            reseau.filtreDoublons(0);
        } else {
            // cas où on ne veut pas nécessairement rendre planaire la
            // topologie
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.Topology") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
            }
            reseau.creeNoeudsManquants(0);
            reseau.filtreDoublons(0);
            reseau.creeTopologieArcsNoeuds(0);
        }
        // 2- On fusionne les noeuds proches
        if (ref) {
            if (paramApp.topologieSeuilFusionNoeuds1 >= 0) {
                if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.NodesFusion") //$NON-NLS-1$
                + new Time(System.currentTimeMillis()).toString());
                }
                reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds1);
            }
            if (paramApp.topologieSurfacesFusionNoeuds1 != null) {
                if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.NodesFusionInsideASurface") //$NON-NLS-1$
                + (new Time(System.currentTimeMillis())).toString());
                }
                reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds1);
            }
        } else {
            if (paramApp.topologieSeuilFusionNoeuds2 >= 0) {
                if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.NodesFusion") //$NON-NLS-1$
                + new Time(System.currentTimeMillis()).toString());
                }
                reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds2);
            }
            if (paramApp.topologieSurfacesFusionNoeuds2 != null) {
                if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.NodesFusionInsideASurface") //$NON-NLS-1$
                + new Time(System.currentTimeMillis()).toString());
                }
                reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds2);
            }
        }
        // 3- On enlève les noeuds isolés
        if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
        "AppariementIO.IsolatedNodesFiltering") //$NON-NLS-1$
        + new Time(System.currentTimeMillis()).toString());
        }
        reseau.filtreNoeudsIsoles();
        // 4- On filtre les noeuds simples (avec 2 arcs incidents)
        if ((ref && paramApp.topologieElimineNoeudsAvecDeuxArcs1)
                || (!ref && paramApp.topologieElimineNoeudsAvecDeuxArcs2)) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
            "AppariementIO.NodesFiltering") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
            }
            reseau.filtreNoeudsSimples();
        }

        // 5- On fusionne des arcs en double
        if (ref && paramApp.topologieFusionArcsDoubles1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(I18N.getString(
                "AppariementIO.EdgesFiltering") //$NON-NLS-1$
                + new Time(System.currentTimeMillis()).toString());
            }
            reseau.filtreArcsDoublons();
        }
        if (!ref && paramApp.topologieFusionArcsDoubles2) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.EdgesFiltering") //$NON-NLS-1$
                + new Time(System.currentTimeMillis()).toString());
            }
            reseau.filtreArcsDoublons();
        }
        // 6 - On crée la topologie de faces
        if (!ref && paramApp.varianteChercheRondsPoints) {
            if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                "AppariementIO.FaceTopology")); //$NON-NLS-1$
            }
            reseau.creeTopologieFaces();
        }
        // 7 - On double la taille de recherche pour les impasses
        if (paramApp.distanceNoeudsImpassesMax >= 0) {
            if (ref) {
                if (LOGGER.isDebugEnabled()) { LOGGER.debug(I18N.getString(
                    "AppariementIO.DoublingOfSearchRadius")); //$NON-NLS-1$
                }
                Iterator<?> itNoeuds =
                    reseau.getPopNoeuds().getElements().iterator();
                while (itNoeuds.hasNext()) {
                    NoeudApp noeud2 = (NoeudApp) itNoeuds.next();
                    if (noeud2.arcs().size() == 1) {
                        noeud2.setTaille(paramApp.distanceNoeudsImpassesMax);
                    }
                }
            }
        }
        return reseau;
    }
    // METHODES D'EXPORT
    /**
     * Methode utile principalement pour analyser les résultats d'un
     * appariement, qui découpe un réseau en plusieurs réseaux selon les
     * valeurs de l'attribut "ResultatAppariement" des arcs et noeuds du
     * réseau apparié.
     * @param reseauRef reference network
     * @param valeursClassement sorting values
     * @return sorted networks
     */
    public static List<ReseauApp> scindeSelonValeursResultatsAppariement(
            final ReseauApp reseauRef, final List<String> valeursClassement) {
        List<ReseauApp> cartesTopoClassees = new ArrayList<ReseauApp>();
        Iterator<?> itArcs = reseauRef.getPopArcs().getElements().iterator();
        Iterator<?> itNoeuds =
            reseauRef.getPopNoeuds().getElements().iterator();
        ArcApp arc, arcClasse;
        NoeudApp noeud, noeudClasse;
        int i;
        for (i = 0; i < valeursClassement.size(); i++) {
            cartesTopoClassees.add(new ReseauApp(I18N.getString(
                    "AppariementIO.Evaluation") //$NON-NLS-1$
                    + valeursClassement.get(i)));
        }
        while (itArcs.hasNext()) {
            arc = (ArcApp) itArcs.next();
            for (i = 0; i < valeursClassement.size(); i++) {
                if (arc.getResultatAppariement() == null) { continue; }
                if (arc.getResultatAppariement()
                        .startsWith(valeursClassement.get(i))) {
                    arcClasse = (ArcApp) cartesTopoClassees.get(i)
                    .getPopArcs().nouvelElement();
                    arcClasse.setGeom(arc.getGeom());
                    arcClasse.setResultatAppariement(arc
                            .getResultatAppariement());
                }
            }
        }
        while (itNoeuds.hasNext()) {
            noeud = (NoeudApp) itNoeuds.next();
            for (i = 0; i < valeursClassement.size(); i++) {
                if (noeud.getResultatAppariement() == null) { continue; }
                if (noeud.getResultatAppariement().startsWith(
                        valeursClassement.get(i))) {
                    noeudClasse = (NoeudApp) cartesTopoClassees.get(i)
                    .getPopNoeuds().nouvelElement();
                    noeudClasse.setGeom(noeud.getGeom());
                    noeudClasse.setResultatAppariement(noeud
                            .getResultatAppariement());
                }
            }
        }
        return cartesTopoClassees;
    }
}
