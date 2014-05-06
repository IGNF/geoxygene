package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;


public class ThemeRoutier extends DataSet {

	/** Constructeur par defaut */
	public ThemeRoutier() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un theme routier de Georoute au format structure :
	 * - ce theme contient les populations "troncons de route"...  :
	 * - ces populations ont des noms par defaut a ne pas changer.
	 * - leurs elements se realisent dans des classes contretes du package nom_package.
	 * - un theme peut etre persistant ou non
	 * - un theme a un nom logique (utile pour naviguer entre themes).
	 */
	public ThemeRoutier(boolean persistance, String nom_package, DataSet DS) {
		super(DS);
		this.setTypeBD("Theme routier de Georoute");
		Population<?> pop;
		this.ojbConcreteClass = this.getClass().getName(); // necessaire pour ojb
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
			System.out.println("Probleme de nom de package : "+nom_package);
		}
	}

	public IPopulation<?> getPopCarrefoursComplexes() {return this.getPopulation("Carrefour complexe");}
	public IPopulation<?> getPopNonCommunications() {return this.getPopulation("Non communication");}
	public IPopulation<?> getPopNoeuds() {return this.getPopulation("Noeud routier");}
	public IPopulation<?> getPopTroncons() {return this.getPopulation("Troncon de route");}

}
