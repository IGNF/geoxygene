/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Réseau à apparier, spécialisation d'une carte topo,
 * utile quasi-uniquement pour spécifier un constructeur adhoc.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class ReseauApp extends CarteTopo {

	/** Constructeur par défaut : ATTENTION, constructeur à éviter car aucune population n'est créée */
	public ReseauApp() {
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
	}

	/** Constructeur qui créé une carte topo non persistante de type Reseau_App, avec des populations
	 * de Arc_App, Noeud_App, Face_App, et Groupe_App */
	public ReseauApp(String nom_logique) {
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
		this.setNom(nom_logique);
		this.setPersistant(false);
		Population<ArcApp> arcs = new Population<ArcApp>(false, "Arc", ArcApp.class,true);
		this.addPopulation(arcs);
		Population<NoeudApp> noeuds = new Population<NoeudApp>(false, "Noeud", NoeudApp.class,true);
		this.addPopulation(noeuds);
		Population<FaceApp> faces = new Population<FaceApp>(false, "Face", FaceApp.class,true);
		this.addPopulation(faces);
		Population<GroupeApp> groupes = new Population<GroupeApp>(false, "Groupe", GroupeApp.class,false);
		this.addPopulation(groupes);
	}

	public void instancieAttributsNuls(ParametresApp param) {
		// On affecte une taille par défaut à tous nouveaux sans taille
		Iterator<?> itNoeuds = this.getPopNoeuds().getElements().iterator();
		while( itNoeuds.hasNext() ) {
			NoeudApp noeud = (NoeudApp)itNoeuds.next();
			if (noeud.getTaille() == 0 ) noeud.setTaille(param.distanceNoeudsMax);
		}
	}



}
