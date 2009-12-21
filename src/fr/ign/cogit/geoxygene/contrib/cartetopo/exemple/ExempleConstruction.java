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

package fr.ign.cogit.geoxygene.contrib.cartetopo.exemple;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * Exemple de construction et d'utilisation d'une carte topo simple, hérit�e
 * du schéma générique de la cartetopo.
 * 
 * La carte topo en question est definie par heritage à partir des
 * classes du package : MonNoeud, MonArc, MaFace, MaCarteTopo (on n'utilise
 * pas de groupes dans cet exemple).
 * 
 * La seule petite difficult� est d'indiquer dans le code que
 * MaCarteTopo est constitue� d'objets MonNoeud, MonArc et MaFace
 * plutôt que les génériques Noeud, Arc et Face. Cette opération doit
 * se faire par un constructeur spécial dans la classe MaCarteTopo.
 * 
 * NB: il est bien entendu possible d'uiliser une cartetopo par défaut si
 * la surcharge des classes arcs, noeuds et faces n'est pas necessaire
 * dans l'application.
 * 
 * English: Example of cartetopo construction.
 * 
 * 
 * @author Bonin
 * @version 1.0
 */

public class ExempleConstruction {

	public static void main(String[] args){
		// On crée une carte de type MaCarteTopo
		MaCarteTopo ct = new MaCarteTopo("Exemple");

		// On ajoute à la carte ct des noeuds et des arcs
		MonNoeud n1 = new MonNoeud();
		n1.setCoord(new DirectPosition (0.,0.,0.));
		ct.addNoeud(n1);
		MonNoeud n2 = new MonNoeud();
		n2.setCoord(new DirectPosition (3.,1.,0.));
		ct.addNoeud(n2);
		MonNoeud n3 = new MonNoeud();
		n3.setCoord(new DirectPosition (1.,1.,0.));
		ct.addNoeud(n3);
		MonNoeud n4 = new MonNoeud();
		n4.setCoord(new DirectPosition (1.,-1.,0.));
		ct.addNoeud(n4);

		MonArc a1 = new MonArc();
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition (1.,-1.,0.));
		dpl1.add(new DirectPosition (1.,1.,0.));
		a1.setCoord(dpl1);
		ct.addArc(a1);

		MonArc a2 = new MonArc();
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition (0.,0.,0.));
		dpl2.add(new DirectPosition (1.,1.,0.));
		a2.setCoord(dpl2);
		ct.addArc(a2);

		MonArc a3 = new MonArc();
		DirectPositionList dpl3 = new DirectPositionList();
		dpl3.add(new DirectPosition (1.,1.,0.));
		dpl3.add(new DirectPosition (3.,1.,0.));
		a3.setCoord(dpl3);
		ct.addArc(a3);

		MonArc a4 = new MonArc();
		DirectPositionList dpl4 = new DirectPositionList();
		dpl4.add(new DirectPosition (3.,1.,0.));
		dpl4.add(new DirectPosition (1.,-1.,0.));
		a4.setCoord(dpl4);
		ct.addArc(a4);

		MonArc a5 = new MonArc();
		DirectPositionList dpl5 = new DirectPositionList();
		dpl5.add(new DirectPosition (1.,-1.,0.));
		dpl5.add(new DirectPosition (0.,0.,0.));
		a5.setCoord(dpl5);
		ct.addArc(a5);

		// Calcul de la topologie arc/noeuds (relations noeud initial/noeud final
		// pour chaque arete) a l'aide de la géometrie.
		ct.creeTopologieArcsNoeuds(0.1);

		// Calcul de la topologie de carte topologique (relations face gauche /
		// face droite pour chaque arete) avec les faces définies comme des
		// cycles du graphe.
		ct.creeTopologieFaces();

		// Affichage du nombre de faces
		System.out.println("Nombre de faces de la carte : "+ct.getListeFaces().size());

		// Affichage des coordonnees du noeud initial et du noeud final du premier arc
		MonArc arc = (MonArc) ct.getListeArcs().get(0);
		System.out.println("Noeud initial de a0 : "+arc.getNoeudIni().getCoord());
		System.out.println("Noeud final de a0   : "+arc.getNoeudFin().getCoord());

		// Calcul de la superficie des deux faces
		MaFace face = (MaFace) ct.getListeFaces().get(0);
		System.out.println("Superficie de f0 : "+face.getGeom().area());
		face = (MaFace) ct.getListeFaces().get(1);
		System.out.println("Superficie de f1 : "+face.getGeom().area());

		// Visualisation des données
		ObjectViewer obj = new ObjectViewer();
		// Note : on utilise ici ct.getPopNoeuds() pour afficher. Pour la manipulation,
		// il est préférable d'utiliser ct.getListenoeuds()
		// (=ct.getPopNoeuds().getElements()) qui renvoie directement une liste.
		obj.addFeatureCollection(ct.getPopFaces(),"Faces");
		obj.addFeatureCollection(ct.getPopArcs(),"Arcs");
		obj.addFeatureCollection(ct.getPopNoeuds(),"Noeuds");

	}

}
