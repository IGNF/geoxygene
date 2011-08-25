/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.tutorial.exemple.appariement;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.tutorial.data.BdCartoTrRoute;
import fr.ign.cogit.geoxygene.tutorial.data.BdTopoTrRoute;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * Exemple d'appariement entre données routières
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TestAppariementRoutier {

  public static void main(String[] args) {

    // Initialisation de la connexion à la base de données
    Geodatabase geodb = GeodatabaseOjbFactory.newInstance();

    // Chargement des données

    // données BDCarto
    IFeatureCollection<IFeature> tronconsBDC = geodb
        .loadAllFeatures(BdCartoTrRoute.class);
    // données BDTopo
    IFeatureCollection<IFeature> tronconsBDT = geodb
        .loadAllFeatures(BdTopoTrRoute.class);

    // Appariement

    // Initialisation des paramètres
    ParametresApp param = Parametres.parametresDefaut(tronconsBDC, tronconsBDT);

    // Lance les traitement et récupère les liens d'appariement
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param,
        cartesTopo);

    // Récupération des réseaux (ReseauApp hérite de CarteTopo)
    ReseauApp carteTopoBDC = cartesTopo.get(0);
    ReseauApp carteTopoBDT = cartesTopo.get(1);

    // Classement des arcs selon le resultat (évaluation des résultats)
    List<String> valeursClassement = new ArrayList<String>();
    valeursClassement.add("Apparié"); //$NON-NLS-1$
    valeursClassement.add("Incertitude"); //$NON-NLS-1$
    valeursClassement.add("Non apparié"); //$NON-NLS-1$

    // Récupération des arcs et des noeuds puis classement en "appariés",
    // "incertains" ou "non appariés" (BD référence)
    List<ReseauApp> cartesTopoBDCValuees = AppariementIO
        .scindeSelonValeursResultatsAppariement(carteTopoBDC, valeursClassement);
    IPopulation<Arc> arcsBDCApparies = cartesTopoBDCValuees.get(0).getPopArcs();
    IPopulation<Arc> arcsBDCIncertains = cartesTopoBDCValuees.get(1)
        .getPopArcs();
    IPopulation<Arc> arcsBDCNonApparies = cartesTopoBDCValuees.get(2)
        .getPopArcs();
    IPopulation<Noeud> noeudsBDCApparies = cartesTopoBDCValuees.get(0)
        .getPopNoeuds();
    IPopulation<Noeud> noeudsBDCIncertains = cartesTopoBDCValuees.get(1)
        .getPopNoeuds();
    IPopulation<Noeud> noeudsBDCNonApparies = cartesTopoBDCValuees.get(2)
        .getPopNoeuds();

    // Récupération des arcs et des noeuds puis classement en "appariés",
    // "incertains" ou "non appariés" (BD comparaison)
    List<ReseauApp> cartesTopoBDTValuees = AppariementIO
        .scindeSelonValeursResultatsAppariement(carteTopoBDC, valeursClassement);
    IPopulation<Arc> arcsBDTApparies = cartesTopoBDTValuees.get(0).getPopArcs();
    IPopulation<Arc> arcsBDTIncertains = cartesTopoBDTValuees.get(1)
        .getPopArcs();
    IPopulation<Arc> arcsBDTNonApparies = cartesTopoBDTValuees.get(2)
        .getPopArcs();
    IPopulation<Noeud> noeudsBDTApparies = cartesTopoBDTValuees.get(0)
        .getPopNoeuds();
    IPopulation<Noeud> noeudsBDTIncertains = cartesTopoBDTValuees.get(1)
        .getPopNoeuds();
    IPopulation<Noeud> noeudsBDTNonApparies = cartesTopoBDTValuees.get(2)
        .getPopNoeuds();

    // Récupération des liens puis classement en surs, incertains et très
    // incertains
    List<Double> valeursClassementL = new ArrayList<Double>();
    valeursClassementL.add(new Double(0.5));
    valeursClassementL.add(new Double(1));

    List<EnsembleDeLiens> liensClasses = liens
        .classeSelonSeuilEvaluation(valeursClassementL);
    EnsembleDeLiens liensNuls = liensClasses.get(0);
    EnsembleDeLiens liensIncertains = liensClasses.get(1);
    EnsembleDeLiens liensSurs = liensClasses.get(2);

    // Affichage
    // Initiatlisation des visualisateurs
    // BDTopo
    ObjectViewer viewerCarto = new ObjectViewer();
    // BDCarto
    ObjectViewer viewerTopo = new ObjectViewer();
    // Objets appariés et non appariés
    ObjectViewer viewerApp = new ObjectViewer();
    // Appariement et évaluation des liens
    ObjectViewer viewerEval = new ObjectViewer();

    // FENETRE BD REFERENCE
    viewerCarto
        .addFeatureCollection(tronconsBDC, "BDCarto : Tronçons routiers"); //$NON-NLS-1$
    viewerCarto.addFeatureCollection(carteTopoBDC.getPopArcs(),
        "Topologie : Arcs"); //$NON-NLS-1$
    viewerCarto.addFeatureCollection(carteTopoBDC.getPopNoeuds(),
        "Réseau : Noeuds"); //$NON-NLS-1$

    // FENETRE BD COMPARAISON
    viewerTopo.addFeatureCollection(tronconsBDT, "BDTopo : Tronçons routiers"); //$NON-NLS-1$
    viewerTopo.addFeatureCollection(carteTopoBDT.getPopArcs(),
        "Topologie : Arcs"); //$NON-NLS-1$
    viewerTopo.addFeatureCollection(carteTopoBDT.getPopNoeuds(),
        "Réseau : Noeuds"); //$NON-NLS-1$

    // FENETRE APPARIEMENT
    viewerApp.addFeatureCollection(carteTopoBDC.getPopArcs(), "Arcs BDCarto"); //$NON-NLS-1$
    viewerApp.addFeatureCollection(carteTopoBDC.getPopNoeuds(),
        "Noeuds BDCarto"); //$NON-NLS-1$
    viewerApp.addFeatureCollection(carteTopoBDT.getPopArcs(),
        "Topologie : Arcs"); //$NON-NLS-1$
    viewerApp
        .addFeatureCollection(carteTopoBDT.getPopNoeuds(), "Noeuds BDTopo"); //$NON-NLS-1$
    viewerApp.addFeatureCollection(liensNuls, "Liens très peu surs"); //$NON-NLS-1$
    viewerApp.addFeatureCollection(liensIncertains, "Liens incertains"); //$NON-NLS-1$
    viewerApp.addFeatureCollection(liensSurs, "Liens surs"); //$NON-NLS-1$

    // FENETRE EVALUATION
    viewerEval.addFeatureCollection(arcsBDCApparies, "Arcs BDCarto appariées"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(arcsBDCIncertains,
        "Arcs BDCarto incertains"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(arcsBDCNonApparies,
        "Arcs BDCarto non appariés"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(noeudsBDCApparies,
        "Noeuds BDCarto appariés"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(noeudsBDCIncertains,
        "Noeuds BDCarto incertains"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(noeudsBDCNonApparies,
        "Noeuds BDCarto non appariés"); //$NON-NLS-1$

    viewerEval.addFeatureCollection(arcsBDTApparies, "Arcs BDTopo appariées"); //$NON-NLS-1$
    viewerEval
        .addFeatureCollection(arcsBDTIncertains, "Arcs BDTopo incertains"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(arcsBDTNonApparies,
        "Arcs BDTopo non appariés"); //$NON-NLS-1$
    viewerEval
        .addFeatureCollection(noeudsBDTApparies, "Noeuds BDTopo appariés"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(noeudsBDTIncertains,
        "Noeuds BDTopo incertains"); //$NON-NLS-1$
    viewerEval.addFeatureCollection(noeudsBDTNonApparies,
        "Noeuds BDTopo non appariés"); //$NON-NLS-1$
  }
}
