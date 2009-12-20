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

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author Julien Perret
 *
 */
public class ExternalGraphic {
	
	private String href;
	/**
	 * Renvoie la valeur de l'attribut href.
	 * @return la valeur de l'attribut href
	 */
	public String getHref() {return this.href;}
	/**
	 * Affecte la valeur de l'attribut href.
	 * @param href l'attribut href � affecter
	 */
	public void setHref(String href) {this.href = href;}

	private String format;
	/**
	 * Renvoie la valeur de l'attribut format.
	 * @return la valeur de l'attribut format
	 */
	public String getFormat() {return this.format;}
	/**
	 * Affecte la valeur de l'attribut format.
	 * @param format l'attribut format � affecter
	 */
	public void setFormat(String format) {this.format = format;}

	private Image onlineResource = null;
	/**
	 * Renvoie la valeur de l'attribut onlineResource.
	 * @return la valeur de l'attribut onlineResource
	 */
	public Image getOnlineResource() {
		if (this.onlineResource==null)
			try {this.onlineResource=ImageIO.read(new URL(this.href));}
			catch (IOException e) {e.printStackTrace();}
		return this.onlineResource;
	}

	/**
	 * Affecte la valeur de l'attribut onlineResource.
	 * @param onlineResource l'attribut onlineResource � affecter
	 */
	public void setOnlineResource(Image onlineResource) {this.onlineResource = onlineResource;}

}
