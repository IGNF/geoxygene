/**
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.spatial.geomroot;


import java.awt.Color;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.ImgUtil;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;


/**
 * Classe mère abstraite pour la géométrie, selon la norme OGC Topic 1 / ISO 19107.
 * Cette classe définit les opérations communes aux différents objets géométriques qui en héritent.
 * Toutes les opérations doivent se faire dans le même système de coordonnées.
 * Un objet géométrique est en fait une combinaison d'un système de référence de coordonnées (CRS),
 * et d'une géométrie munie de coordonnées (CoordGeom).
 * <p>
 * Les methodes geometriques font par defaut appel a la bibliotheque JTS,
 * via des appels aux methodes de la classe {@link JtsAlgorithms}. Attention, bien souvent, ces méthodes
 * ne fonctionnent que sur des primitives ou des agrégats homogènes GM_MultiPrimitive.
 * <p>
 * Historiquememt, les methodes faisaient appel aux fonctions geometriques d'Oracle
 * et a la bibliotheque fournie par Oracle sdoapi.zip,
 * via des appels aux methodes de la classe util.algo.OracleAlgorithms,
 * qui elles-memes appellent des methodes datatools.oracle.SpatialQuery.
 * Ces methodes ont ete gardees et portent le suffixe "Oracle".
 * Pour les appeler, il est nécessaire d'établir une connection à Oracle,
 * c'est pourquoi on passe une "Geodatabase" en paramètre de chaque fonction.
 * On suppose qu'il existe dans la base, dans le schéma utilisateur,
 * une table TEMP_REQUETE, avec une colonne GID (NUMBER) et une colonne GEOM (SDO_GEOMETRY).
 * Cette table est dédiée aux requêtes spatiales.
 * De même, le paramètre tolérance est exigé par Oracle.
 *
 * ARNAUD 12 juillet 2005 : mise en commentaire de ce qui se rapporte à Oracle
 * pour isoler la compilation. A décommenter pour utiliser Oracle.
 * 
 * @author Thierry Badard & Arnaud Braun
 *
 */
abstract public class GM_Object implements Cloneable {

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// Attributs et accesseurs //////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Identifiant de l'objet géométrique, dans la table du SGBD.
	 * Cet identifiant n'est pas spécifié dans la norme ISO.
	 * Non utilise a ce jour. */
	//    protected int GM_ObjectID;
	/** Renvoie l'identifiant géométrique.*/
	//    public int getGM_ObjectID() { return this.GM_ObjectID; }
	/** Affecte un identifiant. */
	//    public void setGM_ObjectID(int geomID) { this.GM_ObjectID = geomID; }

	/** FT_Feature auquel est rattaché cette géométrie.
	 * Cette association n'est pas dans la norme.
	 * A prevoir : faire une liste pour gérer les partages de géométrie. */
	//    protected FT_Feature feature;
	/** Renvoie le FT_Feature auquel est rattaché cette géométrie. */
	//    public FT_Feature getFeature() { return this.feature; }
	/** Affecte un FT_Feature. */
	//    public void setFeature(FT_Feature Feature) { this.feature = Feature;}

	/** Identifiant du système de coordonnées de référence (CRS en anglais).
	 * Par défaut, vaut 41014 : identifiant du Lambert II carto.
	 * Dans la norme ISO, cet attribut est une relation qui pointe vers la classe SC_CRS (non implémentée) */
	protected int CRS = -1;
	/** Renvoie l' identifiant du système de coordonnées de référence. */
	public int getCRS() { return this.CRS; }
	/** Affecte une valeur au système de coordonnées de référence. */
	public void setCRS(int crs) { this.CRS = crs;}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// Méthodes de la norme non implementees (souvent liees a l'utilisation de GM_Conplex) //////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Collection de GM_Object représentant la frontière de self.
	 * Cette collection d'objets a une structure de GM_Boundary, qui est un sous-type de GM_Complex.  */
	// en commentaire car oblige a typer toute les méthodes boundary() des sous-classes en GM_Boundary : pénible à l'utilisation
	//abstract public GM_Boundary boundary() ;

	/** Union de l'objet et de sa frontière.
	 *  Si l'objet est dans un GM_Complex, alors la frontière du GM_Complex retourné doit être dans le même complexe ;
	 *  Si l'objet n'est pas dans un GM_Complex, alors sa frontière doit être construite en réponse à cette opération.  */
	//public GM_Complex closure() {
	//}

	/** Set de complexes maximaux auxquels apppartient l'objet. */
	//public GM_Complex[] maximalComplex() {
	//}

	/** Renvoie TRUE si la frontière est vide. */
	//public boolean isCycle() {
	//}

	/** Dimension du système de coordonnées (1D, 2D ou 3D). */
	//public int coordinateDimension() {
	//}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// diverses methodes utiles /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie la liste des DirectPosition de l'objet.
        Méthode abstraite redéfinie dans les sous-classes.
        Cette méthode se comporte différemment selon le type d'objet géométrique. */
	abstract public DirectPositionList coord();
	/** Clone l'objet. */
	@Override
	public Object clone() {
		//FIXME j'ai comme un doute que ça marche ça
		try {return super.clone();}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/** Ecrit la géométrie dans une chaine de caractere au format WKT. */
	@Override
	public String toString() {
		try {return WktGeOxygene.makeWkt(this);}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/** Exporte la géométrie dans un fichier texte au format WKT.
	 * Si append = false, un nouveau fichier est systematiquement cree.
	 * Si append = true, et que le fichier existe deja, la geometrie est ajoutee a la fin du fichier;
	 * si le fichier n'existe pas, il est cree. */
	public void exportWkt(String path, boolean append) {
		try {WktGeOxygene.writeWkt(path,append,this);}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Exporte des géométries dans une image.
	 * Le format de l'image (.jpg ou .png par defaut) est determiné par l'extension du nom de fichier,
	 * a mettre dans le parametre "path".
	 * Le tableau de couleur permet d'affecter des couleurs différentes aux géométries.
	 * <BR> Exemple : GM_Object.exportImage(new GM_Object[] {geom1, geom2},"/home/users/truc/essai.jpg",
	 *                                          new Color[] {Color.RED, Color.BLUE}, Color.WHITE, 150, 80) */
	public static void exportImage (GM_Object[] geoms, String path, Color foreground[], Color background, int width, int height) {
		try {ImgUtil.saveImage (geoms, path, foreground, background, width, height);}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Exporte des géométries dans un fichier SVG compressé.
	 * Donner dans la variable "path" le chemin et le nom du fichier (avec l'extension .svgz)
	 * Le tableau de couleur permet d'affecter des couleurs différentes aux géométries.
	 * <BR> Exemple : GM_Object.exportSvgz(new GM_Object[] {geom1, geom2},"/home/users/truc/essai.jpg",
	 *                                          new Color[] {Color.RED, Color.BLUE}, Color.WHITE, 150, 80) */
	public  static void exportSvgz(GM_Object[] geoms, String path, Color foreground[], Color background, int width, int height) {
		try {ImgUtil.saveSvgz (geoms, path, foreground, background, width, height);}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// methodes geometriques directement codees /////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Rectangle englobant minimum de l'objet (en 2D) sous forme de GM_Envelope. */
	public GM_Envelope envelope() {
		DirectPositionList list = coord();
		if (list.size()==0) return new GM_Envelope();
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		for (DirectPosition point:list) {
			if (!Double.isNaN(point.getX())) xmin=Math.min(xmin, point.getX());
			if (!Double.isNaN(point.getX())) xmax=Math.max(xmax, point.getX());
			if (!Double.isNaN(point.getY())) ymin=Math.min(ymin, point.getY());
			if (!Double.isNaN(point.getY())) ymax=Math.max(ymax, point.getY());
		}
		return new GM_Envelope (xmin,xmax,ymin,ymax);
	}
	/** Rectangle englobant minimum de l'objet (en 2D) sous forme de GM_Polygon.
	 * Le but est d'obtenir une region contenant l'objet.
	 * Tout autre implémentation serait possible : le but serait de supporter des méthodes d'indexation
	 * qui n'utilisent pas les rectangles minimaux englobants.  
	 * @param data
	 * @return
	 */
	public GM_Polygon mbRegion(Geodatabase data) {return new GM_Polygon(this.envelope());}
	/**Teste l'intersection stricte entre la géométrie manipulée et celle passée en
	 * paramètre, i.e. l'intersection sans les cas où les géométries sont simplement
	 * adjacentes (intersection = point ou ligne) ou sont contenues l'une dans
	 * dans l'autre
	 * @param geom GM_Object
	 * @return boolean
	 */
	public boolean intersectsStrictement(GM_Object geom) {
		return (this.intersects(geom)&&!this.contains(geom)&&!geom.contains(this)&&!this.touches(geom));
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// methodes geometriques et topologiques faisant appel a JTS ////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Centre de gravité de l'objet (avec JTS). Le résultat n'est pas nécessairement dans l'objet. */
	public DirectPosition centroid() {return new JtsAlgorithms().centroid(this);}
	/** Enveloppe convexe de l'objet (avec JTS).  */
	public GM_Object convexHull()  {return new JtsAlgorithms().convexHull(this);}
	/**
	 * Calcule de buffer sur l'objet (avec JTS).
	 * Les distances negatives sont acceptees (pour faire une érosion).
	 * Le nombre de segments utilisés pour approximer les parties courbes du buffer est celui par défaut de JTS, i.e. 8.
	 * La forme du "chapeau" (cap) utilsée est celle par défaut de JTS, i.e. CAP_ROUND : une courbe.
	 * @param distance distance utilisée pour le calcul du buffer
	 * @return buffer sur l'objet
	 * @see #buffer(double, int)
	 */
	public GM_Object buffer (double distance) {return new JtsAlgorithms().buffer(this,distance);}
	/**
	 * Calcule de buffer sur l'objet (avec JTS) en indiquant le nombre de segments approximant la partie courbe.
	 * Les distances negatives sont acceptees (pour faire une érosion).
	 * La forme du "chapeau" (cap) utilsée est celle par défaut de JTS, i.e. CAP_ROUND : une courbe.
	 * @param distance distance utilisée pour le calcul du buffer
	 * @param nSegments nombre de segments utilisés pour approximer les parties courbes du buffer
	 * @return buffer sur l'objet
	 * @see #buffer(double)
	 */
	public GM_Object buffer (double distance, int nSegments) {return new JtsAlgorithms().buffer(this,distance,nSegments);}
	/** Union avec l'objet passé en paramètre (avec JTS).
	 * Renvoie éventuellement un aggrégat si les objets sont disjoints.  */
	public GM_Object union(GM_Object geom)  {return new JtsAlgorithms().union(this,geom);}
	/** Intersection avec l'objet passé en paramètre (avec JTS).
	 * Renvoie un GM_Aggregate vide si les objets sont disjoints. */
	public GM_Object intersection(GM_Object geom) {return new JtsAlgorithms().intersection(this,geom);}
	/** Différence avec l'objet passé en paramètre (avec JTS).
	 * Returns a Geometry representing the points making up this Geometry that do not make up "geom".*/
	public GM_Object difference(GM_Object geom)   {return new JtsAlgorithms().difference(this,geom);}
	/** Différence symétrique avec l'objet passé en paramètre (avec JTS).
	 * La différence symétrique (opérateur booléan XOR) est la différence de l'union avec l'intersection.
	 *  Returns a set combining the points in this Geometry not in other, and the points in other not in this Geometry.*/
	public GM_Object symmetricDifference(GM_Object geom) {return new JtsAlgorithms().symDifference(this,geom);}
	/**
	 * Predicat topologique sur la relation d'egalite (!= equalsExact) (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys is T*F**FFF*.
	 * @param geom géométrie à comparer à this
	 * @return vrai si les deux géométries sont égales (if the DE-9IM intersection matrix for the two Geometrys is T*F**FFF*)
	 * @see #equalsExact(GM_Object)
	 * @see #equalsExact(GM_Object, double)
	 */
	public boolean equals(GM_Object geom) {return new JtsAlgorithms().equals(this,geom);}
	/**
	 * This et l'objet passe en parametre appartiennent a la meme classe et ont exactement les memes coordonnees dans le même ordre (avec JTS).
	 * Ce prédicat est plus stricte que {@link #equals(GM_Object)}
	 * @param geom géométrie à comparer à this
	 * @return vrai si les deux géométries ont la même classe et sont strictement égales
	 * @see #equals(GM_Object)
	 * @see #equalsExact(GM_Object, double)
	 */
	public boolean equalsExact(GM_Object geom) {return new JtsAlgorithms().equalsExact(this,geom);}
	/** This et l'objet passe en parametre appartiennent a la meme classe et ont exactement les memes coordonnees à une tolérance près (avec JTS)
	 * Ce prédicat est plus stricte que {@link #equals(GM_Object)} et moins que {@link #equalsExact(GM_Object)}
	 * @param geom géométrie à comparer à this
	 * @return vrai si les deux géométries ont la même classe et sont strictement égales à une tolérance près
	 */
	public boolean equalsExact(GM_Object geom, double tolerance) {return new JtsAlgorithms().equalsExact(this,geom,tolerance);}
	/** Predicat topologique sur la relation de contenance (avec JTS).
	 * Returns true if geom.within(this) returns true.*/
	public boolean contains(GM_Object geom) {return new JtsAlgorithms().contains(this,geom);}
	/** Predicat topologique crosses (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys is T*T******
	 * (for a point and a curve, a point and an area or a line and an area),
	 *  0******** (for two curves) .*/
	public boolean crosses(GM_Object geom) {return new JtsAlgorithms().crosses(this,geom);}
	/** Predicat topologique sur la relation de disjonction (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys is FF*FF****. */
	public boolean disjoint(GM_Object geom) {return new JtsAlgorithms().disjoint(this,geom);}
	/** Predicat topologique sur la relation d'interieur (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys is T*F**F***. */
	public boolean within(GM_Object geom) {return new JtsAlgorithms().within(this,geom);}
	/** Teste si la distance entre cette géométrie et geom est inférieure à la distance passée en paramètre. */
	public boolean isWithinDistance(GM_Object geom, double distance) {return new JtsAlgorithms().isWithinDistance(this,geom,distance);}
	/** Predicat topologique sur la relation d'intersection (avec JTS).
	 * Returns true if disjoint returns false. */
	public boolean intersects(GM_Object geom) {return new JtsAlgorithms().intersects(this,geom);}
	/** Predicat topologique sur la relation de recouvrement (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys is T*T***T**
	 * (for two points or two surfaces), or 1*T***T** (for two curves) . */
	public boolean overlaps(GM_Object geom) {return new JtsAlgorithms().overlaps(this,geom);}
	/** Predicat topologique sur la relation de contact (avec JTS).
	 * Returns true if the DE-9IM intersection matrix for the two Geometrys
	 * is FT*******, F**T***** or F***T****.*/
	public boolean touches(GM_Object geom) {return new JtsAlgorithms().touches(this,geom);}
	/** Renvoie true si la geometrie est vide (avec JTS). */
	public boolean isEmpty() {return new JtsAlgorithms().isEmpty(this);}
	/** Renvoie TRUE si l'objet n'a pas de point d'auto-intersection ou d'auto-tangence (avec JTS).
	 * Cette opération n'est pas applicable aux objets fermés (ceux pour lesquels isCycle() = TRUE). */
	public boolean isSimple() {return new JtsAlgorithms().isSimple(this);}
	/** Renvoie TRUE si la geometrie est valide au sens JTS. Utile pour debugger. */
	public boolean isValid() {return new JtsAlgorithms().isValid(this);}
	/** Distance entre this et l'objet passe en parametre (avec JTS).
	 * Returns the minimum distance between this Geometry and the Geometry geom. */
	public double distance(GM_Object geom)  {return new JtsAlgorithms().distance(this,geom);}
	/** Aire de l'objet (avec JTS) */
	public double area() {return new JtsAlgorithms().area(this);}
	/** Longueur de l'objet (avec JTS) */
	public double length() {return new JtsAlgorithms().length(this);}
	/** Dimension maximale de l'objet (point 0, courbe 1, surface 2) (avec JTS). */
	public int dimension() {return new JtsAlgorithms().dimension(this);}
	/** Nombre de points de l'objet (avec JTS). */
	public int numPoints() {return new JtsAlgorithms().numPoints(this);}
	/** Translation de l'objet (avec JTS). */
	public GM_Object translate(final double tx, final double ty, final double tz)  {
		return new JtsAlgorithms().translate(this,tx,ty,tz);
	}
	/** Returns the DE-9IM intersection matrix for the two Geometries. */
	public String relate(GM_Object geom) {return new JtsAlgorithms().relate(this,geom);}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// methodes geometriques et topologiques faisant appel a Oracle /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Calcul de buffer optimisé sous Oracle, pour les agrégats.
	 *  Cette methode semble meilleure en performance que "bufferOracle"
	 *  pour calculer les buffer et les fusionner,
	 *  si le GM_Object est un agrégat contenant beaucoup d'objets.
	 *  Le GM_Object doit etre un GM_Aggregate ou une sous classe, sinon plantage.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param radius Distance pour calculer le buffer. */
	/*   public GM_Object bufferAgregatOracle(Geodatabase data, double tolerance, double radius) {
       return new OracleAlgorithms(data,tolerance).bufferAgregat(this,radius);
   }
	 */
	/** Calcule le buffer de self (avec Oracle). La distance doit être positive.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param radius Distance pour calculer le buffer. */
	/*   public GM_Object bufferOracle (Geodatabase data, double tolerance, double radius) {
     return new OracleAlgorithms(data,tolerance).buffer(this,radius);
   }
	 */
	/** Enveloppe convexe de self (avec Oracle). Renvoie NULL si self est un point, ou est défini avec moins de trois points.
	 * Le résultat est un GM_Polygon.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul. */
	/*   public GM_Object convexHullOracle (Geodatabase data, double tolerance)  {
       return new OracleAlgorithms(data,tolerance).convexHull(this);
   }
	 */
	/** Centre de gravité de self (avec Oracle). Le résultat n'est pas nécessairement dans l'objet.
	 * Pour un objet multi-dimensions, on ne prendra en compte que la plus grande dimension pour le calcul.
	 * ATTENTION implementé uniquement pour un polygone. Sinon renvoie NULL.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.  */
	/*   public GM_Object centroidOracle (Geodatabase data, double tolerance)  {
       return new OracleAlgorithms(data,tolerance).centroid(this);
   }
	 */
	/** Un point représentatif à l'intérieur de self (avec Oracle). Ce point peut être n'importe où.
	 * Deux appels différents à cette méthode sur un objet peuvent produire deux résultats différents.
	 * On garantit juste que le point est à l'intérieur.
	 * ATTENTION : implémenté uniquement pour un polygone, sinon renvoie NULL.
	 * Une utilisation peut être le placement de labels pour une présentation graphique.
	 * REMARQUE : dans la norme, on impose que ce point soit le centroide s'il est à l'intérieur, un autre point sinon.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul. */
	/*   public DirectPosition representativePointOracle (Geodatabase data, double tolerance) {
       return new OracleAlgorithms(data,tolerance).representativePoint(this);
   }
	 */
	/** Rectangle englobant minimum de self (avec Oracle).
	 *  @param data Paramètres de connection à la base de données.  */
	/*   public GM_Envelope envelopeOracle (Geodatabase data) {
       return new OracleAlgorithms(data,0.).envelope(this);
   }
	 */
	/** Différence de self avec l'objet passé en paramètre (avec Oracle).
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g Objet géométrique avec lequel on va réaliser l'opération. */
	/*   public GM_Object differenceOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).difference(this,g);
   }
	 */
	/** Intersection de self avec l'objet passé en paramètre (avec Oracle).
	 * Renvoie NULL si les objets sont disjoints.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g Objet géométrique avec lequel on va réaliser l'opération. */
	/*   public GM_Object intersectionOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).intersection(this,g);
   }
	 */
	/** Union de self et de l'objet passé en paramètre (avec Oracle).
	 * Renvoie éventuellement un aggrégat si les objets sont disjoints.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g Objet géométrique avec lequel on va réaliser l'union.  */
	/*   public GM_Object unionOracle (Geodatabase data, double tolerance, GM_Object g) {
       return new OracleAlgorithms(data,tolerance).union(this,g);
   }
	 */
	/** Différence symétrique de self avec l'objet passé en paramètre (avec Oracle).
	 * La différence symétrique (opérateur booléan XOR) est la différence de l'union avec l'intersection.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g Objet géométrique avec lequel on va réaliser l'opération. */
	/*   public GM_Object symmetricDifferenceOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).symDifference(this,g);
   }
	 */
	/** Teste si self contient l'objet passé en paramètre (avec Oracle).
	 * REMARQUE : les frontières ne doivent pas se toucher, sinon renvoie false - A TESTER.
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g GM_Object avec lequel on teste l'intersection  */
	/*   public boolean containsOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).contains(this,g);
   }
	 */
	/** Teste si self contient le DirectPosition passé en paramètre (avec Oracle).
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param P DirectPosition avec lequel on teste l'intersection. */
	/*   public boolean containsOracle (Geodatabase data, double tolerance, DirectPosition P)  {
       return new OracleAlgorithms(data,tolerance).contains(this,P);
   }
	 */
	/** Teste si self intersecte l'objet géométrique passé en paramètre (avec Oracle). Renvoie un boolean.
	 * REMARQUE : si les 2 objets n'ont que la frontière en commun, alors renvoie false - A TESTER.
	 * CAS des COMPLEXES : a revoir (cf.norme)
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g GM_Object avec lequel on teste l'intersection.  */
	/*   public boolean intersectsOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).intersects(this,g);
   }
	 */
	/** Teste si self et l'objet passé en paramètre sont géométriquement égaux (avec Oracle).
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g GM_Object avec lequel on teste l'intersection.  */
	/*   public boolean equalsOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,tolerance).equals(this,g);
   }
	 */
	/** Renvoie TRUE si self n'a pas de point d'auto-intersection ou d'auto-tangence (avec Oracle SDOAPI).
	 * Cette opération n'est pas applicable aux objets fermés (ceux pour lesquels isCycle() = TRUE).
	 * @param data Paramètres de connection à la base de données. */
	/*   public boolean isSimpleOracle(Geodatabase data)  {
       return new OracleAlgorithms(data,0.).isSimple(this);
   }
	 */
	/** Distance de self à l'objet passé en paramètre (avec Oracle).
	 * Cette distance est définie comme la distance euclidienne.
	 * Si les objets se recouvrent ou se touchent, la distance doit être nulle (pas de distance négative).
	 * @param data Paramètres de connection à la base de données.
	 * @param tolerance Tolérance pour le calcul.
	 * @param g GM_Object avec lequel on teste l'intersection. */
	/*   public double distanceOracle (Geodatabase data, double tolerance, GM_Object g)  {
       return new OracleAlgorithms(data,0.).distance(this,g);
   }
	 */
	/** Longueur de l'objet, si c'est une primitive linéaire (avec Oracle).
	 * Applicable sur GM_Curve et GM_MultiCurve.
	 * @param data Paramètres de connection à la base de données. */
	/*   public double lengthOracle (Geodatabase data)  {
       return new OracleAlgorithms(data,0.).length(this);
   }
	 */
	/** Surface de l'objet, si c'est une primitive surfacique (avec Oracle).
	 * Applicable sur GM_Surface et GM_MultiSurface.*/
	/*   public double areaOracle (Geodatabase data)  {
       return new OracleAlgorithms(data,0.).area(this);
   }
	 */

	public boolean isLineString() {return false;}
	public boolean isMultiCurve() {return false;}
	public boolean isPolygon() {return false;}
	public boolean isMultiSurface() {return false;}
	public boolean isPoint() {return false;}
}
