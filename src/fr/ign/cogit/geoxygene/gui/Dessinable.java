/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.gui;

import java.awt.Graphics2D;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;


/**
 * @author Julien Perret
 *
 */
public interface Dessinable {
	
	/**
	 * Géometrie en coordonnées géographiques de la fenetre d'affichage du cadre : tous les objets intersectant ce cadre seront affichés
	 * @return Géometrie en coordonnées géographiques de la fenetre d'affichage du cadre
	 */
	public GM_Envelope getEnveloppeAffichage();
	/**
	 * @return the center of this Dessinalble in Model coordinates
	 */
	public DirectPosition getCentreGeo();
	/**
	 * @param centreGeo
	 */
	public void setCentreGeo(DirectPosition centreGeo);
	/**
	 * @return the size of one pixel in meters
	 */
	public double getTaillePixel();
	/**
	 * @param tp
	 */
	public void setTaillePixel(double tp);
	
	/**
	 * met a jour le champ 'enveloppeAffichage' qui le rectangle de la fenetre de viualisation en coordonnees geographiques, en fonction des
	 * coordonnees du centre de la vue 'centreGeo' et du facteur de zoom 'taillePixel'
	 * 'enveloppeAffichage' est utilise pour determiner les objets a tracer dans la vue (ceux qui l'intersectent)
	 */
	public void majLimitesAffichage(int width, int height);
	
	/**
	 * 
	 */
	public void dessiner(Graphics2D g) throws InterruptedException;
}
