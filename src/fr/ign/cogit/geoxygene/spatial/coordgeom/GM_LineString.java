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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

//import operateur.OpDirectPosition;

/**
 * Polyligne.
 * L'attribut "interpolation" est égal à "linear".
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 * 19.02.2007 : correction de bug constructeur à partir d'une liste de DirectPosition
 * 
 */

public class GM_LineString extends GM_CurveSegment {

	//////////////////////////////////////////////////////////////////////////
	// Attribut "controlPoint" et méthodes pour le traiter ///////////////////
	//////////////////////////////////////////////////////////////////////////
	/** Points pour le dessin de la polyligne : séquence de DirectPosition.
        Le premier point est le startPoint de la polyligne. */
	protected DirectPositionList controlPoint;

	/** Renvoie la liste conbtrolPoint. Equivalent de samplePoint() et de coord(). A laisser ?*/
	public DirectPositionList getControlPoint() {return controlPoint;}

	/** Renvoie le DirectPosition de rang i. */
	public DirectPosition getControlPoint (int i) {return this.controlPoint.get(i);}

	/** Affecte un DirectPosition au i-ème rang de la liste. */
	public void setControlPoint (int i, DirectPosition value) {this.controlPoint.set(i, value);}

	/** Ajoute un DirectPosition en fin de liste */
	public void addControlPoint (DirectPosition value) {this.controlPoint.add(value);}

	/** Ajoute un DirectPosition au i-ème rang de la liste. */
	public void addControlPoint (int i, DirectPosition value) {this.controlPoint.add(i, value);}

	/** Efface de la liste le DirectPosition passé en paramètre. */
	public void removeControlPoint (DirectPosition value)  {this.controlPoint.remove(value);}

	/** Efface le i-ème DirectPosition de la liste. */
	public void removeControlPoint (int i)  {this.controlPoint.remove(i);}

	/** Renvoie le nombre de DirectPosition. */
	public int sizeControlPoint () {return this.controlPoint.size();}

	//////////////////////////////////////////////////////////////////////////
	// Constructeurs /////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	/** Constructeur par défaut.*/
	public GM_LineString() {this(new DirectPositionList());}

	/** Constructeur à partir d'une liste de DirectPosition.*/
	public GM_LineString(DirectPositionList points) {
		super();
		segment.add(this);
		controlPoint = new DirectPositionList();
		controlPoint.addAll(points);
		interpolation = "linear";
	}

	//////////////////////////////////////////////////////////////////////////
	// Méthode de la norme ///////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	/** 
	 * TODO Renvoie null.
	 * Décompose une polyligne en une séquence de segments.
	 */
	public List<GM_LineSegment> asGM_LineSegment() {return null;}




	//////////////////////////////////////////////////////////////////////////
	// Implémentation de méthodes abstraites /////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	/**  Renvoie la liste ordonnée des points de contrôle (idem que coord()).   */
	@Override
	public DirectPositionList coord() {return controlPoint;}

	/** Renvoie un GM_CurveSegment de sens opposé. */
	@Override
	public GM_CurveSegment reverse() {
		GM_LineString result = new GM_LineString();
		int n = controlPoint.size();
		for (int i=0; i<n; i++) result.getControlPoint().add(controlPoint.get(n-1-i));
		return result;
	}

	  /**
	   * Verifie si la ligne est fermee ou non. La ligne est fermee lorsque les deux points extremes ont la meme position
	 * @return
	 */
	public boolean isClosed(double tolerance) {
		if (isEmpty()) return false;
		return coord().get(0).equals2D( coord().get(coord().size()-1), tolerance );
	}

	/**
	 * @return
	 */
	public boolean isClosed() {return isClosed(0);}

	@Override
	public Object clone() {return new GM_LineString((DirectPositionList) controlPoint.clone());}

	@Override
	public boolean isLineString() {return true;}

}
