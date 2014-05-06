package fr.ign.cogit.appli.commun.schema.structure.georoute.destination;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class ThemeDestination extends DataSet  {

	/** Constructeur par defaut */
	public ThemeDestination() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un theme destination de Georoute au format structure :
	 * - ce theme contient les populations "Equipement public"...  :
	 * - ces populations ont des noms par defaut a ne pas changer.
	 * - leurs elements se realisent dans des classes contretes du package nom_package.
	 * - un theme peut etre persistant ou non
	 * - un theme a un nom logique (utile pour naviguer entre themes).
	 */
	public ThemeDestination(boolean persistance, String nom_package, DataSet DS) {
		super(DS);
		this.setTypeBD("Theme destination de Georoute");
		Population<?> pop;
		this.ojbConcreteClass = this.getClass().getName(); // necessaire pour ojb
		this.setNom("destination");
		if (persistance) DataSet.db.makePersistent(this);
		try{
			pop = new Population<EquipementPublic>(persistance, "Equipement public", Class.forName(nom_package+".EquipementPublic"),true);
			this.addPopulation(pop);
			pop = new Population<EquipementRoutier>(persistance, "Equipement routier", Class.forName(nom_package+".EquipementRoutier"),true);
			this.addPopulation(pop);
			pop = new Population<Acces>(persistance, "Acces", Class.forName(nom_package+".Acces"),true);
			this.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probleme de nom de package : "+nom_package);
		}
	}

	public IPopulation<?> getPopEquipementsPublics() {return this.getPopulation("Equipement public");}
	public IPopulation<?> getPopEquipementsRoutiers() {return this.getPopulation("Equipement routier");}
	public IPopulation<?> getPopAcces() {return this.getPopulation("Acces");}

}
