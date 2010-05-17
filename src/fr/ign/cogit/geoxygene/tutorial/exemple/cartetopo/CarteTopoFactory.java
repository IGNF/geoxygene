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

package fr.ign.cogit.geoxygene.tutorial.exemple.cartetopo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * Classe fabrique de cartes topologiques : - soit par défaut, - soit une carte
 * topologique avec des noeuds valués.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * 
 */
public class CarteTopoFactory {

	/**
	 * Logger.
	 */
	static Logger logger = Logger.getLogger(CarteTopoFactory.class);

	/**
	 * Création d'une CarteTopo à partir d'une FT_FeatureCollection.
	 * 
	 * @param collection
	 *            collection of features
	 * @return une carte topologique
	 */
	@SuppressWarnings("unchecked")
	public static CarteTopo creeCarteTopoDefaut(
			FT_FeatureCollection<? extends FT_Feature> collection) {

		// Initialisation d'une nouvelle CarteTopo
		CarteTopo carteTopo = new CarteTopo("Graphe"); //$NON-NLS-1$

		// Récupération des arcs de la carteTopo
		Population<Arc> arcs = carteTopo.getPopArcs();

		Iterator<FT_Feature> it = (Iterator<FT_Feature>) collection
				.getElements().iterator();
		FT_Feature feature;
		Arc arc;

		// Import des arcs de la collection dans la carteTopo

		
		while (it.hasNext()) {
			feature = it.next();
			List<GM_LineString> lineStrings = null;
			if (feature.getGeom().isLineString()) {
				lineStrings = getLineStrings((GM_LineString) feature.getGeom());
			} else {
				if (feature.getGeom().isMultiCurve()) {
					lineStrings = getLineStrings((GM_LineString) JtsAlgorithms
							.union(((GM_MultiCurve<GM_LineString>) feature
									.getGeom()).getList()));
				}
			}
			if (lineStrings != null) {

				for(GM_LineString line : lineStrings) {
					// création d'un nouvel élément
					arc = arcs.nouvelElement();
					
					//arc.setOrientation(orientation);
					// affectation de la géométrie de l'objet issu de la collection
					// à l'arc de la carteTopo
					arc.setGeometrie(line);
					// instanciation de la relation entre l'arc créé et l'objet
					// issu de la collection
					arc.addCorrespondant(feature);
				}

			}
		}

		logger.info("Nombre d'arcs créés : " //$NON-NLS-1$
				+ carteTopo.getPopArcs().size());

		// création des noeuds manquants
		logger.info("création des noeuds manquants"); //$NON-NLS-1$
		carteTopo.creeNoeudsManquants(2);
		// création de la topologie Arcs Noeuds
		logger.info("création de la topologie Arcs Noeuds"); //$NON-NLS-1$
		carteTopo.creeTopologieArcsNoeuds(2);
		// La carteTopo est rendue planaire
		logger.info("La carte topologique est rendue planaire"); //$NON-NLS-1$
		carteTopo.rendPlanaire(2);
		logger.info("création des faces de la carte topologique"); //$NON-NLS-1$
		// création des faces de la carteTopo
		carteTopo.creeTopologieFaces();

		logger.info("Nombre de faces créées : " //$NON-NLS-1$
				+ carteTopo.getPopFaces().size());

		return carteTopo;
	}

	static List<GM_LineString> getLineStrings(GM_LineString line) {
		List<GM_LineString> result = new ArrayList<GM_LineString>();
		for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
			DirectPositionList list = new DirectPositionList();
			list.add(line.getControlPoint(i));
			list.add(line.getControlPoint(i+1));
			result.add(new GM_LineString(list));
		}
		return result;
	}

	/**
	 * Création d'une carte topologique étendue (noeud valué) à partir d'une
	 * FT_FeatureCollection.
	 * 
	 * @param collection
	 *            collection of features
	 * @return une carte topologique
	 */
	@SuppressWarnings("unchecked")
	public static CarteTopo creeCarteTopoEtendue(
			FT_FeatureCollection<? extends FT_Feature> collection) {

		// Initialisation d'une nouvelle CarteTopo
		CarteTopoEtendue carteTopo = new CarteTopoEtendue("Graphe"); //$NON-NLS-1$

		// Récupération des arcs de la carteTopo
		Population<Arc> arcs = carteTopo.getPopArcs();

		Iterator<FT_Feature> it = (Iterator<FT_Feature>) collection
				.getElements().iterator();
		FT_Feature feature;
		Arc arc;

		// Import des arcs de la collection dans la carteTopo
		while (it.hasNext()) {
			feature = it.next();
			// création d'un nouvel élément
			arc = arcs.nouvelElement();
			// affectation de la géométrie de l'objet issu de la collection
			// à l'arc de la carteTopo
			arc.setGeometrie((GM_LineString) feature.getGeom());
			// instanciation de la relation entre l'arc créé et l'objet
			// issu de la collection
			arc.addCorrespondant(feature);
		}

		logger.info("Nombre d'arcs créés : " //$NON-NLS-1$
				+ carteTopo.getPopArcs().size());

		// création des noeuds manquants
		logger.info("création des noeuds manquants"); //$NON-NLS-1$
		carteTopo.creeNoeudsManquants(2);
		// création de la topologie Arcs Noeuds
		logger.info("création de la topologie Arcs Noeuds"); //$NON-NLS-1$
		carteTopo.creeTopologieArcsNoeuds(2);
		// Valuation des noeuds
		logger.info("Valuation des noeuds"); //$NON-NLS-1$

		// Récupération des noeuds de la carteTopo
		Population<Noeud> noeuds = carteTopo.getPopNoeuds();

		Iterator<Noeud> itNoeuds = noeuds.iterator();
		while (itNoeuds.hasNext()) {
			NoeudValue noeudValue = (NoeudValue) itNoeuds.next();
			noeudValue.setDegre(noeudValue.arcs().size());
		}

		return carteTopo;
	}
}
