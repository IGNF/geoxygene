package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.index.SpatialIndex;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Orientation;
import fr.ign.cogit.geoxygene.sig3d.calculation.Proximity;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
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
 * @version 1.7
 */
public class RayCasting {

	private final static Logger logger = Logger.getLogger(RayCasting.class);
	/**
	 * Epsilon pour arrondir les zéro
	 */
	public static double EPSILON = 0.01;
	
	public static double EPSILON_INSIDE_POLYGON = 0.01;

	public static int TYPE_FIRST_POINT_INTERSECTED = 0;
	public static int TYPE_POINT_ALL_INTERSECTION = 1;
	public static int TYPE_FIRST_POINT_AND_SPHERE = 2;
	public static int TYPE_CAST_SOLID_POINT = 3;
	public static int TYPE_CAST_SOLID_POINT_GAUSS = 4;

	public static int TYPE_FIRST_AND_SPHERE_OPTIMIZED = 98;
	public static int TYPE_FIRST_POINT_INTERSECTED_OPTIMIZED = 99;

	// Rajoute une vérification supplémentaire
	// Permet de considérer comme intersection des points qui
	// Touchent presque les arrêtes (distance < EPSILON)
	public static boolean CHECK_IS_ON_EDGE = true;

	private GM_Solid solGenerated = null;
	private GM_Polygon polGenerated = null;

	private IDirectPosition centre;
	private IFeatureCollection<IFeature> lFeat;
	private int nbPointsCouronnes;
	private double rayon;
	private IDirectPositionList dpGenerated;
	private int lastTypeResult;
	private boolean isSphere;
	private int resultType;

	/**
	 * 
	 * 
	 * Permet d'instancier et d'effectuer un lancer de rayon sur la demi-sphère
	 * positive
	 * 
	 * @param centre
	 *            le centre du lancer de rayon
	 * @param lFeat
	 *            les entités (qui doivent pour l'instant être surfaciques) sur
	 *            lesquels on effectue le lancer
	 * @param nbPointsCouronnes
	 *            le nombre de point sur une couronne
	 * @param rayon
	 *            le rayon dans lequel on effectule lancer
	 * @param resultType
	 *            le type de résultat que l'on souhaite
	 * @param aims
	 *            les points que l'on souhaite viser (cas de :
	 *            TYPE_CAST_SOLID_POINT)
	 * @param sphere
	 *            indique si le lancer de rayon se fait sur une sphère ou une
	 *            demi-sphère. Ne vaut pas pour le cas TYPE_CAST_SOLID_POINT
	 * 
	 */
	public RayCasting(IDirectPosition centre, IFeatureCollection<IFeature> lFeat, int nbPointsCouronnes, double rayon,
			int resultType, boolean isSphere) {
		this.centre = centre;
		this.lFeat = lFeat;
		this.nbPointsCouronnes = nbPointsCouronnes;
		this.rayon = rayon;
		this.lastTypeResult = resultType;
		this.isSphere = isSphere;

		this.resultType = resultType;

	}

	public void cast() {
		if (resultType == RayCasting.TYPE_CAST_SOLID_POINT) {

			this.dpGenerated = this.castOnSolidPosition(centre, lFeat, nbPointsCouronnes, rayon, resultType);
		} else if (resultType == RayCasting.TYPE_FIRST_AND_SPHERE_OPTIMIZED) {

			this.dpGenerated = this.castOptimized(centre, lFeat, nbPointsCouronnes, rayon, isSphere);

		} else {
			this.dpGenerated = this.cast(centre, lFeat, nbPointsCouronnes, rayon, resultType, isSphere);
		}

	}

	/**
	 * Methode permettant de créer un lancer de rayon positive
	 * 
	 * @param centre
	 *            le centre du lancer de rayon
	 * @param lFeat
	 *            les entités (qui doivent pour l'instant être surfaciques) sur
	 *            lesquels on effectue le lancer
	 * @param nbPointsCouronnes
	 *            le nombre de point sur une couronne
	 * @param rayon
	 *            le rayon dans lequel on effectule lancer
	 * @param optimized
	 *            optimisation en termes de temps de calcul qui ne donne pas
	 *            toujours des résultats exacts
	 * @return une liste de point correspondant aux intersections entre les
	 *         rayons et les faces des géométries
	 */
	private IDirectPositionList cast(IDirectPosition centre, IFeatureCollection<IFeature> lFeat, int nbPointsCouronnes,
			double rayon, int resultType, boolean isSphere) {

		// Pas angulaire (on ne fait que la demis sphère z > 0
		double pasAlpha = 2 * Math.PI / nbPointsCouronnes;

		double pasBeta = 2 * Math.PI / (nbPointsCouronnes);

		// Les points intersectés
		DirectPositionList dplOut = new DirectPositionList();

		// On créer une projection sphérique
		// Elle permettra de trouver les entités potentiellement intersectées
		// par le
		// lancer grâce aux coordonnées angulaires
		SphericalProjection sp = new SphericalProjection(lFeat, centre, rayon, true);
		this.sphericalProjection = sp;

		IFeatureCollection<IFeature> featC = sp.getLFeatMapped();

		IFeatureCollection<IFeature> featCut = sp.getFeatToProject();

		int nbElemT = featC.size();

		// On supprimer les géométries non valides
		// Géométries plates ou sous la demisphère
		for (int i = 0; i < nbElemT; i++) {

			if (!featC.get(i).getGeom().isValid()) {
				featC.remove(i);
				featCut.remove(i);
				i--;
				nbElemT--;
				continue;
			}
			AttributeManager.addAttribute(featC.get(i), "ind", i, "Integer");

		}

		// L'index spatiale permettra de retrouver en coordonnées angulaires
		// les faces concernées par l'intersection
		featC.initSpatialIndex(Tiling.class, false);

		Tiling<IFeature> sI = (Tiling<IFeature>) featC.getSpatialIndex();

		int nbPCouronnesP = nbPointsCouronnes;
		int nbPCouronnesA = nbPointsCouronnes / 4;

		if (isSphere) {
			nbPCouronnesA = nbPCouronnesA * 2;

		}

		// On effectue un rayonnement angulaire
		// de pas angulaire constant
		// On effectue un rayonnement angulaire
		// de pas angulaire constant
		for (int i = 0; i < nbPCouronnesP; i++) {

			for (int j = 0; j < nbPCouronnesA; j++) {

				// Alpha : angle horizontale par rapport au Nord
				double alpha = i * pasAlpha;

				// Beta : angle vertical par rapport à l'horizontale
				double beta;

				if (isSphere) {
					beta = j * pasBeta - Math.PI / 2;
				} else {
					beta = j * pasBeta;

				}
				// On précalcule leur cosinus et sinus
				double cosI = Math.cos(alpha);
				double sinI = Math.sin(alpha);

				double cosJ = Math.cos(beta);
				double sinJ = Math.sin(beta);

				// Grâce à l'index, on récupère les faces concernées
				Collection<IFeature> cf = sI.select(new DirectPosition(alpha, beta), RayCasting.EPSILON);

				// Cette direction n'a pas de face, on continue

				int nbPol2 = cf.size();

				DirectPosition dpAim = new DirectPosition(centre.getX() + rayon * sinI * cosJ,
						centre.getY() + rayon * cosI * cosJ, centre.getZ() + rayon * sinJ);

				// System.out.println("Alpha SP" +
				// sp.calculAngle(dpAim).getAlpha().getValeur() +
				// " angle alpha "+alpha
				// );

				// System.out.println((new Vecteur(centre,dpAim)).getCoord() +
				// " angle "+alpha+" cosAl "+cosI);
				// Cette direction n'a pas de face, on continue
				if (0 == nbPol2) {

					if (resultType == RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {

						dplOut.add(dpAim);

					}

					continue;
				}

				// On créer une équation linéaire
				LineEquation lE = new LineEquation(centre, dpAim);

				IDirectPosition dpTempOk = null;

				// On calcule les lancers de rayon
				Iterator<IFeature> itFeat = cf.iterator();

				if (resultType == RayCasting.TYPE_FIRST_POINT_INTERSECTED_OPTIMIZED) {

					IFeature featOutTemp = this.getOptimized(itFeat);
					int ind = featC.getElements().indexOf(featOutTemp);

					dpTempOk = RayCasting.intersectionPolygonLine(lE, (GM_Polygon) featCut.get(ind).getGeom());

				} else {

					// Cas autre : TYPE_POINT_ALL_INTERSECTION &&
					// TYPE_FIRST_POINT_INTERSECTED
					// TYPE_FIRST_POINT_AND_SPHERE

					double dMin = Double.POSITIVE_INFINITY;

					for (int k = 0; k < nbPol2; k++) {

						IFeature featInter = itFeat.next();

						// int ind = featC.getElements().indexOf(featInter);
						int ind = Integer.parseInt(featInter.getAttribute("ind").toString());

						// if( !
						// featInter.getAttribute("ind").toString().equalsIgnoreCase(ind
						// +"") ){
						// System.out.println("indice" + ind + "
						// featC"+featInter.getAttribute("ind") );
						// }

						// featInter.getAttribute("ind")

						// System.out.println("indice" + ind + "
						// featC"+featInter.getAttribute("ind") );

						// On calcule le point d'intersection
						IDirectPosition dpTemp = RayCasting.intersectionPolygonLine(lE,
								(GM_Polygon) featCut.get(ind).getGeom());

						if (dpTemp == null) {

							continue;
						}

						double dTemp = centre.distance(dpTemp);

						if (resultType == RayCasting.TYPE_POINT_ALL_INTERSECTION) {

							dplOut.add(dpTemp);
							continue;
						}

						// On effectue un tri par distance
						if (dMin > dTemp && dTemp <= this.rayon) {
							dpTempOk = dpTemp;
							dMin = dTemp;

						}

					} // Boucle k

				}
				// dpTempOK == null normalement si resultType ==
				// TYPE_POINT_ALL_INTERSECTION
				// Ca éviter d'avoir plusieurs fois le même point dans ce type
				// de
				// résultat
				if (dpTempOk != null) {
					// On garde le plus proche (si il existe)
					dplOut.add(dpTempOk);

				} else {
					if (resultType == RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {

						dplOut.add(dpAim);

					}
				}
			} // Fin boucle j
		} // Fin boucle i
			// On renvoie les points crées

		return dplOut;

	}

	/**
	 * Methode permettant de créer un lancer de rayon positive
	 * 
	 * @param centre
	 *            le centre du lancer de rayon
	 * @param lFeat
	 *            les entités (qui doivent pour l'instant être surfaciques) sur
	 *            lesquels on effectue le lancer
	 * @param nbPointsCouronnes
	 *            le nombre de point sur une couronne
	 * @param rayon
	 *            le rayon dans lequel on effectule lancer
	 * @param optimized
	 *            optimisation en termes de temps de calcul qui ne donne pas
	 *            toujours des résultats exacts
	 * @return une liste de point correspondant aux intersections entre les
	 *         rayons et les faces des géométries
	 */
	private DirectPositionList castOptimized(IDirectPosition centre, IFeatureCollection<IFeature> lFeat,
			int nbPointsCouronnes, double rayon, boolean isSphere) {

		// Pas angulaire (on ne fait que la demis sphère z > 0
		double pasAlpha = 2 * Math.PI / nbPointsCouronnes;

		double pasBeta;
		if (isSphere) {

			pasBeta = Math.PI / (nbPointsCouronnes);
		} else {
			pasBeta = Math.PI / (nbPointsCouronnes * 2);
		}

		// Les points intersectés
		DirectPositionList dplOut = new DirectPositionList();

		// On créer une projection sphérique
		// Elle permettra de trouver les entités potentiellement intersectées
		// par le
		// lancer grâce aux coordonnées angulaires
		SphericalProjection sp = new SphericalProjection(lFeat, centre, rayon, true);
		this.sphericalProjection = sp;

		IFeatureCollection<IFeature> featC = sp.getLFeatMapped();

		IFeatureCollection<IFeature> featCut = sp.getFeatToProject();

		int nbElemT = featC.size();

		// On supprimer les géométries non valides
		// Géométries plates ou sous la demisphère
		for (int i = 0; i < nbElemT; i++) {

			if (!featC.get(i).getGeom().isValid()) {
				featC.remove(i);
				featCut.remove(i);
				i--;
				nbElemT--;

			}

		}

		// L'index spatiale permettra de retrouver en coordonnées angulaires
		// les faces concernées par l'intersection
		featC.initSpatialIndex(Tiling.class, false);

		SpatialIndex<IFeature> sI = featC.getSpatialIndex();
		// On effectue un rayonnement angulaire
		// de pas angulaire constant
		for (int j = 0; j < nbPointsCouronnes; j++) {

			boolean finished = false;

			for (int i = 0; i < nbPointsCouronnes; i++) {

				// Alpha : angle horizontale par rapport au Nord
				double alpha = i * pasAlpha;

				// Beta : angle vertical par rapport à l'horizontale
				double beta;

				if (isSphere) {
					beta = j * pasBeta - Math.PI / 2;
				} else {
					beta = j * pasBeta;

				}

				// On précalcule leur cosinus et sinus
				double cosI = Math.cos(alpha);
				double sinI = Math.sin(alpha);

				double cosJ = Math.cos(beta);
				double sinJ = Math.sin(beta);

				DirectPosition dpAim = new DirectPosition(centre.getX() + rayon * sinI * cosJ,
						centre.getY() + rayon * cosI * cosJ, centre.getZ() + rayon * sinJ);
				if (finished) {
					dplOut.add(dpAim);
					continue;

				}

				// Grâce à l'index, on récupère les faces concernées
				Collection<IFeature> cf = sI.select(new DirectPosition(alpha, beta), RayCasting.EPSILON);

				// Cette direction n'a pas de face, on continue

				int nbPol2 = cf.size();

				// Cette direction n'a pas de face, on continue
				if (0 == nbPol2) {

					dplOut.add(dpAim);
					finished = true;

					continue;
				}

				// On créer une équation linéaire
				LineEquation lE = new LineEquation(centre, dpAim);

				IDirectPosition dpTempOk = null;

				// On calcule les lancers de rayon
				Iterator<IFeature> itFeat = cf.iterator();

				// Cas autre : TYPE_POINT_ALL_INTERSECTION &&
				// TYPE_FIRST_POINT_INTERSECTED
				// TYPE_FIRST_POINT_AND_SPHERE

				double dMin = Double.POSITIVE_INFINITY;

				for (int k = 0; k < nbPol2; k++) {

					int ind = featC.getElements().indexOf(itFeat.next());

					// On calcule le point d'intersection
					IDirectPosition dpTemp = RayCasting.intersectionPolygonLine(lE,
							(GM_Polygon) featCut.get(ind).getGeom());

					if (dpTemp == null) {

						continue;
					}

					double dTemp = centre.distance(dpTemp);

					// On effectue un tri par distance
					if (dMin > dTemp && dTemp <= this.rayon) {
						dpTempOk = dpTemp;
						dMin = dTemp;

					}

				} // Boucle k

				// dpTempOK == null normalement si resultType ==
				// TYPE_POINT_ALL_INTERSECTION
				// Ca éviter d'avoir plusieurs fois le même point dans ce type
				// de
				// résultat
				if (dpTempOk != null) {
					// On garde le plus proche (si il existe)
					dplOut.add(dpTempOk);

				} else {

					dplOut.add(dpAim);
					finished = true;

				}
			} // Fin boucle i
		} // Fin boucle j
			// On renvoie les points crées

		return dplOut;

	}

	public IFeatureCollection<IFeature> castOnSolidPositionGauss(int nbLancers, double ecartTypeP, double ecartTypeA) {

		// Pas angulaire (on ne fait que la demis sphère z > 0
		double pasAlpha = 2 * Math.PI / nbPointsCouronnes;

		IFeatureCollection<IFeature> featCOut = new FT_FeatureCollection<IFeature>();

		double pasBeta = 2 * Math.PI / (nbPointsCouronnes);

		for (int p = 0; p < nbLancers; p++) {

			// System.out.println(p);

			// IDirectPosition centreBis = centre;
			IDirectPosition centreBis = SensibilitePosition.generateCoordinate(centre, ecartTypeP, ecartTypeA);

			// On créer une projection sphérique
			// Elle permettra de trouver les entités potentiellement
			// intersectées
			// par le
			// lancer grâce aux coordonnées angulaires
			SphericalProjection sp = new SphericalProjection(lFeat, centreBis, rayon, true);
			this.sphericalProjection = sp;

			IFeatureCollection<IFeature> featC = sp.getLFeatMapped();

			IFeatureCollection<IFeature> featCut = sp.getFeatToProject();

			int nbElemT = featC.size();

			// On supprimer les géométries non valides
			// Géométries plates ou sous la demisphère
			for (int i = 0; i < nbElemT; i++) {

				if (!featC.get(i).getGeom().isValid()) {
					featC.remove(i);
					featCut.remove(i);
					i--;
					nbElemT--;

				}

			}

			// L'index spatiale permettra de retrouver en coordonnées angulaires
			// les faces concernées par l'intersection
			featC.initSpatialIndex(Tiling.class, false);

			Tiling<IFeature> sI = (Tiling<IFeature>) featC.getSpatialIndex();

			int nbInter = 0;
			int nbLancerTotaux = 0;

			int nbPCouronnesP = nbPointsCouronnes;
			int nbPCouronnesA = nbPointsCouronnes / 4;

			if (isSphere) {
				nbPCouronnesA = nbPCouronnesA * 2;

			}

			// On effectue un rayonnement angulaire
			// de pas angulaire constant
			for (int i = 0; i < nbPCouronnesP; i++) {

				for (int j = 0; j < nbPCouronnesA; j++) {

					nbLancerTotaux++;

					// Alpha : angle horizontale par rapport au Nord
					double alpha = i * pasAlpha;

					// Beta : angle vertical par rapport à l'horizontale
					double beta;

					if (isSphere) {
						beta = j * pasBeta - Math.PI / 2;
					} else {
						beta = j * pasBeta;

					}

					// On précalcule leur cosinus et sinus

					// Grâce à l'index, on récupère les faces concernées
					Collection<IFeature> cf = sI.select(new DirectPosition(alpha, beta), RayCasting.EPSILON);

					// Cette direction n'a pas de face, on continue

					if (cf != null && cf.size() != 0) {
						nbInter++;
						continue;

					}

					/*
					 * 
					 * 
					 * 
					 * double cosI = Math.cos(alpha); double sinI =
					 * Math.sin(alpha);
					 * 
					 * double cosJ = Math.cos(beta); double sinJ =
					 * Math.sin(beta);
					 * 
					 * int nbPol2 = cf.size();
					 * 
					 * DirectPosition dpAim = new
					 * DirectPosition(centreBis.getX() + rayon sinI * cosJ,
					 * centreBis.getY() + rayon * cosI * cosJ, centreBis.getZ()
					 * + rayon * sinJ);
					 * 
					 * // System.out.println("Alpha SP" + //
					 * sp.calculAngle(dpAim).getAlpha().getValeur() + //
					 * "  angle alpha "+alpha // );
					 * 
					 * // System.out.println((new
					 * Vecteur(centre,dpAim)).getCoord() + //
					 * " angle "+alpha+" cosAl "+cosI); // Cette direction n'a
					 * pas de face, on continue
					 * 
					 * // On créer une équation linéaire LineEquation lE = new
					 * LineEquation(centreBis, dpAim);
					 * 
					 * 
					 * // On calcule les lancers de rayon Iterator<IFeature>
					 * itFeat = cf.iterator();
					 * 
					 * double dMin = Double.POSITIVE_INFINITY;
					 * 
					 * for (int k = 0; k < nbPol2; k++) {
					 * 
					 * int ind = featC.getElements().indexOf(itFeat.next());
					 * 
					 * // On calcule le point d'intersection IDirectPosition
					 * dpTemp = RayCasting.intersectionPolygonLine(lE,
					 * (GM_Polygon) featCut.get(ind).getGeom());
					 * 
					 * if (dpTemp == null) {
					 * 
					 * continue; }
					 * 
					 * double dTemp = centre.distance(dpTemp);
					 * 
					 * // On effectue un tri par distance if (dMin > dTemp &&
					 * dTemp <= this.rayon) {
					 * 
					 * nbInter++; break; }// Boucle k
					 * 
					 * }
					 */
					// dpTempOK == null normalement si resultType ==
					// TYPE_POINT_ALL_INTERSECTION
					// Ca éviter d'avoir plusieurs fois le même point dans ce
					// type
					// de
					// résultat

				} // Fin boucle j
			} // Fin boucle i
				// On renvoie les points crées
			IFeature feat = new DefaultFeature(new GM_Point(centreBis));
			AttributeManager.addAttribute(feat, "Rayon", rayon, "Double");
			AttributeManager.addAttribute(feat, "PasA", pasAlpha, "Double");
			AttributeManager.addAttribute(feat, "PasB", pasBeta, "Double");

			double ouverture = 1 - (double) nbInter / (double) nbLancerTotaux;
			// System.out.println(ouverture);
			AttributeManager.addAttribute(feat, "Ouverture", ouverture, "Double");
			featCOut.add(feat);

		} // Fin boucle p

		return featCOut;

	}

	/**
	 * 
	 * @param centre
	 * @param lFeat
	 * @param nbPointsCouronnes
	 * @param rayon
	 * @param resultType
	 * @return
	 */
	public IDirectPositionList castOnSolidPosition(IDirectPosition centre, IFeatureCollection<IFeature> lFeat,
			int nbPointsCouronnes, double rayon, int resultType) {

		// Les points intersectés
		DirectPositionList dplOut = new DirectPositionList();

		// On créer une projection sphérique
		// Elle permettra de trouver les entités potentiellement intersectées
		// par le
		// lancer grâce aux coordonnées angulaires
		SphericalProjection sp = new SphericalProjection(lFeat, centre, rayon, true);
		this.sphericalProjection = sp;

		IFeatureCollection<IFeature> featC = sp.getLFeatMapped();

		IFeatureCollection<IFeature> featCut = sp.getFeatToProject();

		int nbElemT = featC.size();

		for (int i = 0; i < nbElemT; i++) {

			if (!featC.get(i).getGeom().isValid()) {
				featC.remove(i);
				featCut.remove(i);
				i--;
				nbElemT--;

			}

		}

		DirectPositionList aims = new DirectPositionList();

		for (int i = 0; i < nbElemT; i++) {
			GM_Polygon polyTemp = (GM_Polygon) featCut.get(i).getGeom();

			aims.addAll(polyTemp.coord());

		}

		// L'index spatiale permettra de retrouver en coordonnées angulaires
		// les faces concernées par l'intersection
		featC.initSpatialIndex(Tiling.class, false);

		SpatialIndex<IFeature> sI = featC.getSpatialIndex();

		int nbAims = aims.size();

		for (int i = 0; i < nbAims; i++) {
			IDirectPosition dpAim = aims.get(i);

			// On récupère dans le système de coordonnées sphérique
			// les coordonnées du rayon que l'on souhaite lancer
			Orientation or = this.sphericalProjection.calculAngle(dpAim);
			double x = or.getAlpha();
			double y = or.getBeta();

			// Grâce à l'index, on récupère les faces concernées
			Collection<IFeature> cf = sI.select(new DirectPosition(x, y), RayCasting.EPSILON);

			// Cette direction n'a pas de face, on continue

			int nbPol2 = cf.size();

			// Cette direction n'a pas de face, on continue
			if (0 == nbPol2) {

				continue;
			}

			// On créer une équation linéaire
			LineEquation lE = new LineEquation(centre, dpAim);

			IDirectPosition dpTempOk = null;

			// On calcule les lancers de rayon
			Iterator<IFeature> itFeat = cf.iterator();

			double dMin = Double.POSITIVE_INFINITY;

			for (int k = 0; k < nbPol2; k++) {

				int ind = featC.getElements().indexOf(itFeat.next());

				// On calcule le point d'intersection
				IDirectPosition dpTemp = RayCasting.intersectionPolygonLine(lE,
						(GM_Polygon) featCut.get(ind).getGeom());

				if (dpTemp == null) {

					continue;
				}

				double dTemp = centre.distance(dpTemp);

				// On effectue un tri par distance
				if (dMin > dTemp && dTemp <= this.rayon) {

					dpTempOk = dpTemp;
					dMin = dTemp;

				} // Boucle k

			}
			// dpTempOK == null normalement si resultType ==
			// TYPE_POINT_ALL_INTERSECTION
			// Ca éviter d'avoir plusieurs fois le même point dans ce type de
			// résultat
			if (dpTempOk != null) {
				// On garde le plus proche (si il existe)

				dplOut.add(dpTempOk);

			}
		}

		return dplOut;

	}

	private IFeature getOptimized(Iterator<IFeature> itFeat) {

		double dMin = Double.POSITIVE_INFINITY;
		IFeature featOut = null;

		while (true) {

			IFeature feat = itFeat.next();

			GM_Polygon polyTemp = (GM_Polygon) feat.getGeom();

			double distTemp = fr.ign.cogit.geoxygene.sig3d.calculation.Util.centerOf(polyTemp.coord())
					.distance(this.centre);

			if (distTemp < dMin) {

				dMin = distTemp;
				featOut = feat;

			}

			if (!itFeat.hasNext()) {

				break;
			}

		}

		return featOut;

	}

	/**
	 * Renvoie l'intersection entre un polygone et une ligne en 3D.
	 * 
	 * Renvoie null si coplanaire ou si pas d'intersection
	 * 
	 * @param lE
	 * @param p
	 * @return
	 */
	public static IDirectPosition intersectionPolygonLine(LineEquation lE, IPolygon p) {

		// On calcule l'intersection entre le plan et le polygone
		ApproximatedPlanEquation aPE = new ApproximatedPlanEquation(p);
		IDirectPosition dp = lE.intersectionLinePlan(aPE);

		if (dp == null) {
			return dp;
		}

		if (!RayCasting.lieInsidePolygon(dp, p)) {

			return null;
		}

		return dp;

	}

	/**
	 * Indique si un point est dans un polygone
	 * 
	 * @param dp
	 *            le point
	 * @param poly
	 *            le polygone
	 * @param normal
	 *            une normale (nécessaire pour optimiser les calculs)
	 * @return
	 */
	public static boolean lieInsidePolygon(IDirectPosition dp, IPolygon poly, Vecteur normal) {

		boolean isInside = RayCasting.lieInsideRing(dp, poly.getExterior().coord(),
				(new ApproximatedPlanEquation(poly).getNormale()));

		if (!isInside) {
			return false;
		}

		// Dans le polygone mais peut être dans un trou
		List<IRing> lInt = poly.getInterior();
		int nbTrou = lInt.size();

		for (int i = 0; i < nbTrou; i++) {
			isInside = RayCasting.lieInsideRing(dp, lInt.get(i).coord(),
					(new ApproximatedPlanEquation(poly).getNormale()));

			if (isInside) {

				return false;
			}
		}

		return isInside;
	}

	/**
	 * Indique si un point se trouve dans un polygone en 3D
	 * 
	 * @param dp
	 * @param poly
	 * @return
	 */
	public static boolean lieInsidePolygon(IDirectPosition dp, IPolygon poly) {

		return RayCasting.lieInsidePolygon(dp, poly, (new ApproximatedPlanEquation(poly).getNormale()));

	}

	/**
   * Indique en 3D si un point est dans un GM_Ring
   * 
   * @param dp
   * @param dpl
   * @param normal
   * @return
   */
  private static boolean lieInsideRing(IDirectPosition dp,
      IDirectPositionList dpl, Vecteur normal) {

    normal.normalise();
    // Pour tous les points on mesure l'angle entre le centre du point
    // candidat
    // et les sommets du polygone
    // PAs 0 = objet à l'intérieur
    // Porduit mixte pour calculer tout cela
    int nbP = dpl.size();

    if (dpl.get(0).distance(dpl.get(nbP - 1)) > 0.001) {
      dpl.add(dpl.get(0));
    }

    Vecteur vPred, vActu;

    vPred = new Vecteur(dp, dpl.get(0));
    vPred.normalise();

    double angleTotal = 0;

    for (int i = 1; i < nbP; i++) {
      vActu = new Vecteur(dp, dpl.get(i));
      vActu.normalise();

      double cos = vPred.prodScalaire(vActu);
      double sin = vPred.prodVectoriel(vActu).prodScalaire(normal);

      double angle = Math.acos(cos);

      if (sin < 0) {

        angle = -angle;

      }

      angleTotal = angleTotal + angle;

      vPred = vActu;
    }
    
 
  
    
    boolean inside = (Math.abs(angleTotal)  > RayCasting.EPSILON_INSIDE_POLYGON);// && ((Math.abs(angleTotal) - Math.PI*2)> RayCasting.EPSILON)  ;// && (Math.abs(angleTotal) < (2 * Math.PI - RayCasting.EPSILON)) ;
    
    

    if (!inside && RayCasting.CHECK_IS_ON_EDGE) {

      inside = RayCasting.checkOnEdge(dpl, dp);
    }

    return inside;

  }

	private static boolean checkOnEdge(IDirectPositionList dpl, IDirectPosition dp) {

		Proximity p = new Proximity();
		IDirectPosition dpTemp = p.nearest(dp, dpl);

		if (dpTemp.distance(dp) < RayCasting.EPSILON) {
			return true;
		}

		int nbP = dpl.size();

		for (int i = 0; i < nbP - 1; i++) {

			Vecteur v1 = new Vecteur(dp, dpl.get(i));
			Vecteur v2 = new Vecteur(dp, dpl.get(i + 1));

			v1.normalise();
			v2.normalise();
			
			double prodScalaire = v1.prodScalaire(v2); 

			if ((prodScalaire <= -1 + RayCasting.EPSILON)) {

				return true;
			}

		}

		return false;

	}

	/**
	 * Renvoie les rayons ayant une intersection
	 * 
	 * @return
	 */
	public List<IOrientableCurve> generateLineString() {
		int nbPoints = this.dpGenerated.size();

		List<IOrientableCurve> lOut = new ArrayList<IOrientableCurve>(nbPoints);

		for (int i = 0; i < nbPoints; i++) {

			DirectPositionList dpTemp = new DirectPositionList();
			dpTemp.add(this.getCentre());
			dpTemp.add(this.getDpGenerated().get(i));

			lOut.add(new GM_LineString(dpTemp));
		}
		return lOut;

	}

	/**
	 * Ne peut être actuellement utilisé que dans le cas qu'après un résultat du
	 * type : TYPE_FIRST_POINT_AND_SPHERE
	 * 
	 * @return le solide correspondant aux rayons générés
	 */
	private void generateSolid() {

		if (this.lastTypeResult != RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {
			return;
		}

		// Le pole de la demi sphère
		DirectPosition pole = new DirectPosition(this.centre.getX(), this.centre.getY(),
				this.centre.getZ() + this.rayon);

		// les surfaces que l'on souhaite générer
		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

		// L'indice du point que l'on souhaite chercher
		int elemInd = 0;

		// Les points de la ligne précédente
		DirectPositionList dplPred = new DirectPositionList();

		int nbVertiCouronnes = this.nbPointsCouronnes / 4;

		if (this.isSphere) {
			nbVertiCouronnes = 2 * nbVertiCouronnes;
		}

		// On initialise la première colonne
		for (int j = 0; j < nbVertiCouronnes; j++) {
			dplPred.add(this.getDpGenerated().get(j));
			elemInd++;

		}

		// On passe au second point de la première ligne
		elemInd++;

		for (int i = 1; i < this.nbPointsCouronnes; i++) {

			// Liste de points de la couronnes actuelles
			DirectPositionList dplActu = new DirectPositionList();

			// On prend le point précédent
			IDirectPosition dpPred = this.getDpGenerated().get(elemInd - 1);
			IDirectPosition dpSuiv = null;

			for (int j = 1; j < nbVertiCouronnes; j++) {
				// On ajoute le point précédent aux points que l'on souhaite
				// traiter
				dplActu.add(dpPred);
				dpSuiv = this.getDpGenerated().get(elemInd);

				GM_Triangle triPred = new GM_Triangle(dplPred.get(j - 1), dplPred.get(j), dpPred);
				GM_Triangle triAct = new GM_Triangle(dplPred.get(j), dpSuiv, dpPred);

				Box3D bPred = new Box3D(triPred);
				if (bPred.getLLDP().getZ() < this.centre.getZ()) {

					triPred = (GM_Triangle) triPred.reverse();

				}

				Box3D bAct = new Box3D(triAct);

				if (bAct.getLLDP().getZ() < this.centre.getZ()) {

					triAct = (GM_Triangle) triAct.reverse();

				}

				lOS.add(triPred);
				lOS.add(triAct);

				elemInd++;
				dpPred = dpSuiv;
			}

			GM_Triangle triAct = new GM_Triangle(dplPred.get(dplPred.size() - 1), dpSuiv, pole);

			Box3D b = new Box3D(triAct);

			if (b.getLLDP().getZ() < this.centre.getZ()) {

				triAct.reverse();

			}

			lOS.add(triAct);

			// On ajoute le point actuel aux points que l'on souhaite traiter
			// (fin de
			// boucle oblige)
			dplActu.add(dpSuiv);

			elemInd++;

			dplPred = dplActu;

		}

		// On ferme la dernière boucle

		IDirectPosition dpSuiv = null;
		for (int j = 1; j < nbVertiCouronnes; j++) {

			// On récupère le point actuel qui est en fait sur la première ligne
			// traitée
			IDirectPosition dpPred = this.getDpGenerated().get(j - 1);
			dpSuiv = this.getDpGenerated().get(j);

			GM_Triangle triPred = new GM_Triangle(dplPred.get(j - 1), dplPred.get(j), dpPred);
			GM_Triangle triAct = new GM_Triangle(dplPred.get(j), dpSuiv, dpPred);

			Box3D bPred = new Box3D(triPred);
			if (bPred.getLLDP().getZ() < this.centre.getZ()) {

				triPred = (GM_Triangle) triPred.reverse();

			}

			Box3D bAct = new Box3D(triAct);

			if (bAct.getLLDP().getZ() < this.centre.getZ()) {

				triAct = (GM_Triangle) triAct.reverse();

			}

			lOS.add(triPred);
			lOS.add(triAct);
		}

		GM_Triangle triAct = new GM_Triangle(dplPred.get(dplPred.size() - 1), dpSuiv, pole);

		Box3D b = new Box3D(triAct);

		if (b.getLLDP().getZ() < this.centre.getZ()) {

			triAct.reverse();

		}

		lOS.add(triAct);

		this.solGenerated = new GM_Solid(lOS);
	}

	private void generatePolygon() {

		/*
		 * if (this.lastTypeResult != RayCasting.TYPE_CAST_SOLID_POINT) {
		 * return; }
		 */

		if (this.getDpGenerated() == null || this.getDpGenerated().isEmpty()) {
			return;
		}

		int nbPoints = this.getDpGenerated().size();

		DirectPositionList dpl = new DirectPositionList();
		List<Orientation> lOrient = new ArrayList<Orientation>();

		SphericalProjection sp = new SphericalProjection(this.centre);

		bouclei: for (int i = 0; i < nbPoints; i++) {

			IDirectPosition dp = this.getDpGenerated().get(i);
			Orientation or = sp.calculAngle(dp);
			double alpha = or.getAlpha();
			double beta = or.getBeta();

			if (Math.abs(beta) > 0.04) {
				continue;
			}

			int nbElem = lOrient.size();
			int j = 0;
			for (j = 0; j < nbElem; j++) {

				double alphaBis = lOrient.get(j).getAlpha();

				if (Math.abs(alpha - lOrient.get(j).getAlpha()) < RayCasting.EPSILON) {

					IDirectPosition dpCandidate2 = dpl.get(j);

					if (dp.distance(this.centre) < dpCandidate2.distance(this.centre)
							&& Math.abs(beta) < Math.abs(lOrient.get(j).getBeta())) {

						DirectPosition dpToAdd = (DirectPosition) dp.clone();
						dpToAdd.setZ(this.centre.getZ());

						dpl.set(j, dpToAdd);
						lOrient.set(j, or);

						continue bouclei;

					} else {
						continue bouclei;
					}

				}

				if (alphaBis < alpha) {

					DirectPosition dpToAdd = (DirectPosition) dp.clone();
					dpToAdd.setZ(this.centre.getZ());

					dpl.add(j, dpToAdd);
					lOrient.add(j, or);

					continue bouclei;
				}

			}

			DirectPosition dpToAdd = (DirectPosition) dp.clone();
			dpToAdd.setZ(this.centre.getZ());

			dpl.add(dpToAdd);

			lOrient.add(or);

		}

		dpl.add(dpl.get(0));

		this.polGenerated = new GM_Polygon(new GM_LineString(dpl));

	}

	/**
	 * Indique si un point se trouve dans un polygone
	 * 
	 * @param dp
	 * @param r
	 * @return
	 */
	public static boolean lieInsideRing(IDirectPosition dp, IRing r) {
		return RayCasting.lieInsideRing(dp, r.coord(), (new ApproximatedPlanEquation(r.coord())).getNormale());

	}

	/**
	 * @return the centre
	 */
	public IDirectPosition getCentre() {
		return this.centre;
	}

	/**
	 * @return the lFeat
	 */
	public IFeatureCollection<IFeature> getlFeat() {
		return this.lFeat;
	}

	/**
	 * @return the nbPointsCouronnes
	 */
	public int getNbPointsCouronnes() {
		return this.nbPointsCouronnes;
	}

	/**
	 * @return the rayon
	 */
	public double getRayon() {
		return this.rayon;
	}

	/**
	 * @return the dpGenerated
	 */
	public IDirectPositionList getDpGenerated() {
		return this.dpGenerated;
	}

	/**
	 * @return the lastTypeResult
	 */
	public int getLastTypeResult() {
		return this.lastTypeResult;
	}

	public boolean isSphere() {
		return this.isSphere;
	}

	/**
	 * Ne peut être actuellement utilisé que dans le cas qu'après un résultat du
	 * type : TYPE_FIRST_POINT_AND_SPHERE
	 * 
	 * @return le solide correspondant aux rayons générés
	 */
	public GM_Solid getGeneratedSolid() {

		if (this.solGenerated == null) {
			this.generateSolid();

			// On a besoin de refaire un cast avec le bon type
			if (this.solGenerated == null) {
				logger.info("We proceed of a raycasting from type : " + TYPE_FIRST_POINT_AND_SPHERE);
				this.cast(this.getCentre(), this.getlFeat(), this.nbPointsCouronnes, this.rayon,
						RayCasting.TYPE_FIRST_POINT_AND_SPHERE, this.isSphere);

			}
		}
		return this.solGenerated;
	}

	public GM_Polygon getGeneratedPolygon() {
		if (this.polGenerated == null) {
			this.generatePolygon();

			// On a besoin de refaire un cast avec le bon type
			if (this.polGenerated == null) {
				System.out.println("On effectue un lancer de rayon du type TYPE_CAST_SOLID_POINT");

				this.castOnSolidPosition(this.centre, this.lFeat, this.nbPointsCouronnes, this.rayon,
						RayCasting.TYPE_CAST_SOLID_POINT);

				this.generatePolygon();

			}
		}
		return this.polGenerated;

	}

	SphericalProjection sphericalProjection;

	public SphericalProjection getSphericalProjection() {
		return this.sphericalProjection;
	}
	
	public IFeature prepareRayCastingRecords( IFeature currentFeature) {

		IFeature feat = null;
		try {
			feat = currentFeature.cloneGeom();
		} catch (CloneNotSupportedException e) {

			e.printStackTrace();
		}

		IndicatorVisu Iv = new IndicatorVisu(this);

		AttributeManager.addAttribute(feat, "miniRadDis", Iv.getMinimalRadialDistance(), "Double");
		AttributeManager.addAttribute(feat, "maxRadDis", Iv.getMaximalRadialDistance(), "Double");
		AttributeManager.addAttribute(feat, "avgRadDis", Iv.getMoyRadialDistance(), "Double");
		AttributeManager.addAttribute(feat, "varRadDis", Iv.getVarianceRadialDistance(), "Double");
		AttributeManager.addAttribute(feat, "mnRDis2D", Iv.getMaximalRadialDistance2D(), "Double");
		AttributeManager.addAttribute(feat, "avgRDis2D", Iv.getMoyRadialDistance2D(), "Double");
		AttributeManager.addAttribute(feat, "openess", Iv.getOpeness(), "Double");
		AttributeManager.addAttribute(feat, "ratioSph", Iv.getRatioSphere(), "Double");
		AttributeManager.addAttribute(feat, "visSkySurf", Iv.getVisibleSkySurface(), "Double");
		AttributeManager.addAttribute(feat, "visVol", Iv.getVisibleVolume(), "Double");
		AttributeManager.addAttribute(feat, "visVolRa", Iv.getVisibleVolumeRatio(), "Double");
		AttributeManager.addAttribute(feat, "solPeri", Iv.getSolidPerimeter(), "Double");

		return feat;

	}

}
