package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;


public class ThemeRoutier extends DataSet {

	/** Constructeur par d�faut */
	public ThemeRoutier() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un th�me routier de G�oroute au format structur� :
	 * - ce th�me contient les populations "troncons de route"...  :
	 * - ces populations ont des noms par d�faut � ne pas changer.
	 * - leurs �l�ments se r�alisent dans des classes contr�tes du package nom_package.
	 * - un th�me peut �tre persistant ou non
	 * - un th�me a un nom logique (utile pour naviguer entre th�mes).
	 */
	public ThemeRoutier(boolean persistance, String nom_package, DataSet DS) {
		super(DS);
		this.setTypeBD("Th�me routier de G�oroute");
		Population<?> pop;
		this.ojbConcreteClass = this.getClass().getName(); // n�cessaire pour ojb
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
			System.out.println("Probl�me de nom de package : "+nom_package);
		}
	}

	public IPopulation<?> getPopCarrefoursComplexes() {return this.getPopulation("Carrefour complexe");}
	public IPopulation<?> getPopNonCommunications() {return this.getPopulation("Non communication");}
	public IPopulation<?> getPopNoeuds() {return this.getPopulation("Noeud routier");}
	public IPopulation<?> getPopTroncons() {return this.getPopulation("Troncon de route");}

}
