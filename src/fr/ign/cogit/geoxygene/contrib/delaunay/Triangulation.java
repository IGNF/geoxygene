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

package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe mère de la triangulation.
 * @author Bonin
 * @version 1.0
 */


public class Triangulation extends CarteTopo{

	public Triangulation() {}

	public Triangulation(String nom_logique) {
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
		this.setNom(nom_logique);
		this.setPersistant(false);
		Population arcs = new Population(false, "Arc", ArcDelaunay.class,true);
		this.addPopulation(arcs);
		Population noeuds = new Population(false, "Noeud", NoeudDelaunay.class,true);
		this.addPopulation(noeuds);
		Population faces = new Population(false, "Face", TriangleDelaunay.class,true);
		this.addPopulation(faces);
	}

	private Triangulateio jin = new Triangulateio();
	private Triangulateio jout = new Triangulateio();
	private Triangulateio jvorout = new Triangulateio();
	private String options = null;

	private void convertJin() {
		int i;
		NoeudDelaunay node;
		GM_Point point;
		DirectPosition coord;
		ArrayList noeuds = new ArrayList(this.getListeNoeuds());

		jin.numberofpoints = noeuds.size();
		jin.pointlist = new double[2*jin.numberofpoints];
		for (i=0; i<noeuds.size(); i++) {
			node = (NoeudDelaunay) noeuds.get(i);
			point = node.getGeometrie();
			coord = point.getPosition();
			jin.pointlist[2*i]=coord.getX();
			jin.pointlist[2*i+1]=coord.getY();
		}    
	}

	private void convertJinSegments() {
		int i;
		ArrayList noeuds = new ArrayList(this.getListeNoeuds());
		ArrayList aretes = new ArrayList(this.getListeArcs());
		jin.numberofsegments = aretes.size();
		jin.segmentlist = new int[2*jin.numberofsegments];
		for (i=0; i<jin.numberofsegments; i++) {
			jin.segmentlist[2*i]=noeuds.indexOf(((ArcDelaunay)aretes.get(i)).getNoeudIni());
			jin.segmentlist[2*i+1]=noeuds.indexOf(((ArcDelaunay)aretes.get(i)).getNoeudFin());
		}

	}

	private void convertJout() {
		try {
			TriangleDelaunay tri;
			ArcDelaunay are;
			NoeudDelaunay noe;
			int i;
			GM_LineString ls;

			for (i=jin.numberofpoints; i<jout.numberofpoints; i++) {

				noe = (NoeudDelaunay)this.getPopNoeuds().nouvelElement();
				noe.setCoord(new DirectPosition(jout.pointlist[2*i],jout.pointlist[2*i+1]));
				this.addNoeud(noe);
			}

			ArrayList noeuds = new ArrayList(this.getListeNoeuds());

			Class[] signaturea = {this.getPopNoeuds().getClasse(),this.getPopNoeuds().getClasse()};
			Object[] parama = new Object[2];

			for (i=0; i<jout.numberofedges; i++) {
				parama[0] = (NoeudDelaunay) noeuds.get(jout.edgelist[2*i]);
				parama[1] = (NoeudDelaunay) noeuds.get(jout.edgelist[2*i+1]);
				are = (ArcDelaunay)this.getPopArcs().nouvelElement(signaturea,parama);
			}


			Class [] signaturef = {this.getPopNoeuds().getClasse(),this.getPopNoeuds().getClasse(),
					this.getPopNoeuds().getClasse()};
			Object[] paramf = new Object[3];

			for (i=0; i<jout.numberoftriangles; i++) {
				paramf[0] = (NoeudDelaunay) noeuds.get(jout.trianglelist[3*i]);
				paramf[1] = (NoeudDelaunay) noeuds.get(jout.trianglelist[3*i+1]);
				paramf[2] = (NoeudDelaunay) noeuds.get(jout.trianglelist[3*i+2]);
				tri = (TriangleDelaunay)this.getPopFaces().nouvelElement(signaturef,paramf);
				tri.setId(i);
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}

	///Méthode de triangulation proprment dite en C - va chercher la dll
	private native void trianguleC(String options, Triangulateio jin, Triangulateio jout, Triangulateio jvorout);
	static {
		System.loadLibrary("trianguledll");
	}

	public void lanceTriangulation(String options) {
		this.options = options;
		this.convertJin(); 
		if (options.indexOf('p') != -1) {
			this.convertJinSegments(); 
			this.getPopArcs().setElements(new ArrayList());
		}
		trianguleC(options, jin, jout, null);
		convertJout();
	}

	public void triangule() throws Exception{
		this.lanceTriangulation("czeB");
		System.out.println("Triangulation terminée");
	}

}