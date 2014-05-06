package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/** Classe mère pour toute classe d'éléments de la BDTopo Pays au format de livraison shape2D.
 * NB: un attribut Identifiant doit être rajouté dans le futur, mais
 * cela n'est pas encore fait.
 */

public abstract class ElementBDPays extends FT_Feature {

	protected String source;
	public String getSource() {return this.source; }
	public void setSource (String Source) {source = Source; }

}
