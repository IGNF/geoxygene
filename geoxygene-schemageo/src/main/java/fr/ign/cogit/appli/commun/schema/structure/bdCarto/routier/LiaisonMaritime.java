package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Liaisom maritime ou bac.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Linéaire.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotée ou nommée"
 * <BR> <STRONG> Définition </STRONG>:
 *       Liaison maritime ou ligne de bac reliant deux embarcadères.
 *       Sont retenus dans la BDCarto : <UL>
 *       <LI> tous les bacs et liaisons maritimes reliant deux embarcadères situés sur le territoire de la BDCarto et ouverts au public (noeuds routiers de type 22), à
 *       l'exception des bacs fluviaux réservés aux piétons ; </LI>
 *       <LI> toutes les liaisons maritimes régulières effectuant le transport des passagers ou des véhicules entre un embarcadère situé sur le territoire de la BDCarto
 *       (type 22) et un embarcadère situé hors du territoire BDCarto (type 23). </LI> </UL>
 *       Lorsqu'un embarcadère est également noeud routier apportant une information particulière (rond-point?) c'est ce dernier qui est codé. Les liaisons
 *       maritimes sont toujours connectées au réseau routier pour assurer la continuité du réseau. Quand il n'existe pas de route pour assurer cette connection,
 *       celle-ci est assurée par un tronçon fictif (voir B-s-1-[31-2]).
 * <BR> <STRONG> Compatibilité entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class LiaisonMaritime extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géoémtries multiples (plusieurs tronçons) */
	//     protected GM_Curve geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/** Ouverture.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Ouverture.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- toute l'année   </LI>
	 * <LI>     6- en saison seulement </LI>
	 * </UL>
	 */
	protected String ouverture;
	public String getOuverture() {return ouverture; }
	public void setOuverture(String S) {ouverture = S; }

	/** Vocation.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Vocation.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- piétons seulement    </LI>
	 * <LI>     2- piétons et automobiles </LI>
	 * </UL>
	 */
	protected String vocation;
	public String getVocation() {return vocation; }
	public void setVocation(String S) {vocation= S; }

	/** Durée.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Durée de la traversée en minutes, pouvant éventuellement ne porter aucune valeur (inconnue).
	 *      Note : Quand il y a plusieurs temps de parcours pour une même liaison, c'est le temps le plus long qui est retenu.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Entier > 0
	 */
	protected double duree;
	public double getDuree() {return duree; }
	public void setDuree(double D) {duree= D; }

	/** Toponyme.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Texte d'au plus 80 caractères, pouvant éventuellement ne porter aucune valeur (inconnu),
	 *      spécifiant la localisation des embarcadères de départ et d'arrivée (ex : " brest : le conquet ").
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */
	protected String toponyme;
	public String getToponyme() {return toponyme; }
	public void setToponyme(String S) {toponyme = S; }




	/** Noeud initial de la liaison maritime.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liasons sortants.
	 */

	protected NoeudRoutier noeudIni;
	/** Récupère le noeud initial. */
	public NoeudRoutier getNoeudIni() {return noeudIni;}
	/** Définit le noeud initial, et met à jour la relation inverse. */
	public void setNoeudIni(NoeudRoutier O) {
		NoeudRoutier old = noeudIni;
		noeudIni = O;
		if ( old  != null ) old.getSortantsMaritime().remove(this);
		if ( O != null ) {
			noeudIniID = O.getId();
			if ( !(O.getSortantsMaritime().contains(this)) ) O.getSortantsMaritime().add(this);
		} else noeudIniID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudIniID;
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setNoeudIniID(int I) {noeudIniID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getNoeudIniID() {return noeudIniID;}




	/** Noeud final de la liaison maritime.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liaisons sortants.
	 */
	protected NoeudRoutier noeudFin;
	/** Récupère le noeud final. */
	public NoeudRoutier getNoeudFin() {return noeudFin;}
	/** Définit le noeud final, et met à jour la relation inverse. */
	public void setNoeudFin(NoeudRoutier O) {
		NoeudRoutier old = noeudFin;
		noeudFin = O;
		if ( old  != null ) old.getEntrantsMaritime().remove(this);
		if ( O != null ) {
			noeudFinID = O.getId();
			if ( !(O.getEntrantsMaritime().contains(this)) ) O.getEntrantsMaritime().add(this);
		} else noeudFinID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudFinID;
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setNoeudFinID(int I) {noeudFinID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getNoeudFinID() {return noeudFinID;}


}
