package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Liaisom maritime ou bac.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Lineaire.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotee ou nommee"
 * <BR> <STRONG> Definition </STRONG>:
 *       Liaison maritime ou ligne de bac reliant deux embarcaderes.
 *       Sont retenus dans la BDCarto : <UL>
 *       <LI> tous les bacs et liaisons maritimes reliant deux embarcaderes situes sur le territoire de la BDCarto et ouverts au public (noeuds routiers de type 22), a
 *       l'exception des bacs fluviaux reserves aux pietons ; </LI>
 *       <LI> toutes les liaisons maritimes regulieres effectuant le transport des passagers ou des vehicules entre un embarcadere situe sur le territoire de la BDCarto
 *       (type 22) et un embarcadere situe hors du territoire BDCarto (type 23). </LI> </UL>
 *       Lorsqu'un embarcadere est egalement noeud routier apportant une information particuliere (rond-point?) c'est ce dernier qui est code. Les liaisons
 *       maritimes sont toujours connectees au reseau routier pour assurer la continuite du reseau. Quand il n'existe pas de route pour assurer cette connection,
 *       celle-ci est assuree par un troncon fictif (voir B-s-1-[31-2]).
 * <BR> <STRONG> Compatibilite entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class LiaisonMaritime extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//     protected GM_Curve geometrie = null;
	/** Renvoie la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/** Ouverture.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Ouverture.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- toute l'annee   </LI>
	 * <LI>     6- en saison seulement </LI>
	 * </UL>
	 */
	protected String ouverture;
	public String getOuverture() {return ouverture; }
	public void setOuverture(String S) {ouverture = S; }

	/** Vocation.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Vocation.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- pietons seulement    </LI>
	 * <LI>     2- pietons et automobiles </LI>
	 * </UL>
	 */
	protected String vocation;
	public String getVocation() {return vocation; }
	public void setVocation(String S) {vocation= S; }

	/** Duree.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Duree de la traversee en minutes, pouvant eventuellement ne porter aucune valeur (inconnue).
	 *      Note : Quand il y a plusieurs temps de parcours pour une meme liaison, c'est le temps le plus long qui est retenu.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Entier > 0
	 */
	protected double duree;
	public double getDuree() {return duree; }
	public void setDuree(double D) {duree= D; }

	/** Toponyme.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Texte d'au plus 80 caracteres, pouvant eventuellement ne porter aucune valeur (inconnu),
	 *      specifiant la localisation des embarcaderes de depart et d'arrivee (ex : " brest : le conquet ").
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 */
	protected String toponyme;
	public String getToponyme() {return toponyme; }
	public void setToponyme(String S) {toponyme = S; }




	/** Noeud initial de la liaison maritime.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liasons sortants.
	 */

	protected NoeudRoutier noeudIni;
	/** Recupere le noeud initial. */
	public NoeudRoutier getNoeudIni() {return noeudIni;}
	/** Definit le noeud initial, et met a jour la relation inverse. */
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
	/** Ne pas utiliser, necessaire au mapping*/
	public void setNoeudIniID(int I) {noeudIniID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getNoeudIniID() {return noeudIniID;}




	/** Noeud final de la liaison maritime.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liaisons sortants.
	 */
	protected NoeudRoutier noeudFin;
	/** Recupere le noeud final. */
	public NoeudRoutier getNoeudFin() {return noeudFin;}
	/** Definit le noeud final, et met a jour la relation inverse. */
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
	/** Ne pas utiliser, necessaire au mapping*/
	public void setNoeudFinID(int I) {noeudFinID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getNoeudFinID() {return noeudFinID;}


}
