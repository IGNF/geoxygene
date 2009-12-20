/**
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

package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien Perret
 *
 */
public class Graphic {
	
	private List<ExternalGraphic> externalGraphics = new ArrayList<ExternalGraphic>();
	/**
	 * Renvoie la valeur de l'attribut externalGraphics.
	 * @return la valeur de l'attribut externalGraphics
	 */
	public List<ExternalGraphic> getExternalGraphics() {return this.externalGraphics;}
	/**
	 * Affecte la valeur de l'attribut externalGraphics.
	 * @param externalGraphics l'attribut externalGraphics � affecter
	 */
	public void setExternalGraphics(List<ExternalGraphic> externalGraphics) {this.externalGraphics = externalGraphics;}

	private List<Mark> marks = new ArrayList<Mark>();
	/**
	 * Renvoie la valeur de l'attribut marks.
	 * @return la valeur de l'attribut marks
	 */
	public List<Mark> getMarks() {return this.marks;}
	/**
	 * Affecte la valeur de l'attribut marks.
	 * @param marks l'attribut marks � affecter
	 */
	public void setMarks(List<Mark> marks) {this.marks = marks;}
	
	private float opacity=1.0f;
	/**
	 * Renvoie la valeur de l'attribut opacity.
	 * @return la valeur de l'attribut opacity
	 */
	public float getOpacity() {return this.opacity;}
	/**
	 * Affecte la valeur de l'attribut opacity.
	 * @param opacity l'attribut opacity � affecter
	 */
	public void setOpacity(float opacity) {this.opacity = opacity;}

	private float size=6.0f;
	/**
	 * Renvoie la valeur de l'attribut size.
	 * @return la valeur de l'attribut size
	 */
	public float getSize() {return this.size;}
	/**
	 * Affecte la valeur de l'attribut size.
	 * @param size l'attribut size � affecter
	 */
	public void setSize(float size) {this.size = size;}

	private float rotation=0.0f;
	/**
	 * Renvoie la valeur de l'attribut rotation.
	 * @return la valeur de l'attribut rotation
	 */
	public float getRotation() {return this.rotation;}
	/**
	 * Affecte la valeur de l'attribut rotation.
	 * @param rotation l'attribut rotation � affecter
	 */
	public void setRotation(float rotation) {this.rotation = rotation;}
}
