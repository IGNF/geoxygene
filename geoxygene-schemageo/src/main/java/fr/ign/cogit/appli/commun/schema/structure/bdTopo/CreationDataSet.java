package fr.ign.cogit.appli.commun.schema.structure.bdTopo;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.Batiment;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.Cimetiere;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.ConstructionLineaire;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.ConstructionPonctuelle;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.ConstructionSurfacique;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.PisteAerodrome;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.PointActiviteInteret;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.Reservoir;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.SurfaceActivite;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati.TerrainDeSport;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.administratif.Commune;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.Hydronyme;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.LieuDitHabite;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.LieuDitNonHabite;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.Oronyme;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.ToponymeCommunication;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.divers.ToponymeDivers;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide.Canalisation;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide.LigneElectrique;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide.PosteTransformation;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide.Pylone;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ferre.AireDeTriage;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ferre.TransportParCable;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ferre.TronconVoieFerree;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.hydrographie.Laisse;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.hydrographie.PointDEau;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.hydrographie.SurfaceDEau;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.hydrographie.TronconDeCoursDEau;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.orographie.LigneOrographique;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.routier.SurfaceDeRoute;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.routier.TronconDeChemin;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.routier.TronconDeRoute;
import fr.ign.cogit.appli.commun.schema.structure.bdTopo.vegetation.ZoneArboree;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class CreationDataSet  {

	/** Creation des themes et des populations des themes
	 * d'un jeu de donnees BDTopo Pays suivant le modele interne IGN (idem export shape en fait).
	 * 
	 * @param persistance : Definit si le jeu de donnees est persistant ou non.
	 * Si il est persistant, tous les themes et populations creees le sont
	 * aussi par defaut, mais cela peut etre change par la suite.
	 * 
	 * @param nom_package_jeu_complet : nom du package qui contient les classes concretes
	 * des populations du jeu de donnees (chemin complet du package contenant
	 * les sous package des themes).
	 * 
	 * @param metadonnees : Liste de string (peut-etre null) representant dans l'ordre:
	 * - le nom logique de la base (texte libre pour la deco),
	 * - la date des donnees
	 * - la zone couverte par les donnees
	 * - un commentaire
	 * Tous les textes sont libres et d'au plus 255 caracteres
	 * 
	 */
	public static DataSet nouveauDataSet(boolean persistance, String nom_package_jeu_complet, List<String> metadonnees) {
		DataSet jeu = new DataSet();
		if (persistance) DataSet.db.makePersistent(jeu);

		//metadonnees
		jeu.setTypeBD("BDPays");
		jeu.setModele("Structure");
		if (metadonnees != null) {
			Iterator<String> itMD = metadonnees.iterator();
			if (itMD.hasNext()) jeu.setNom(itMD.next());
			if (itMD.hasNext()) jeu.setDate(itMD.next());
			if (itMD.hasNext()) jeu.setZone(itMD.next());
			if (itMD.hasNext()) jeu.setCommentaire(itMD.next());
		}

		// creation des themes
		ajouteThemeBati(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeAdmin(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeDivers(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeEnergie(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeFerre(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeHydrographie(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeOrographie(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeRoutier(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeVegetation(jeu, nom_package_jeu_complet, persistance);

		return jeu;
	}


	public static void ajouteThemeBati(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Surface d'activité et bâti' de la BDPays");
		theme.setNom("Bâti");
		String nom_package = nom_package_jeu_complet+".activite_bati";
		try{
			Population<?> pop;
			pop = new Population<Batiment>(persistance, "Bâtiments", Class.forName(nom_package+".Batiment"),true);
			theme.addPopulation(pop);
			pop = new Population<Cimetiere>(persistance, "Cimetières", Class.forName(nom_package+".Cimetiere"),true);
			theme.addPopulation(pop);
			pop = new Population<ConstructionLineaire>(persistance, "Constructions linéaires", Class.forName(nom_package+".ConstructionLineaire"),true);
			theme.addPopulation(pop);
			pop = new Population<ConstructionPonctuelle>(persistance, "Constructions ponctuelles", Class.forName(nom_package+".ConstructionPonctuelle"),true);
			theme.addPopulation(pop);
			pop = new Population<ConstructionSurfacique>(persistance, "Constructions surfaciques", Class.forName(nom_package+".ConstructionSurfacique"),true);
			theme.addPopulation(pop);
			pop = new Population<PisteAerodrome>(persistance, "Pistes d'aérodrome", Class.forName(nom_package+".PisteAerodrome"),true);
			theme.addPopulation(pop);
			pop = new Population<PointActiviteInteret>(persistance, "Points d'activité ou d'intérêt", Class.forName(nom_package+".PointActiviteInteret"),true);
			theme.addPopulation(pop);
			pop = new Population<Reservoir>(persistance, "Reservoirs", Class.forName(nom_package+".Reservoir"),true);
			theme.addPopulation(pop);
			pop = new Population<SurfaceActivite>(persistance, "Surfaces d'activité", Class.forName(nom_package+".SurfaceActivite"),true);
			theme.addPopulation(pop);
			pop = new Population<TerrainDeSport>(persistance, "Terrains de sport", Class.forName(nom_package+".TerrainDeSport"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeAdmin(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Administratif' de la BDPays");
		theme.setNom("Administratif");
		String nom_package = nom_package_jeu_complet+".administratif";
		try{
			Population<?> pop;
			pop = new Population<Commune>(persistance, "Communes", Class.forName(nom_package+".Commune"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeDivers(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Objets Divers' de la BDPays");
		theme.setNom("Divers");
		String nom_package = nom_package_jeu_complet+".divers";
		try{
			Population<?> pop;
			pop = new Population<Hydronyme>(persistance, "Hydronymes", Class.forName(nom_package+".Hydronyme"),true);
			theme.addPopulation(pop);
			pop = new Population<LieuDitHabite>(persistance, "Lieux-dits habités", Class.forName(nom_package+".LieuDitHabite"),true);
			theme.addPopulation(pop);
			pop = new Population<LieuDitNonHabite>(persistance, "Lieux-dits non habités", Class.forName(nom_package+".LieuDitNonHabite"),true);
			theme.addPopulation(pop);
			pop = new Population<Oronyme>(persistance, "Oronymes", Class.forName(nom_package+".Oronyme"),true);
			theme.addPopulation(pop);
			pop = new Population<ToponymeCommunication>(persistance, "Toponymes communication", Class.forName(nom_package+".ToponymeCommunication"),true);
			theme.addPopulation(pop);
			pop = new Population<ToponymeDivers>(persistance, "Toponymes divers", Class.forName(nom_package+".ToponymeDivers"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeEnergie(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Transport d'énergie et de fluides' de la BDPays");
		theme.setNom("Energie");
		String nom_package = nom_package_jeu_complet+".energie_fluide";
		try{
			Population<?> pop;
			pop = new Population<Canalisation>(persistance, "Canalisation", Class.forName(nom_package+".Canalisation"),true);
			theme.addPopulation(pop);
			pop = new Population<LigneElectrique>(persistance, "Ligne électrique", Class.forName(nom_package+".LigneElectrique"),true);
			theme.addPopulation(pop);
			pop = new Population<PosteTransformation>(persistance, "Poste de transformation", Class.forName(nom_package+".PosteTransformation"),true);
			theme.addPopulation(pop);
			pop = new Population<Pylone>(persistance, "Pylone", Class.forName(nom_package+".Pylone"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeFerre(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Voies ferrées et autres moyens de transport terrestre' de la BDPays");
		theme.setNom("Ferré");
		String nom_package = nom_package_jeu_complet+".ferre";
		try{
			Population<?> pop;
			pop = new Population<TronconVoieFerree>(persistance, "Tronçons de voies ferrées", Class.forName(nom_package+".TronconVoieFerree"),true);
			theme.addPopulation(pop);
			pop = new Population<AireDeTriage>(persistance, "Aires de triage", Class.forName(nom_package+".AireDeTriage"),true);
			theme.addPopulation(pop);
			pop = new Population<TransportParCable>(persistance, "Transports par câble", Class.forName(nom_package+".TransportParCable"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeHydrographie(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Hydrographie terrestre' de la BDPays");
		theme.setNom("Hydrographie");
		String nom_package = nom_package_jeu_complet+".hydrographie";
		try{
			Population<?> pop;
			pop = new Population<Laisse>(persistance, "Tronçons de laisse", Class.forName(nom_package+".Laisse"),true);
			theme.addPopulation(pop);
			pop = new Population<PointDEau>(persistance, "Points d'eau", Class.forName(nom_package+".PointDEau"),true);
			theme.addPopulation(pop);
			pop = new Population<SurfaceDEau>(persistance, "Surfaces d'eau", Class.forName(nom_package+".SurfaceDEau"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconDeCoursDEau>(persistance, "Tronçons de cours d'eau", Class.forName(nom_package+".TronconDeCoursDEau"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeOrographie(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Orographie' de la BDPays");
		theme.setNom("Orographie");
		String nom_package = nom_package_jeu_complet+".orographie";
		try{
			Population<?> pop;
			pop = new Population<LigneOrographique>(persistance, "Lignes orographiques", Class.forName(nom_package+".LigneOrographique"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeRoutier(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Voies de Communication Routière' de la BDPays");
		theme.setNom("Routier");
		String nom_package = nom_package_jeu_complet+".routier";
		try{
			Population<?> pop;
			pop = new Population<SurfaceDeRoute>(persistance, "Surfaces de route", Class.forName(nom_package+".SurfaceDeRoute"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconDeRoute>(persistance, "Tronçons de route", Class.forName(nom_package+".TronconDeRoute"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconDeChemin>(persistance, "Tronçons de chemin", Class.forName(nom_package+".TronconDeChemin"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeVegetation(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Végétation' de la BDPays");
		theme.setNom("Végétation");
		String nom_package = nom_package_jeu_complet+".vegetation";
		try{
			Population<?> pop;
			pop = new Population<ZoneArboree>(persistance, "Zones arborées", Class.forName(nom_package+".ZoneArboree"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}


}

