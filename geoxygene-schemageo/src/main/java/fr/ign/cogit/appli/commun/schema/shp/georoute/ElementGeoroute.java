package fr.ign.cogit.appli.commun.schema.shp.georoute;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

public abstract class ElementGeoroute extends FT_Feature {

	protected String typ_donnee;
	public String getTyp_donnee() {return this.typ_donnee; }
	public void setTyp_donnee (String Typ_donnee) {typ_donnee = Typ_donnee; }

	protected double id_georout;
	public double getId_georout() {return this.id_georout; }
	public void setId_georout (double Id_georout) {id_georout = Id_georout; }

	public static ElementGeoroute recherche_element_avec_idgeoroute(Population<ElementGeoroute> pop, double id) {
		ElementGeoroute element;
		Iterator<ElementGeoroute> it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = it.next();
			if ( element.getId_georout() == id ) return element;
		}
		return null;
	}

}
