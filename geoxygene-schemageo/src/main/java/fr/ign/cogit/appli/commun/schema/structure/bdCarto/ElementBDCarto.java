package fr.ign.cogit.appli.commun.schema.structure.bdCarto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;

/** Classe mere pour toute classe de la BDCarto V2 au format structure de la BDCarto.
 * Document de reference: specifications de contenu BDCarto (schemas HBDS), version 2, revision 4, 2001.
 */

@SuppressWarnings("unchecked")

public abstract class ElementBDCarto extends FT_Feature {

	/** Identifiant donne par la BDCarto */
	protected int id_bdcarto;
	public int getId_bdcarto() {return this.id_bdcarto; }
	public void setId_bdcarto (int Id_bdcarto) {id_bdcarto = Id_bdcarto; }


	public static ElementBDCarto recherche_element_avec_idbdc(Population pop, int id) {
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) return element;
		}
		return null;
	}

	public static List search_list_idBDCarto (Population pop,int id){
		List list_idBDCarto = new ArrayList();
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) list_idBDCarto.add(element);
		}
		if (list_idBDCarto == null) return null;
		return list_idBDCarto;
	}

	public static ElementBDCarto recherche_element_avec_idbdc(FT_FeatureCollection pop, int id) {
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) return element;
		}
		return null;
	}

	public static List search_list_idBDCarto (FT_FeatureCollection pop,int id){
		List list_idBDCarto = new ArrayList();
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) list_idBDCarto.add(element);
		}
		if (list_idBDCarto == null) return null;
		return list_idBDCarto;
	}
}
