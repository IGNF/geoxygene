package fr.ign.cogit.appli.commun.schema.shp.bdcarto;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif.Arrondissement;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif.Canton;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif.Commune;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif.Departement;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif.LimiteAdministrative;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.Aerodrome;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.Cimetiere;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.ConstructionElevee;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.Digue;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.EnceinteMilitaire;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.LigneElectrique;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.MetroAerien;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.PisteAerodrome;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements.TransportParCable;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ferre.NoeudFerre;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ferre.TronconVoieFerree;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.habillage.ZoneOccupationDuSol;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie.Laisse;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie.ObjetHydrographiquePonctuel;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie.ToponymeSurfaceHydrographique;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie.TronconHydrographique;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie.ZoneHydrographiqueTexture;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.AccesEquipement;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.CommunicationRestreinte;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.DebutSection;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.EquipementRoutier;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.Franchissement;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.Itineraire;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.LiaisonMaritime;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.NoeudRoutier;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier.TronconRoute;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.Etablissement;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.GR;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.MassifBoise;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.PointRemarquableDuRelief;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.ZoneActivite;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.ZoneHabitat;
import fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes.ZoneReglementeeTouristique;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class CreationDataSet  {

	/** Création des thèmes et des populations des thèmes
	 * d'un jeu de données BDCarto suivant le modèle défini par l'export Shape.
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
		jeu.setTypeBD("BDCarto");
		jeu.setModele("Shape");
		if (metadonnees != null) {
			Iterator<String> itMD = metadonnees.iterator();
			if (itMD.hasNext()) jeu.setNom(itMD.next());
			if (itMD.hasNext()) jeu.setDate(itMD.next());
			if (itMD.hasNext()) jeu.setZone(itMD.next());
			if (itMD.hasNext()) jeu.setCommentaire(itMD.next());
		}

		// création des thèmes
		ajouteThemeAdmin(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeEquipements(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeFerre(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeHabillage(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeHydrographie(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeRoutier(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeToponymes(jeu, nom_package_jeu_complet, persistance);

		return jeu;
	}

	public static void ajouteThemeAdmin(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Administratif' de la BDCarto");
		theme.setNom("Administratif");
		String nom_package = nom_package_jeu_complet+".administratif";
		try{
			Population<?> pop;
			pop = new Population<Arrondissement>(persistance, "Arrondissements", Class.forName(nom_package+".Arrondissement"),true);
			theme.addPopulation(pop);
			pop = new Population<Canton>(persistance, "Cantons", Class.forName(nom_package+".Canton"),true);
			theme.addPopulation(pop);
			pop = new Population<Commune>(persistance, "Communes", Class.forName(nom_package+".Commune"),true);
			theme.addPopulation(pop);
			pop = new Population<Departement>(persistance, "Départements", Class.forName(nom_package+".Departement"),true);
			theme.addPopulation(pop);
			pop = new Population<LimiteAdministrative>(persistance, "Limites administratives", Class.forName(nom_package+".LimiteAdministrative"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeEquipements(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Equipements' de la BDCarto");
		theme.setNom("Equipements");
		String nom_package = nom_package_jeu_complet+".equipements";
		try{
			Population<?> pop;
			pop = new Population<Aerodrome>(persistance, "Aérodromes", Class.forName(nom_package+".Aerodrome"),true);
			theme.addPopulation(pop);
			pop = new Population<Cimetiere>(persistance, "Cimetières", Class.forName(nom_package+".Cimetiere"),true);
			theme.addPopulation(pop);
			pop = new Population<ConstructionElevee>(persistance, "Constructions élevées", Class.forName(nom_package+".ConstructionElevee"),true);
			theme.addPopulation(pop);
			pop = new Population<EnceinteMilitaire>(persistance, "Enceintes militaires", Class.forName(nom_package+".EnceinteMilitaire"),true);
			theme.addPopulation(pop);
			pop = new Population<LigneElectrique>(persistance, "Lignes électriques", Class.forName(nom_package+".LigneElectrique"),true);
			theme.addPopulation(pop);
			pop = new Population<PisteAerodrome>(persistance, "Pistes d'aérodromes", Class.forName(nom_package+".PisteAerodrome"),true);
			theme.addPopulation(pop);
			pop = new Population<Digue>(persistance, "Digues", Class.forName(nom_package+".Digue"),true);
			theme.addPopulation(pop);
			pop = new Population<TransportParCable>(persistance, "Transports par cable", Class.forName(nom_package+".TransportParCable"),true);
			theme.addPopulation(pop);
			pop = new Population<MetroAerien>(persistance, "Métros aériens", Class.forName(nom_package+".MetroAerien"),true);
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
		theme.setTypeBD("Thème 'Réseau ferré' de la BDCarto");
		theme.setNom("Ferré");
		String nom_package = nom_package_jeu_complet+".ferre";
		try{
			Population<?> pop;
			pop = new Population<NoeudFerre>(persistance, "Noeuds ferrés", Class.forName(nom_package+".NoeudFerre"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconVoieFerree>(persistance, "Tronçons de voie ferrée", Class.forName(nom_package+".TronconVoieFerree"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeHabillage(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Habillage' de la BDCarto");
		theme.setNom("Habillage");
		String nom_package = nom_package_jeu_complet+".habillage";
		try{
			Population<?> pop;
			pop = new Population<ZoneOccupationDuSol>(persistance, "Zones d'occupation du sol", Class.forName(nom_package+".ZoneOccupationDuSol"),true);
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
		theme.setTypeBD("Thème 'Hydrographie' de la BDCarto");
		theme.setNom("Hydrographie");
		String nom_package = nom_package_jeu_complet+".hydrographie";
		try{
			Population<?> pop;
			pop = new Population<TronconHydrographique>(persistance, "Tronçons hydrographiques", Class.forName(nom_package+".TronconHydrographique"),true);
			theme.addPopulation(pop);
			pop = new Population<ObjetHydrographiquePonctuel>(persistance, "Objets hydrographiques ponctuels", Class.forName(nom_package+".ObjetHydrographiquePonctuel"),true);
			theme.addPopulation(pop);
			pop = new Population<ToponymeSurfaceHydrographique>(persistance, "Toponymes de surface hydrographique", Class.forName(nom_package+".ToponymeSurfaceHydrographique"),true);
			theme.addPopulation(pop);
			pop = new Population<Laisse>(persistance, "Tronçons de laisse", Class.forName(nom_package+".Laisse"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneHydrographiqueTexture>(persistance, "Zones hydrographiques de texture", Class.forName(nom_package+".ZoneHydrographiqueTexture"),true);
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
		theme.setTypeBD("Thème 'Réseau routier et franchissements' de la BDCarto");
		theme.setNom("Routier");
		String nom_package = nom_package_jeu_complet+".routier";
		try{
			Population<?> pop;
			pop = new Population<AccesEquipement>(persistance, "Accès équipements", Class.forName(nom_package+".AccesEquipement"),true);
			theme.addPopulation(pop);
			pop = new Population<CommunicationRestreinte>(persistance, "Communications restreintes", Class.forName(nom_package+".CommunicationRestreinte"),true);
			theme.addPopulation(pop);
			pop = new Population<DebutSection>(persistance, "Débuts de section", Class.forName(nom_package+".DebutSection"),true);
			theme.addPopulation(pop);
			pop = new Population<EquipementRoutier>(persistance, "Equipements routiers", Class.forName(nom_package+".EquipementRoutier"),true);
			theme.addPopulation(pop);
			pop = new Population<Franchissement>(persistance, "Franchissements", Class.forName(nom_package+".Franchissement"),true);
			theme.addPopulation(pop);
			pop = new Population<NoeudRoutier>(persistance, "Noeuds routiers", Class.forName(nom_package+".NoeudRoutier"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconRoute>(persistance, "Tronçons de route", Class.forName(nom_package+".TronconRoute"),true);
			theme.addPopulation(pop);
			pop = new Population<LiaisonMaritime>(persistance, "Liaisons maritimes", Class.forName(nom_package+".LiaisonMaritime"),true);
			theme.addPopulation(pop);
			pop = new Population<Itineraire>(persistance, "Itinéraires routiers", Class.forName(nom_package+".Itineraire"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}

	}

	public static void ajouteThemeToponymes(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Thème 'Toponymes' de la BDCarto");
		theme.setNom("Toponymes");
		String nom_package = nom_package_jeu_complet+".toponymes";
		try{
			Population<?> pop;
			pop = new Population<Etablissement>(persistance, "Etablissements", Class.forName(nom_package+".Etablissement"),true);
			theme.addPopulation(pop);
			pop = new Population<MassifBoise>(persistance, "Massifs boisés", Class.forName(nom_package+".MassifBoise"),true);
			theme.addPopulation(pop);
			pop = new Population<PointRemarquableDuRelief>(persistance, "Points remarquables du relief", Class.forName(nom_package+".PointRemarquableDuRelief"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneActivite>(persistance, "Zones d'activité", Class.forName(nom_package+".ZoneActivite"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneHabitat>(persistance, "Zones d'habitat", Class.forName(nom_package+".ZoneHabitat"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneReglementeeTouristique>(persistance, "Zones règlementées touristiques", Class.forName(nom_package+".ZoneReglementeeTouristique"),true);
			theme.addPopulation(pop);
			pop = new Population<GR>(persistance, "GR", Class.forName(nom_package+".GR"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Problème de nom de package : "+nom_package_jeu_complet);
		}
	}
}