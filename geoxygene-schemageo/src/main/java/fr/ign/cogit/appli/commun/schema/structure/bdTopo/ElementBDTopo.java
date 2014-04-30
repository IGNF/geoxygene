package fr.ign.cogit.appli.commun.schema.structure.bdTopo;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/** Classe mère pour toute classe d'éléments de la BDTopo Pays
 * AU FORMAT DE TRAVAIL pour l'appariement.
 */

public abstract class ElementBDTopo extends FT_Feature {

	protected String source;
	public String getSource() {return this.source; }
	public void setSource (String Source) {source = Source; }

}
