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

package fr.ign.cogit.geoxygene.contrib.geometrie;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


/**
 * Rectangle.
 * 
 * NB: utiliser autant que possible directement la classe GM_Envelope qui
 * représente aussi des rectangles.
 * Cette classe n'est là que pour optimiser quelques méthodes,
 * et est essentiellement utilisée pour l'index de dallage.
 * 
 * @author  Mustière
 * @version 1.0
 */

public class Rectangle {

	public double xmin ;
	public double xmax;
	public double ymin;
	public double ymax;

	public Rectangle() {
	}

	public static Rectangle rectangleEnglobant(GM_LineString L) {
		DirectPositionList listepoints = L.coord();
		DirectPosition point;
		Rectangle R = new Rectangle();
		R.xmin = listepoints.get(0).getX();
		R.xmax = listepoints.get(0).getX();
		R.ymin = listepoints.get(0).getY();
		R.ymax = listepoints.get(0).getY();

		for (int i=1;i<listepoints.size();i++) {
			point = listepoints.get(i);
			if ( point.getX() < R.xmin ) R.xmin = point.getX();
			if ( point.getX() > R.xmax ) R.xmax = point.getX();
			if ( point.getY() < R.ymin ) R.ymin = point.getY();
			if ( point.getY() > R.ymax ) R.ymax = point.getY();
		}
		return R;
	}

	public Rectangle dilate(double dilatation) {
		Rectangle R = new Rectangle();
		R.xmin = this.xmin - dilatation;
		R.xmax = this.xmax + dilatation;
		R.ymin = this.ymin - dilatation;
		R.ymax = this.ymax + dilatation;
		return R;
	}


	public boolean intersecte(Rectangle R) {
		boolean intersecteX = false;
		boolean intersecteY = false;
		if (R.xmin < this.xmin && R.xmax > this.xmin ) intersecteX = true;
		if (R.xmin > this.xmin && R.xmin < this.xmax ) intersecteX = true;
		if (R.ymin < this.ymin && R.ymax > this.ymin ) intersecteY = true;
		if (R.ymin > this.ymin && R.ymin < this.ymax ) intersecteY = true;
		return (intersecteX && intersecteY );
	}
}
