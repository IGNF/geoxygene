package fr.ign.cogit.geoxygene.sig3d.convert.transform;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * 
 *          Classe permettant d'obtenir des geometries de dimension N +1 a
 *          partir d'objets de dimension N et d'une hauteur. Tous les objets à
 *          utiliser doivent avoir des geometries 3D Class to get an extrusion
 *          of 3D geometries
 * 
 */
public class Extrusion3DObject {

	private final static Logger logger = Logger.getLogger(Extrusion3DObject.class.getName());

	/**
	 * Convertit des géométries 3D d'une dimension N en géométrie à dimension N
	 * +1 en les extrudant suivant un attribut hauteur pouvant être négatif
	 * 
	 * @param geom
	 *            la geometrie qui sera extrudee
	 * @param heigth
	 *            la hauteur que l'on appliquera. La geometrie renvoyee sera
	 *            elle- meme si la hauteur vaut 0 ou n'est pas un nombre
	 * @return un objet extrudé à partir de la géomtrie initiale
	 */
	@SuppressWarnings("unchecked")
	public static IGeometry conversionFromGeom(IGeometry geom, double heigth) {

		if (heigth == 0) {

			return geom;
		}

		if (geom == null) {
			Extrusion3DObject.logger.debug(Messages.getString("3DGIS.GeomEmpty"));
			return null;
		}

		IGeometry geomFinale = null;

		// On essaie de retrouver la classe de la geometrie
		// Ordre de test simple puis complexe de la dim la plus elevee a la
		// plus faible
		/*
		 * Extrusion3DObject.logger.debug(Messages
		 * .getString("FenetreShapeFile3D.FeatureConversion") + " : " +
		 * geom.toString());
		 */

		if (geom instanceof IPolygon) {

			geomFinale = Extrusion3DObject.convertitFromPolygon((GM_Polygon) geom, heigth);

		} else if (geom instanceof IMultiSurface<?>) {

			GM_MultiSurface<IOrientableSurface> multiS = (GM_MultiSurface<IOrientableSurface>) geom;

			if (multiS.size() == 1) {

				geomFinale = Extrusion3DObject.convertitFromPolygon((GM_Polygon) multiS.get(0), heigth);

			} else {

				geomFinale = Extrusion3DObject.convertitFromMultiPolygon(multiS, heigth);

			}

		} else if (geom instanceof ILineString) {

			geomFinale = Extrusion3DObject.convertitFromLine((GM_LineString) geom, heigth);

		} else if (geom instanceof IMultiCurve<?>) {

			geomFinale = Extrusion3DObject.convertitFromMultiLineString((GM_MultiCurve<?>) geom, heigth);

		} else if (geom instanceof IPoint) {

			geomFinale = Extrusion3DObject.convertitFromPoint((GM_Point) geom, heigth);

		} else if (geom instanceof IMultiPoint) {

			geomFinale = Extrusion3DObject.convertitFromMultiPoint((GM_MultiPoint) geom, heigth);

		} else if (geom instanceof IAggregate<?>) {
			IAggregate<IGeometry> aggregate = (IAggregate<IGeometry>) geom;

			IAggregate<IGeometry> agg = new GM_Aggregate<IGeometry>();
			for (IGeometry g : aggregate) {
				agg.add(conversionFromGeom(g, heigth));
			}

			return agg;

		} else {

			/*
			 * Extrusion3DObject.logger.warn(Messages
			 * .getString("Representation.GeomUnk") +
			 * geom.getClass().getName());
			 */
			return null;
		}

		return geomFinale;

	}

	/**
	 * Extrude un point suivant une hauteur H. Le resultat est soit un
	 * GM_MultiPoint soir un GM_MultiLineString.
	 * 
	 * @param multiP
	 *            le multipoint que l'on souhaite extruder
	 * @param heigth
	 *            la hauteur d'extrusion
	 * @return l'objet renvoye l'objet lui meme si la hauteur vaut 0 ou est
	 *         invalide. Sinon, il s'agit d'une extrusion verticale
	 */
	private static IGeometry convertitFromMultiPoint(IMultiPoint multiP, double heigth) {
		int nbPoints = multiP.size();

		if (nbPoints == 0) {

			return null;
		}

		if (heigth == 0 || Double.isNaN(heigth)) {

			return multiP;

		} else {

			GM_MultiCurve<GM_LineString> multiC = new GM_MultiCurve<GM_LineString>();

			for (int i = 0; i < nbPoints; i++) {
				multiC.add((GM_LineString) Extrusion3DObject.convertitFromPoint(multiP.get(i), heigth));

			}
			return multiC;
		}
	}

	/**
	 * Extrude un point suivant une hauteur H. Le resultat est soit un GM_Point
	 * soir un GM_LineString.
	 * 
	 * @param point
	 *            le point que l'on souhaite extruder
	 * @param heigth
	 *            la hauteur d'extrusion
	 * @return l'objet renvoye l'objet lui meme si la hauteur vaut 0 ou est
	 *         invalide. Sinon, il s'agit d'une extrusion verticale
	 */
	private static IGeometry convertitFromPoint(IPoint point, double heigth) {

		if (heigth == 0 || Double.isNaN(heigth)) {

			return point;
		}

		IDirectPosition pIni = point.coord().get(0);

		double z = pIni.getZ() + heigth;

		IDirectPosition pFin = new DirectPosition(pIni.getX(), pIni.getY(), z);

		IDirectPositionList dpl = new DirectPositionList();
		dpl.add(pIni);
		dpl.add(pFin);

		return new GM_LineString(dpl);
	}

	/**
	 * Renvoie l'extrusion d'un GM_MultiLineString 3D suivant une hauteur
	 * hauteur. Le résultat est soit un GM_MultiSurface soit un
	 * GM_MultiLineString
	 * 
	 * @param multiLS
	 *            la geometrie que l'on souhaite extruder
	 * @param heigth
	 *            la hauteur que l'on rajoute a la geometrie. Si la hauteur est
	 *            nulle ou n'est pas un nombre, la geometrie initiale est
	 *            renvoyee.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static IGeometry convertitFromMultiLineString(IMultiCurve<?> multiLS, double heigth) {
		// Recuperation des coordonnees du contour

		int nbLine = multiLS.size();

		if (nbLine == 0) {
			Extrusion3DObject.logger.debug(Messages.getString("3DGIS.GeomEmpty"));
			return null;
		}

		if (heigth == 0 || Double.isNaN(heigth)) {

			return multiLS;

		} else {

			GM_MultiSurface<GM_Polygon> mSurface = new GM_MultiSurface<GM_Polygon>();
			// On decompose le multiPolygon en liste de polygones
			for (int indpoly = 0; indpoly < nbLine; indpoly++) {

				// On applique la transformation a chaque polygone
				GM_LineString p = (GM_LineString) multiLS.get(indpoly);
				mSurface.addAll((GM_MultiSurface<GM_Polygon>) Extrusion3DObject.convertitFromLine(p, heigth));
			}

			return mSurface;
		}
	}

	/**
	 * Renvoie l'extrusion d'un GM_LineString 3D suivant une hauteur hauteur. Le
	 * résultat est soit un GM_MultiSurface soit un GM_LineString
	 * 
	 * @TODO : faire cette méthode pour les autres géométries et trouver le
	 *       moyen de faire des méthodes plus génériques ....
	 * @param ls
	 *            la geometrie que l'on souhaite extruder
	 * @param zMin
	 *            le zMin que l'on rajoute a la geometrie. Si la hauteur est
	 *            nulle ou n'est pas un nombre, la geometrie initiale est
	 *            renvoyee.
	 * @return renvoie un objet extrudé à partir d'un objet GM_LineString objet
	 *         qui sera de type GM_MultiSurface ou GM_LineString
	 */
	public static IGeometry convertitFromLineUntilZMin(ICurve ls, double zMin) {
		// On recupere les points extremes du solides

		if (zMin == 0 || Double.isNaN(zMin)) {

			return ls;
		}

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
		IDirectPositionList lPoints = ls.coord();

		int nbPoints = lPoints.size();
		IDirectPosition pIni = lPoints.get(nbPoints - 1);

		for (int j = 0; j < nbPoints; j++) {

			DirectPositionList fTemp = new DirectPositionList();

			IDirectPosition pSuiv = lPoints.get(j);

			DirectPosition pSuivZmin = new DirectPosition(pSuiv.getX(), pSuiv.getY(), zMin);
			DirectPosition pIniZmin = new DirectPosition(pIni.getX(), pIni.getY(), zMin);

			fTemp.add(pSuiv);
			fTemp.add(pSuivZmin);
			fTemp.add(pIniZmin);

			fTemp.add(pIni);
			fTemp.add(pSuiv);

			pIni = pSuiv;

			GM_LineString lS = new GM_LineString(fTemp);
			GM_Ring gmRing = new GM_Ring(lS);
			GM_OrientableSurface oS = new GM_Polygon(gmRing);

			lOS.add(oS);

		}

		return new GM_MultiSurface<IOrientableSurface>(lOS);
	}

	/**
	 * Renvoie l'extrusion d'un GM_LineString 3D suivant une hauteur hauteur. Le
	 * résultat est soit un GM_MultiSurface soit un GM_LineString
	 * 
	 * @param ls
	 *            la geometrie que l'on souhaite extruder
	 * @param heigth
	 *            la hauteur que l'on rajoute a la geometrie. Si la hauteur est
	 *            nulle ou n'est pas un nombre, la geometrie initiale est
	 *            renvoyee.
	 * @return renvoie un objet extrudé à partir d'un objet GM_LineString objet
	 *         qui sera de type GM_MultiSurface ou GM_LineString
	 */
	public static IGeometry convertitFromLine(ICurve ls, double heigth) {
		// On recupere les points extremes du solides

		if (heigth == 0 || Double.isNaN(heigth)) {

			return ls;
		}

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
		IDirectPositionList lPoints = ls.coord();

		int nbPoints = lPoints.size();
		IDirectPosition pIni = lPoints.get(nbPoints - 1);

		for (int j = 0; j < nbPoints; j++) {

			DirectPositionList fTemp = new DirectPositionList();

			IDirectPosition pSuiv = lPoints.get(j);

			DirectPosition pSuivZmin = new DirectPosition(pSuiv.getX(), pSuiv.getY(), pSuiv.getZ() + heigth);
			DirectPosition pIniZmin = new DirectPosition(pIni.getX(), pIni.getY(), pIni.getZ() + heigth);

			fTemp.add(pSuiv);
			fTemp.add(pSuivZmin);
			fTemp.add(pIniZmin);

			fTemp.add(pIni);

			pIni = pSuiv;

			GM_LineString lS = new GM_LineString(fTemp);
			GM_Ring gmRing = new GM_Ring(lS);
			GM_OrientableSurface oS = new GM_Polygon(gmRing);

			lOS.add(oS);

		}

		return new GM_MultiSurface<IOrientableSurface>(lOS);
	}

	/**
	 * Convertit un polygone 3D en GM_MultiSolid ou en GM_MultiSurface
	 * 
	 * @param multiS
	 *            le multipolygone 3D initial
	 * @param heigth
	 *            la hauteur que l'on applique pour obtenir un solide
	 * @return un solide obtenu par une extrusion droite d'un hauteur hauteur
	 */
	public static IGeometry convertitFromMultiPolygon(IMultiSurface<IOrientableSurface> multiS, double heigth) {
		// Recuperation des coordonnees du contour

		int nbPolygon = multiS.size();

		if (nbPolygon == 0) {

			return null;
		}

		if (heigth == 0 || Double.isNaN(heigth)) {

			return multiS;

		} else {

			GM_MultiSolid<GM_Solid> mSolid = new GM_MultiSolid<GM_Solid>();
			// On decompose le multiPolygon en liste de polygones
			for (int indpoly = 0; indpoly < nbPolygon; indpoly++) {

				// On applique la transformation a chaque polygone
				GM_Polygon p = (GM_Polygon) multiS.get(indpoly);
				mSolid.add((GM_Solid) Extrusion3DObject.convertitFromPolygon(p, heigth));

			}

			// Solide
			return mSolid;
		}
	}

	/**
	 * Convertit un polygone 3D en GM_Solid ou GM_Polygon
	 * 
	 * @param polyIni
	 *            le polygone 3D initial
	 * @param heigth
	 *            la hauteur que l'on applique pour obtenir un solide. Si la
	 *            hauteur vaut 0, le polygone initial est renvoyé
	 * @return un solide obtenu par une extrusion droite d'un hauteur hauteur
	 */
	public static IGeometry convertitFromPolygon(IPolygon polyIni, double heigth) {
		// On recupere les points extremes du solides

		ApproximatedPlanEquation eq = new ApproximatedPlanEquation(polyIni);
		if (eq.getNormale().getZ() < 0) {

			polyIni = new GM_Polygon(polyIni.reverse().boundary());
		}

		if (heigth == 0 || Double.isNaN(heigth)) {

			return polyIni;
		}

		if (heigth < 0) {
			Extrusion3DObject.logger.debug(Messages.getString("FenetreShapeFile3D.NegativeExtrusion"));

		} else if (heigth > 0) {
			Extrusion3DObject.logger.debug(Messages.getString("FenetreShapeFile3D.PositiveExtrusion"));

		}

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

		int nbContrib = 1 + polyIni.getInterior().size();

		IPolygon poly = new GM_Polygon();

		for (int idContrib = 0; idContrib < nbContrib; idContrib++) {

			IDirectPositionList lPExt = new DirectPositionList();

			IDirectPositionList lPoints;
			if (idContrib == 0) {
				lPoints = polyIni.getExterior().coord();
			} else {
				lPoints = polyIni.getInterior().get(idContrib - 1).coord();
			}

			int nbPoints = lPoints.size();

			if (!lPoints.get(0).equals(lPoints.get(nbPoints - 1))) {
				lPoints.add(lPoints.get(0));
			}

			IDirectPosition pIni = lPoints.get(0);

			for (int j = 1; j < nbPoints; j++) {

				DirectPositionList fTemp = new DirectPositionList();

				IDirectPosition pSuiv = lPoints.get(j);

				DirectPosition pSuivZmin = new DirectPosition(pSuiv.getX(), pSuiv.getY(), pSuiv.getZ() + heigth);
				DirectPosition pIniZmin = new DirectPosition(pIni.getX(), pIni.getY(), pIni.getZ() + heigth);

				lPExt.add(pIniZmin);

				fTemp.add(pIni);
				fTemp.add(pSuiv);
				fTemp.add(pSuivZmin);
				fTemp.add(pIniZmin);

				fTemp.add(pIni);

				pIni = pSuiv;

				GM_LineString lS = new GM_LineString(fTemp);
				GM_Ring gmRing = new GM_Ring(lS);
				GM_OrientableSurface oS = new GM_Polygon(gmRing);

				lOS.add(oS);

				if (j == (nbPoints - 1)) {
					lPExt.add(pSuivZmin);
				}

			}

			if (idContrib == 0) {
				poly.setExterior(new GM_Ring(new GM_LineString(lPExt)));
			} else {
				poly.addInterior(new GM_Ring(new GM_LineString(lPExt)));

			}

		}

		GM_Polygon surFHaut = (GM_Polygon) polyIni.clone();

		lOS.add(surFHaut);
		lOS.add(poly);

		return new GM_Solid(lOS);
	}
}
