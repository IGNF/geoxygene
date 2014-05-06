package fr.ign.cogit.appli.commun.schema.structure.georoute;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

public abstract class ElementGeoroute extends FT_Feature {

	/** Identifiant donné par Géoroute */
	protected int id_georoute;
	public int getId_georoute() {return this.id_georoute; }
	public void setId_georoute (int I) {id_georoute = I; }

	public String type;
	public String getType() {return type;}
	public void setType(String type) {this.type = type;}

	public static ElementGeoroute recherche_element_avec_idgeoroute(Population<ElementGeoroute> pop, int id) {
		ElementGeoroute element;
		Iterator<ElementGeoroute> it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = it.next();
			if ( element.getId_georoute() == id ) return element;
		}
		return null;
	}

}
