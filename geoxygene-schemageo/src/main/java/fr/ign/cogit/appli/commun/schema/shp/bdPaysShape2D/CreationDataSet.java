package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.Batiment;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.Cimetiere;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.ConstructionLineaire;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.ConstructionPonctuelle;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.ConstructionSurfacique;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.PisteAerodrome;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.PointActiviteInteret;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.Reservoir;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.SurfaceActivite;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.activite_bati.TerrainDeSport;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.administratif.Commune;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.Hydronyme;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.LieuDitHabite;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.LieuDitNonHabite;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.Oronyme;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.ToponymeCommunication;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers.ToponymeDivers;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.energie_fluide.Canalisation;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.energie_fluide.LigneElectrique;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.energie_fluide.PosteTransformation;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.energie_fluide.Pylone;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ferre.AireDeTriage;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ferre.TransportParCable;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ferre.TronconVoieFerree;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.hydrographie.Laisse;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.hydrographie.PointDEau;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.hydrographie.SurfaceDEau;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.hydrographie.TronconDeCoursDEau;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.orographie.LigneOrographique;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.routier.SurfaceDeRoute;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.routier.TronconDeChemin;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.routier.TronconDeRoute;
import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.vegetation.ZoneArboree;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class CreationDataSet  {

	/** Création des thèmes et des populations des thèmes
	 * d'un jeu de données BDTopo Pays suivant le modèle défini par l'export Shape2D.
	 * 
	 * @param persistance : Définit si le jeu de données est persistant ou non.
	 * Si il est persistant, tous les thèmes et populations créées le sont
	 * aussi par défaut, mais cela peut être changé par la suite.
	 * 
	 * @param nom_package_jeu_complet : nom du package qui contient les classes concrètes
	 * des populations du jeu de données (chemin complet du package contenant
	 * les sous package des thèmes).
	 * 
	 * @param metadonnees : Liste de string (peut-être null) représentant dans l'ordre:
	 * - le nom logique de la base (texte libre pour la déco),
	 * - la date des données
	 * - la zone couverte par les données
	 * - un commentaire
	 * Tous les textes sont libres et d'au plus 255 caractères
	 * 
	 */
	public static DataSet nouveauDataSet(boolean persistance, String nom_package_jeu_complet, List<String> metadonnees) {
		DataSet jeu = new DataSet();
		if (persistance) DataSet.db.makePersistent(jeu);

		//métadonnées
		jeu.setTypeBD("BDPays");
		jeu.setModele("Shape 2D");
		if (metadonnees != null) {
			Iterator<String> itMD = metadonnees.iterator();
			if (itMD.hasNext()) jeu.setNom(itMD.next());
			if (itMD.hasNext()) jeu.setDate(itMD.next());
			if (itMD.hasNext()) jeu.setZone(itMD.next());
			if (itMD.hasNext()) jeu.setCommentaire(itMD.next());
		}

		// création des thèmes
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
		theme.setTypeBD("Thème 'Surface d'activité et bati' de la BDPays");
		theme.setNom("Bati");
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
			pop = new Population<Reservoir>(persistance, "Réservoirs", Class.forName(nom_package+".Reservoir"),true);
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

