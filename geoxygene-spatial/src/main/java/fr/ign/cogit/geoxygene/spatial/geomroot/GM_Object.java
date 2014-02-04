/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.spatial.geomroot;

import java.awt.Color;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.ImgUtil;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Classe mère abstraite pour la géométrie, selon la norme OGC Topic 1 / ISO
 * 19107. Cette classe définit les opérations communes aux différents objets
 * géométriques qui en héritent. Toutes les opérations doivent se faire dans le
 * même système de coordonnées. Un objet géométrique est en fait une combinaison
 * d'un système de référence de coordonnées (CRS), et d'une géométrie munie de
 * coordonnées (CoordGeom).
 * <p>
 * Les methodes geometriques font par defaut appel a la bibliotheque JTS, via
 * des appels aux methodes de la classe {@link JtsAlgorithms}. Attention, bien
 * souvent, ces méthodes ne fonctionnent que sur des primitives ou des agrégats
 * homogènes GM_MultiPrimitive.
 * <p>
 * Historiquememt, les methodes faisaient appel aux fonctions geometriques
 * d'Oracle et a la bibliotheque fournie par Oracle sdoapi.zip, via des appels
 * aux methodes de la classe util.algo.OracleAlgorithms, qui elles-memes
 * appellent des methodes datatools.oracle.SpatialQuery. Ces methodes ont ete
 * gardees et portent le suffixe "Oracle". Pour les appeler, il est nécessaire
 * d'établir une connection à Oracle, c'est pourquoi on passe une "Geodatabase"
 * en paramètre de chaque fonction. On suppose qu'il existe dans la base, dans
 * le schéma utilisateur, une table TEMP_REQUETE, avec une colonne GID (NUMBER)
 * et une colonne GEOM (SDO_GEOMETRY). Cette table est dédiée aux requêtes
 * spatiales. De même, le paramètre tolérance est exigé par Oracle.
 * 
 * ARNAUD 12 juillet 2005 : mise en commentaire de ce qui se rapporte à Oracle
 * pour isoler la compilation. A décommenter pour utiliser Oracle.
 * 
 * @author Thierry Badard & Arnaud Braun
 * 
 */
public abstract class GM_Object implements Cloneable, IGeometry {
	/**
	 * Identifiant de l'objet géométrique, dans la table du SGBD. Cet
	 * identifiant n'est pas spécifié dans la norme ISO. Non utilise a ce jour.
	 */
	// protected int GM_ObjectID;
	/** Renvoie l'identifiant géométrique. */
	// public int getGM_ObjectID() { return this.GM_ObjectID; }
	/** Affecte un identifiant. */
	// public void setGM_ObjectID(int geomID) { this.GM_ObjectID = geomID; }

	/**
	 * FT_Feature auquel est rattaché cette géométrie. Cette association n'est
	 * pas dans la norme. A prevoir : faire une liste pour gérer les partages de
	 * géométrie.
	 */
	// protected FT_Feature feature;
	/** Renvoie le FT_Feature auquel est rattaché cette géométrie. */
	// public FT_Feature getFeature() { return this.feature; }
	/** Affecte un FT_Feature. */
	// public void setFeature(FT_Feature Feature) { this.feature = Feature;}

	/**
	 * Identifiant du système de coordonnées de référence (CRS en anglais). Par
	 * défaut, vaut 41014 : identifiant du Lambert II carto. Dans la norme ISO,
	 * cet attribut est une relation qui pointe vers la classe SC_CRS (non
	 * implémentée)
	 */
	protected int CRS = -1;

	@Override
	public int getCRS() {
		return this.CRS;
	}

	@Override
	public void setCRS(int crs) {
		this.CRS = crs;
	}

	@Override
	abstract public IBoundary boundary();

	/**
	 * Union de l'objet et de sa frontière. Si l'objet est dans un GM_Complex,
	 * alors la frontière du GM_Complex retourné doit être dans le même complexe
	 * ; Si l'objet n'est pas dans un GM_Complex, alors sa frontière doit être
	 * construite en réponse à cette opération.
	 */
	// public GM_Complex closure() {
	// }

	/** Set de complexes maximaux auxquels apppartient l'objet. */
	// public GM_Complex[] maximalComplex() {
	// }

	/** Renvoie TRUE si la frontière est vide. */
	// public boolean isCycle() {
	// }
	@Override
	abstract public IDirectPositionList coord();

	@Override
	public Object clone() {
		// FIXME j'ai comme un doute que ça marche ça
		try {
			return super.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		try {
			return WktGeOxygene.makeWkt(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void exportWkt(String path, boolean append) {
		try {
			WktGeOxygene.writeWkt(path, append, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exporte des géométries dans une image. Le format de l'image (.jpg ou .png
	 * par defaut) est determiné par l'extension du nom de fichier, a mettre
	 * dans le parametre "path". Le tableau de couleur permet d'affecter des
	 * couleurs différentes aux géométries. <BR>
	 * Exemple : GM_Object.exportImage(new GM_Object[] {geom1,
	 * geom2},"/home/users/truc/essai.jpg", new Color[] {Color.RED, Color.BLUE},
	 * Color.WHITE, 150, 80)
	 */
	public static void exportImage(IGeometry[] geoms, String path,
			Color foreground[], Color background, int width, int height) {
		try {
			ImgUtil.saveImage(geoms, path, foreground, background, width,
					height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exporte des géométries dans un fichier SVG compressé. Donner dans la
	 * variable "path" le chemin et le nom du fichier (avec l'extension .svgz)
	 * Le tableau de couleur permet d'affecter des couleurs différentes aux
	 * géométries. <BR>
	 * Exemple : GM_Object.exportSvgz(new GM_Object[] {geom1,
	 * geom2},"/home/users/truc/essai.jpg", new Color[] {Color.RED, Color.BLUE},
	 * Color.WHITE, 150, 80)
	 */
	public static void exportSvgz(IGeometry[] geoms, String path,
			Color foreground[], Color background, int width, int height) {
		try {
			ImgUtil.saveSvgz(geoms, path, foreground, background, width, height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IEnvelope envelope() {
		IDirectPositionList list = this.coord();
		if (list.isEmpty()) {
			return null;
		}
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		for (IDirectPosition point : list) {
			if (!Double.isNaN(point.getX())) {
				xmin = Math.min(xmin, point.getX());
			}
			if (!Double.isNaN(point.getX())) {
				xmax = Math.max(xmax, point.getX());
			}
			if (!Double.isNaN(point.getY())) {
				ymin = Math.min(ymin, point.getY());
			}
			if (!Double.isNaN(point.getY())) {
				ymax = Math.max(ymax, point.getY());
			}
		}
		return new GM_Envelope(xmin, xmax, ymin, ymax);
	}

	IEnvelope envelope = null;

	@Override
	public IEnvelope getEnvelope() {
		if (this.envelope == null) {
			this.envelope = this.envelope();
		}
		return this.envelope;
	}

	@Override
	public IPolygon mbRegion() {
		return new GM_Polygon(this.envelope());
	}

	@Override
	public boolean intersectsStrictement(IGeometry geom) {
		return (this.intersects(geom) && !this.contains(geom)
				&& !geom.contains(this) && !this.touches(geom));
	}

	@Override
	public IDirectPosition centroid() {
		return new JtsAlgorithms().centroid(this);
	}

	@Override
	public IGeometry convexHull() {
		return new JtsAlgorithms().convexHull(this);
	}

	@Override
	public IGeometry buffer(double distance) {
		return new JtsAlgorithms().buffer(this, distance);
	}

	@Override
	public IGeometry buffer(double distance, int nSegments) {
		return new JtsAlgorithms().buffer(this, distance, nSegments);
	}

	/**
	 * Calcule de buffer sur l'objet (avec JTS) en indiquant le nombre de
	 * segments approximant la partie courbe. Les distances negatives sont
	 * acceptees (pour faire une erosion).
	 * 
	 * @param distance
	 *            distance utilisee pour le calcul du buffer
	 * @param nSegments
	 *            nombre de segments utilises pour approximer les parties
	 *            courbes du buffer
	 * @param cap
	 *            forme du chapeau à utiliser
	 * @return buffer sur l'objet
	 * @see #buffer(double)
	 */
	public IGeometry buffer(double distance, int nSegments, int cap) {
		return new JtsAlgorithms().buffer(this, distance, nSegments, cap);
	}

	@Override
	public IGeometry buffer(double distance, int nSegments, int cap, int join) {
		return new JtsAlgorithms().buffer(this, distance, nSegments, cap, join);
	}

	@Override
	public IGeometry union(IGeometry geom) {
		return new JtsAlgorithms().union(this, geom);
	}

	@Override
	public IGeometry intersection(IGeometry geom) {
		return new JtsAlgorithms().intersection(this, geom);
	}

	@Override
	public IGeometry difference(IGeometry geom) {
		return new JtsAlgorithms().difference(this, geom);
	}

	@Override
	public IGeometry symmetricDifference(IGeometry geom) {
		return new JtsAlgorithms().symDifference(this, geom);
	}

	@Override
	public boolean equals(IGeometry geom) {
		return new JtsAlgorithms().equals(this, geom);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (this.getClass().equals(o.getClass())) {
			return this.equals((IGeometry) o);
		}

		return false;
	}

	@Override
	public boolean equalsExact(IGeometry geom) {
		return new JtsAlgorithms().equalsExact(this, geom);
	}

	@Override
	public boolean equalsExact(IGeometry geom, double tolerance) {
		return new JtsAlgorithms().equalsExact(this, geom, tolerance);
	}

	@Override
	public boolean contains(IGeometry geom) {
		return new JtsAlgorithms().contains(this, geom);
	}

	@Override
	public boolean crosses(IGeometry geom) {
		return new JtsAlgorithms().crosses(this, geom);
	}

	@Override
	public boolean disjoint(IGeometry geom) {
		return new JtsAlgorithms().disjoint(this, geom);
	}

	@Override
	public boolean within(IGeometry geom) {
		return new JtsAlgorithms().within(this, geom);
	}

	@Override
	public boolean isWithinDistance(IGeometry geom, double distance) {
		return new JtsAlgorithms().isWithinDistance(this, geom, distance);
	}

	@Override
	public boolean intersects(IGeometry geom) {
		return new JtsAlgorithms().intersects(this, geom);
	}

	@Override
	public boolean overlaps(IGeometry geom) {
		return new JtsAlgorithms().overlaps(this, geom);
	}

	@Override
	public boolean touches(IGeometry geom) {
		return new JtsAlgorithms().touches(this, geom);
	}

	@Override
	public boolean isEmpty() {
		return new JtsAlgorithms().isEmpty(this);
	}

	@Override
	public boolean isSimple() {
		return new JtsAlgorithms().isSimple(this);
	}

	@Override
	public boolean isValid() {
		return new JtsAlgorithms().isValid(this);
	}

	@Override
	public double distance(IGeometry geom) {
		return new JtsAlgorithms().distance(this, geom);
	}

	@Override
	public double area() {
		return new JtsAlgorithms().area(this);
	}

	@Override
	public double length() {
		return new JtsAlgorithms().length(this);
	}

	@Override
	public int coordinateDimension() {
		IDirectPositionList dpl = this.coord();

		if (dpl == null || dpl.size() == 0) {
			return -1;
		}

		if (Double.isNaN(dpl.get(0).getZ())) {

			return 2;
		}

		return 3;
	}

	@Override
	public int dimension() {
		if (this instanceof GM_Solid) {
			return 3;
		}
		if (this instanceof GM_MultiSolid<?>) {
			return 3;
		}
		if (this instanceof GM_CompositeSolid) {
			return 3;
		}
		if (this instanceof GM_OrientableSurface) {
			return 2;
		}
		if (this instanceof GM_MultiSurface<?>) {
			return 2;
		}
		if (this instanceof GM_CompositeSurface) {
			return 2;
		}
		if (this instanceof GM_OrientableCurve) {
			return 1;
		}
		if (this instanceof GM_MultiCurve<?>) {
			return 1;
		}
		if (this instanceof GM_CompositeCurve) {
			return 1;
		}
		if (this instanceof GM_MultiPoint) {
			return 0;
		}
		if (this instanceof GM_Point) {
			return 0;
		}
		return new JtsAlgorithms().dimension(this);
	}

	@Override
	public int numPoints() {
		return new JtsAlgorithms().numPoints(this);
	}

	@Override
	public IGeometry translate(final double tx, final double ty, final double tz) {
		return new JtsAlgorithms().translate(this, tx, ty, tz);
	}

	@Override
	public String relate(IGeometry geom) {
		return new JtsAlgorithms().relate(this, geom);
	}

	@Override
	public boolean isLineString() {
		return false;
	}

	@Override
	public boolean isMultiCurve() {
		return false;
	}

	@Override
	public boolean isPolygon() {
		return false;
	}

	@Override
	public boolean isMultiSurface() {
		return false;
	}

	@Override
	public boolean isPoint() {
		return false;
	}
}
