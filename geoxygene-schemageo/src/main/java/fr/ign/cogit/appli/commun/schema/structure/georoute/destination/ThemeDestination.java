package fr.ign.cogit.appli.commun.schema.structure.georoute.destination;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class ThemeDestination extends DataSet  {

	/** Constructeur par d�faut */
	public ThemeDestination() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un th�me destination de G�oroute au format structur� :
	 * - ce th�me contient les populations "Equipement public"...  :
	 * - ces populations ont des noms par d�faut � ne pas changer.
	 * - leurs �l�ments se r�alisent dans des classes contr�tes du package nom_package.
	 * - un th�me peut �tre persistant ou non
	 * - un th�me a un nom logique (utile pour naviguer entre th�mes).
	 */
	public ThemeDestination(boolean persistance, String nom_package, DataSet DS) {
		super(DS);
		this.setTypeBD("Th�me destination de G�oroute");
		Population<?> pop;
		this.ojbConcreteClass = this.getClass().getName(); // n�cessaire pour ojb
		this.setNom("destination");
		if (persistance) DataSet.db.makePersistent(this);
		try{
			pop = new Population<EquipementPublic>(persistance, "Equipement public", Class.forName(nom_package+".EquipementPublic"),true);
			this.addPopulation(pop);
			pop = new Population<EquipementRoutier>(persistance, "Equipement routier", Class.forName(nom_package+".EquipementRoutier"),true);
			this.addPopulation(pop);
			pop = new Population<Acces>(persistance, "Acc�s", Class.forName(nom_package+".Acces"),true);
			this.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package);
		}
	}

	public IPopulation<?> getPopEquipementsPublics() {return this.getPopulation("Equipement public");}
	public IPopulation<?> getPopEquipementsRoutiers() {return this.getPopulation("Equipement routier");}
	public IPopulation<?> getPopAcces() {return this.getPopulation("Acc�s");}

}
