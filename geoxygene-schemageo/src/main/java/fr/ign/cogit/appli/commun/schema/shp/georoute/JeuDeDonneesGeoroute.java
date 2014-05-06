package fr.ign.cogit.appli.commun.schema.shp.georoute;

import fr.ign.cogit.appli.commun.schema.shp.georoute.routier.ThemeRoutier;
import fr.ign.cogit.geoxygene.feature.DataSet;

public class JeuDeDonneesGeoroute extends DataSet {

	/** Constructeur par défaut */
	public JeuDeDonneesGeoroute() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un jeu de données Géoroute au format shape :
	 * - un jeu de données Géoroute contient les thèmes routier  :
	 * - ces thèmes ont des noms par défaut à ne pas changer : "routier"
	 * - les populations de ces thèmes ont des noms logique par défaut "Troncon de route", "Noeud routier", etc.
	 * - leurs éléments se réalisent dans des classes contretes du package nom_package.
	 * - un jeu de données peut être persistant ou non
	 * - un jeu de données a un nom logique (utile pour naviguer entre jeux de données).
	 */
	public JeuDeDonneesGeoroute(boolean persistance, String nom_logique, String nom_package) {
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
		this.setTypeBD("Géoroute");
		this.setModele("Shape");
		this.setNom(nom_logique);
		if (persistance) DataSet.db.makePersistent(this);
		ThemeRoutier routier = new ThemeRoutier(persistance, nom_package+".routier", this);
		this.addComposant(routier);
	}



	public ThemeRoutier getThemeRoutier() {return (ThemeRoutier)this.getComposant("routier");}





}

