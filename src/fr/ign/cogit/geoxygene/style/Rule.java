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

package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.filter.Filter;

/**
 * @author Julien Perret
 *
 */
public class Rule {
	
	private String name;

	/**
	 * Renvoie la valeur de l'attribut name.
	 * @return la valeur de l'attribut name
	 */
	public String getName() {return this.name;}

	/**
	 * Affecte la valeur de l'attribut name.
	 * @param name l'attribut name à affecter
	 */
	public void setName(String name) {this.name = name;}
	
	private String title;

	/**
	 * Renvoie la valeur de l'attribut title.
	 * @return la valeur de l'attribut title
	 */
	public String getTitle() {return this.title;}

	/**
	 * Affecte la valeur de l'attribut title.
	 * @param title l'attribut title à affecter
	 */
	public void setTitle(String title) {this.title = title;}
	
	private Filter filter = null;

	/**
	 * Renvoie la valeur de l'attribut filter.
	 * @return la valeur de l'attribut filter
	 */
	public Filter getFilter() {return this.filter;}

	/**
	 * Affecte la valeur de l'attribut filter.
	 * @param filter l'attribut filter à affecter
	 */
	public void setFilter(Filter filter) {this.filter = filter;}
	
	private List<Symbolizer> symbolizers = new ArrayList<Symbolizer>();

	/**
	 * Renvoie la valeur de l'attribut symbolizers.
	 * @return la valeur de l'attribut symbolizers
	 */
	public List<Symbolizer> getSymbolizers() {return this.symbolizers;}

	/**
	 * Affecte la valeur de l'attribut symbolizers.
	 * @param symbolizers l'attribut symbolizers à affecter
	 */
	public void setSymbolizers(List<Symbolizer> symbolizers) {this.symbolizers = symbolizers;}

	@Override
	public String toString() {
		String result = "Rule "+this.getName()+" - "+this.getTitle()+"\n";
		result+="\tFilter "+this.getFilter()+"\n";
		for (Symbolizer symbolizer:this.getSymbolizers()) {
			result+="\tSymbolizer "+symbolizer+"\n";
		}
		return result;
	}
}
