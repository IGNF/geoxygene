package fr.ign.cogit.appli.commun.schema.shp.georoute.routier;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class ThemeRoutier extends DataSet {

	/** Constructeur par défaut */
	public ThemeRoutier() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un thème routier de Géoroute au format shape :
	 * - ce thème contient les populations "troncons de route"...  :
	 * - ces populations ont des noms par défaut à ne pas changer.
	 * - leurs éléments se réalisent dans des classes contrètes du package nom_package.
	 * - un thème peut être persistant ou non
	 * - un thème a un nom logique (utile pour naviguer entre thèmes).
	 */
	public ThemeRoutier(boolean persistance, String nom_package, DataSet DS) {
		super(DS);
		this.setTypeBD("Thème routier de Géoroute");
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
		Population<?> pop;
		this.setNom("routier");
		if (persistance) DataSet.db.makePersistent(this);
		try{
			pop = new Population<CarrefourComplexe>(persistance, "Carrefour complexe", Class.forName(nom_package+".CarrefourComplexe"),true);
			this.addPopulation(pop);
			pop = new Population<NonCommunication>(persistance, "Non communication", Class.forName(nom_package+".NonCommunication"),true);
			this.addPopulation(pop);
			pop = new Population<NoeudRoutier>(persistance, "Noeud routier", Class.forName(nom_package+".NoeudRoutier"),true);
			this.addPopulation(pop);
			pop = new Population<TronconRoute>(persistance, "Troncon de route", Class.forName(nom_package+".TronconRoute"),true);
			this.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package);
		}
	}



	public IPopulation<?> getPopCarrefoursComplexes() {return this.getPopulation("Carrefour complexe");}
	public IPopulation<?> getPopNonCommunications() {return this.getPopulation("Non communication");}
	public IPopulation<?> getPopNoeuds() {return this.getPopulation("Noeud routier");}
	public IPopulation<?> getPopTroncons() {return this.getPopulation("Troncon de route");}

}
