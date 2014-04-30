package fr.ign.cogit.appli.commun.schema.traducteurPostGis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * Création dans PostGis de la structure d'un DataSet.
 * A Integrer dans une petite interface
 * @author  mustiere - modif grosso pour postgis
 */
public class CreationDataSet {

	public static void main (String args[]) {
		int id, type = 0;
		List<String> metadonnees = new ArrayList<String>();
		fr.ign.cogit.geoxygene.feature.DataSet jeu = null;
		String S, package_concret, nom_logique, date, zone, commentaire ;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		try {
			System.out.println("CREATION de la structure d'un DataSet (définition des thèmes et des populations)");
			System.out.println("");

			System.out.println("  Connexion au SGBD");
			//DataSet.db = new GeodatabaseOjbOracle();
			DataSet.db = new GeodatabaseOjbPostgis();
			System.out.println("  Connexion OK");
			System.out.println("");


			System.out.println("Type de DataSet à créer ?");
			System.out.println("1 : BDCARTO  au modèle shape ");
			System.out.println("2 : BDPAYS   au modèle shape 2D");
			System.out.println("3 : BDCARTO  au modèle interprétation (DAVID only) ");
			System.out.println("4 : GEOROUTE  au modèle interprétation (DAVID only) ");
			System.out.println("5 : BDCARTO  au modèle de travail pour l'appariement (Eric)");
			System.out.println("6 : BDPAYS  au modèle de travail pour l'appariement (Eric)");

			S = in.readLine();
			type = Integer.valueOf(S).intValue();

			System.out.println("");
			System.out.println("Nom complet du package contenant les classes contrètes (ex: donnees.seb.bdcarto_dept7794) ?");
			package_concret = in.readLine();


			System.out.println("");
			System.out.println("Métadonnées...");
			System.out.println("Nom de la base (ex: 'bdcarto dept 77 en 2003') ?");
			nom_logique = in.readLine();
			metadonnees.add(nom_logique);
			System.out.println("Date (texte au format libre) ?");
			date = in.readLine();
			metadonnees.add(date);
			System.out.println("Zone couverte (texte au format libre) ?");
			zone = in.readLine();
			metadonnees.add(zone);
			System.out.println("Commentaire (texte au format libre) ?");
			commentaire = in.readLine();
			metadonnees.add(commentaire);


			System.out.println("");
			System.out.println("CREATION DU JEU...");
			DataSet.db.begin();
			System.out.println("  Transaction ouverte");
			System.out.println("");



			if ( type == 1 ) jeu = fr.ign.cogit.appli.commun.schema.shp.bdcarto.CreationDataSet.nouveauDataSet(true, package_concret, metadonnees);
			if ( type == 2 ) jeu = fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.CreationDataSet.nouveauDataSet(true, package_concret, metadonnees);

			//if ( type == 3 ) jeu = new interpretation.bdcarto_enrichie.JeuDeDonneesBDCartoInterpretation(true, nom_logique, package_concret);
			//if ( type == 4 ) jeu = new interpretation.georoute_enrichie.JeuDeDonneesGeorouteInterpretation(true, nom_logique, package_concret);;

			if ( type == 5 ) jeu = fr.ign.cogit.appli.commun.schema.structure.bdCarto.CreationDataSet.nouveauDataSet(true, package_concret, metadonnees);
			if ( type == 6 ) jeu = fr.ign.cogit.appli.commun.schema.structure.bdTopo.CreationDataSet.nouveauDataSet(true, package_concret, metadonnees);

			if (jeu==null) return;
			id = jeu.getId();

			System.out.println("  Fermeture de la transaction (commit)");
			DataSet.db.commit();
			System.out.println("  FIN de la création");
			System.out.println("");
			System.out.println("---------------------------------------------------------");
			System.out.println("-- LE JEU A ETE CREE AVEC L'IDENTIFIANT (à noter) : "+id);
			System.out.println("---------------------------------------------------------");



			System.out.println("");
			System.out.println("");
			System.out.println("TEST DE CHARGEMENT EN MEMOIRE DU JEU ");
			System.out.println("  Début du chargement : "+(new Time(System.currentTimeMillis())).toString());
			System.out.println("  Chargement de la structure du jeu (jeux/populations)");
			jeu = DataSet.db.load(DataSet.class, new Integer(id));
			System.out.println("  Chargement des instances des populations");
			jeu.chargeElements();
			System.out.println("  Fin du chargement : "+(new Time(System.currentTimeMillis())).toString());
			System.out.println("C'est fini !!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		}
		catch (Exception e) {e.printStackTrace();}
	}

}

