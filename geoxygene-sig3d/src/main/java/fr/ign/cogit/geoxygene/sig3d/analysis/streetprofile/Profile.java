package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToLineString;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.util.correction.NormalCorrectionNonTriangulated;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * 
 * Classe principale des fonctions de tracé de séquences urbaine
 * 
 * 
 * Stage de Marina Fund : USAGE D'INDICATEUR 3D ET AMENAGEMENT URBAIN
 * 
 * @TODO : ajouter une petite méthode pour déterminer automatiquement altZmin et
 *       altZmax
 * 
 * @author MFund
 * @author MBrasebin
 * @author JPerret
 * @author YMeneroux
 * 
 */
public class Profile {

	public enum SIDE {
		UPSIDE, DOWNSIDE, BOTH
	}

	private static Logger logger = Logger.getLogger(Profile.class);

	// ---------------------------------- ATTRIBUTS
	// ----------------------------------

	// Paramètres

	/**
	 * Altitude minimale prise en compte par le profile
	 */
	private double altZMin = 38;

	/**
	 * Altitude maximale prise en compte par le profile
	 */
	private double altZMax = 74;

	/**
	 * Pas le long de la voirie
	 */
	private double pas = 1;

	/**
	 * Pas des lancers de rayons en z
	 */
	private double pasZ = 1;

	/**
	 * Profondeur jusqu'à laquelle les bâtiments sont pris en compte
	 */
	private double longCut = 40;

	/**
	 * Paramètre diminuant l'écart, dans le profil, entre les deux côtés de la
	 * voirie. (on conseille altZmin - 2 ou 3)
	 */
	private double diff2DRep = 37;

	private boolean displayInit = false;

	// --------------------------------- ACCESSEURS
	// ----------------------------------

	public double getAltMin() {
		return altZMin;
	}

	public double getAltMax() {
		return altZMax;
	}

	public double getXYStep() {
		return pas;
	}

	public double getZStep() {
		return pasZ;
	}

	public double getCut() {
		return longCut;
	}

	public IFeatureCollection<IFeature> getBuildings() {
		return this.bati;
	}

	public IFeatureCollection<IFeature> getParcels() {
		return this.parcelle;
	}

	public void setYProjectionShifting(double yProjectionShifting) {
		this.diff2DRep = yProjectionShifting;
	}

	// ---------------------------------- MUTATEURS
	// ----------------------------------

	public void setAltMin(double altZMin) {
		this.altZMin = altZMin;
	}

	public void setAltMax(double altZMax) {
		this.altZMax = altZMax;
	}

	public void setXYStep(double pas) {
		this.pas = pas;
	}

	public void setZStep(double pasZ) {
		this.pasZ = pasZ;
	}

	public void setCut(double longCut) {
		this.longCut = longCut;
	}

	public void setBuildings(IFeatureCollection<? extends IFeature> buildings) {
		this.bati = new FT_FeatureCollection<>();
		bati.addAll(buildings);
	}

	public void setParcels(IFeatureCollection<? extends IFeature> parcels) {
		this.parcelle = new FT_FeatureCollection<>();
		this.parcelle.addAll(parcels);
	}

	// Variables internes
	/**
	 * Objects géographiques
	 */
	private IFeatureCollection<IFeature> bati;

	private IFeatureCollection<IFeature> parcelle;

	/**
	 * Rue considérée
	 */
	private IFeatureCollection<IFeature> roadsProfiled;



	/**
	 * Compteur déterminant le nombre de points pris sur la rue considérée
	 */
	private double counterX;

	/**
	 * Collection des vecteurs normaux (pour la visu)
	 */
	private IFeatureCollection<IFeature> featOrthoColl;

	/**
	 * Points projetés de part et d'autres de la rue
	 */
	private IFeatureCollection<IFeature> buildingSide1;

	public IFeatureCollection<IFeature> getBuildingSide1() {
		return buildingSide1;
	}

	public IFeatureCollection<IFeature> getBuildingSide2() {
		return buildingSide2;
	}
	
	public IFeatureCollection<IFeature> getRoadsProfiled() {
		return roadsProfiled;
	}

	public IFeatureCollection<IFeature> getFeatOrthoColl() {
		return featOrthoColl;
	}

	private IFeatureCollection<IFeature> buildingSide2;

	/**
	 * Ensemble des points projetés
	 */
	private IFeatureCollection<IFeature> pproj = null;

	/**
	 * 
	 * @return Résultat du calcul
	 */
	public IFeatureCollection<IFeature> getPproj() {
		return pproj;
	}

	// -------------------------------- CONSTRUCTEURS
	// --------------------------------

	/**
	 * Constructeur avec les paramètres de base (de la demo)
	 * 
	 * @param roadsProfiled
	 *            la route à partir de laquelle le profil est calculé
	 * @param buildings
	 *            batiments pour lesquels le profil est calculé
	 * @param parcels
	 *            parcelles sur lesquelles se trouvent les bâtiments. Lors du
	 *            calcul de profil on ne considère que la parcelle la plus
	 *            proche
	 */
	public Profile(IFeatureCollection<? extends IFeature> roadsProfiled,
			IFeatureCollection<? extends IFeature> buildings, IFeatureCollection<? extends IFeature> parcels) {
		this.roadsProfiled = new FT_FeatureCollection<>();
		this.roadsProfiled.addAll(roadsProfiled);
		this.setBuildings(buildings);
		this.setParcels(parcels);
	}

	/**
	 * Constructeur initialisant le paramètre YProjectionShifting à zMin - 2.
	 * (Espace de 4 m entre les deux parties de la voirie)
	 * 
	 * @param roadsProfiled
	 *            la route à partir de laquelle le profil est calculé
	 * @param buildings
	 *            batiments pour lesquels le profil est calculé
	 * @param parcels
	 *            parcelles sur lesquelles se trouvent les bâtiments. Lors du
	 *            calcul de profil on ne considère que la parcelle la plus
	 *            proche
	 * @param zMin
	 *            altitude minimale pour le calcul du profil
	 * @param zMax
	 *            altitude maximale pour le calcul du profil
	 */
	public Profile(IFeatureCollection<? extends IFeature> roadsProfiled,
			IFeatureCollection<? extends IFeature> buildings, IFeatureCollection<? extends IFeature> parcels,
			double zMin, double zMax) {
		this(roadsProfiled, buildings, parcels);
		this.setAltMin(zMin);
		this.setAltMax(zMax);

		this.setYProjectionShifting(zMin - 2);

	}

	/**
	 * Constructeur initialisant le paramètre YProjectionShifting à zMin - 2.
	 * (Espace de 4 m entre les deux parties de la voirie)
	 * 
	 * @param roadsProfiled
	 *            la route à partir de laquelle le profil est calculé
	 * @param buildings
	 *            batiments pour lesquels le profil est calculé
	 * @param parcels
	 *            parcelles sur lesquelles se trouvent les bâtiments. Lors du
	 *            calcul de profil on ne considère que la parcelle la plus
	 *            proche
	 * @param zMin
	 *            altitude minimale pour le calcul du profil
	 * @param zMax
	 *            altitude maximale pour le calcul du profil
	 * @param stepXY
	 *            pas le long de la route choisie
	 * @param stepZ
	 *            pas en z pour les lancers de rayons
	 */
	public Profile(IFeatureCollection<? extends IFeature> roadsProfiled,
			IFeatureCollection<? extends IFeature> buildings, IFeatureCollection<? extends IFeature> parcels,
			double zMin, double zMax, double stepXY, double stepZ) {
		this(roadsProfiled, buildings, parcels, zMin, zMax);
		this.setXYStep(stepXY);
		this.setZStep(stepZ);
	}

	// ----------------------------------- METHODES de calcul
	// ----------------------------------

	// -------------------------------------------------------------------------------
	// Méthode de chargement des données
	// -------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void loadData(boolean toCorrect) {

		logger.info("-------------------------------------------");
		logger.info("Lancement de la fonction de tracé de profil");
		logger.info("-------------------------------------------");
		logger.info("Pas horizontal : " + pas);
		logger.info("Pas vertical : " + pasZ);
		logger.info("Altitude minimale : " + altZMin);
		logger.info("Altitude maximale : " + altZMax);
		logger.info("Seuil de coupure de rayon : " + longCut);
		logger.info("-------------------------------------------");

		// Chargement des données

		logger.info("-------------------------------------------");
		logger.info("Correction de triangulation du bâti");

		try {

			if (toCorrect) {
				bati = (IFeatureCollection<IFeature>) NormalCorrectionNonTriangulated.correct(bati);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// -------------------------------------------------------------------------------------

		// -------------------------------------------------------------------------------------
		for (IFeature p : parcelle) {

			if (p.getGeom().coordinateDimension() == 2) {

				IGeometry geom = Extrusion2DObject.convertFromGeometry(p.getGeom(), altZMin, altZMin);
				p.setGeom(geom);

			}

		}

	}

	public void loadData() {
		loadData(true);
	}

	/**
	 * Méthode principale de calcul
	 */
	@SuppressWarnings("unchecked")
	public void process() {

		logger.info("-------------------------------------------");
		logger.info("Lancement du calcul");
		logger.info("-------------------------------------------");

		logger.info("Nombre de tronçons de rue : " + roadsProfiled.size());

		IFeatureCollection<IFeature> coll_points = new FT_FeatureCollection<IFeature>();

		// La collection des vecteurs normaux que l'on souhaite afficher

		featOrthoColl = new FT_FeatureCollection<IFeature>();
		buildingSide1 = new FT_FeatureCollection<IFeature>();
		buildingSide2 = new FT_FeatureCollection<IFeature>();
		pproj = new FT_FeatureCollection<IFeature>();

		List<ILineString> routeunique = new ArrayList<ILineString>();

		for (IFeature ft1 : roadsProfiled) {

			routeunique.add((ILineString) FromGeomToLineString.convert(ft1.getGeom()).get(0));

		}

		// Fusion des lignes à l'aide des opérateurs
		ILineString rue = Operateurs.union(routeunique);

		double longueur = rue.length();

		logger.info("Total road length : " + Math.round(longueur * 100) / 100.0);

		// Echantillonnage de la rue
		IGeometry geom = Operateurs.echantillone(rue, pas);
		IDirectPositionList dpl = geom.coord();

		// Contrôle
		if (dpl.size() <= 2) {
			return;
		}

		int nbP = dpl.size();

		int counter = 0;

		this.counterX = nbP * pas;

		// Boucles sur les points échantillonnés
		for (int i = 0; i < nbP; i++) {

			// Mise-à-jour du compteur
			counter = i * 100 / nbP;

			// Le point que l'on traite A
			IDirectPosition dpPred = dpl.get(i);
			coll_points.add(new DefaultFeature(new GM_Point(dpPred)));

			// Le point suivant B
			IDirectPosition dpSuiv;

			// Vecteur AB
			Vecteur vLine;
			if (i == nbP - 1) {

				dpSuiv = dpl.get(i - 1);
				vLine = new Vecteur(dpSuiv, dpPred);

			} else {

				dpSuiv = dpl.get(i + 1);
				vLine = new Vecteur(dpPred, dpSuiv);

			}

			vLine.normalise();

			// Vecteur vertical
			Vecteur vZ = new Vecteur(0, 0, 1);

			// Le vecteur orthogonal à la route
			Vecteur vOrtho = vLine.prodVectoriel(vZ);
			Vecteur vOrtho1 = vOrtho.multConstante(longCut);
			Vecteur vOrtho2 = vOrtho.multConstante(-longCut);

			// Récupération des deux points translatés
			IDirectPosition dpOrtho1 = vOrtho1.translate(dpPred);
			IDirectPosition dpOrtho2 = vOrtho2.translate(dpPred);

			// Génération de la liste des sommets pour créer une ligne

			IDirectPositionList dplLineS = new DirectPositionList();
			IDirectPositionList dplLineS1 = new DirectPositionList();

			dplLineS.add(dpPred);
			dplLineS.add(dpOrtho2);

			dplLineS1.add(dpPred);
			dplLineS1.add(dpOrtho1);

			// Création de la ligne à partir d'une liste de sommets
			ILineString ls = new GM_LineString(dplLineS);
			ILineString ls1 = new GM_LineString(dplLineS1);

			// Création d'une entité pour afficher avec la géométrie de la ligne
			if (displayInit) {
				featOrthoColl.add(new DefaultFeature(ls));
				featOrthoColl.add(new DefaultFeature(ls1));
			}

			double X = i * pas;

			// Petite optimisation : 0 on n'a pas croisé d'intersection 1 on en
			// a
			// croisé 2 on en croise plus, il n'y en a plus
			int stat1 = 0;
			int stat2 = 0;

			IFeatureCollection<IFeature> batiPP = BuildingProfileTools.batimentPProche(parcelle, ls, bati, dpPred);
			IFeatureCollection<IFeature> batiPP1 = BuildingProfileTools.batimentPProche(parcelle, ls1, bati, dpPred);

			if (batiPP == null) {
				stat1 = 2;
			}
			if (batiPP1 == null) {
				stat2 = 2;
			}

			logger.info(counter + " %");

			IDirectPosition pointz;

			double altz;

			for (altz = altZMin; altz < altZMax; altz = altz + pasZ) {

				pointz = new DirectPosition(dpPred.getX(), dpPred.getY(), altz);

				double Y1 = altz - diff2DRep;
				double Y2 = -altz + diff2DRep;

				// trouver le batiment le pp proche

				if (batiPP != null) {

					IFeature pointbati2 = BuildingProfileTools.intersectionPProche(new LineEquation(pointz, vOrtho2),
							batiPP, pointz);

					if (pointbati2 != null) {

						double distance = Double.parseDouble(pointbati2.getAttribute("Distance").toString());

						if (distance <= longCut) {
							buildingSide1.add(pointbati2);

							IDirectPosition ptproj = new DirectPosition();
							ptproj.setCoordinate(X, Y1);
							IFeature ftP = (new DefaultFeature(new GM_Point(ptproj)));

							// Ajout de l'attribut distance)
							Object O = pointbati2.getAttribute("Distance");
							AttributeManager.addAttribute(ftP, BuildingProfileParameters.NAM_ATT_DISTANCE, O, "Double");
							Object Ox = X;
							AttributeManager.addAttribute(ftP, BuildingProfileParameters.NAM_ATT_X, Ox, "Double");
							Object Oy = Y1;
							AttributeManager.addAttribute(ftP, BuildingProfileParameters.NAM_ATT_Y, Oy, "Double");
							Object Oid = pointbati2.getAttribute(BuildingProfileParameters.ID);
							AttributeManager.addAttribute(ftP, BuildingProfileParameters.ID, Oid, "Double");
							pproj.add(ftP);
							stat1 = 1;
						} else {

							if (stat1 == 1) {

								stat1 = 2;

							}
						}

					} else {

						if (stat1 == 1) {

							stat1 = 2;

						}

					}
				}

				if (batiPP1 != null) {

					IFeature pointbati1 = BuildingProfileTools.intersectionPProche(new LineEquation(pointz, vOrtho1),
							batiPP1, pointz);

					if (pointbati1 != null) {

						double distance = Double.parseDouble(pointbati1.getAttribute("Distance").toString());

						if (distance <= longCut) {

							buildingSide2.add(pointbati1);
							IDirectPosition ptproj1 = new DirectPosition();
							ptproj1.setCoordinate(X, Y2);

							IFeature ftP1 = (new DefaultFeature(new GM_Point(ptproj1)));
							Object O1 = pointbati1.getAttribute("Distance");
							AttributeManager.addAttribute(ftP1, BuildingProfileParameters.NAM_ATT_DISTANCE, O1,
									"Double");
							Object Ox1 = X;
							AttributeManager.addAttribute(ftP1, BuildingProfileParameters.NAM_ATT_X, Ox1, "Double");
							Object Oy1 = Y2;
							AttributeManager.addAttribute(ftP1, BuildingProfileParameters.NAM_ATT_Y, Oy1, "Double");
							Object Oid1 = pointbati1.getAttribute(BuildingProfileParameters.ID);
							AttributeManager.addAttribute(ftP1, BuildingProfileParameters.ID, Oid1, "Double");
							pproj.add(ftP1);
							stat1 = 1;

						} else {
							if (stat2 == 1) {

								stat2 = 2;

							}
						}

					} else {

						if (stat2 == 1) {

							stat2 = 2;

						}

					}
				}

				// à rectifier une fois que altzini à été initialisé
			}
		}

		if (!displayInit) {
			logger.info("-------------------------------------------");
			logger.info("Fin de la procédure de calcul");
			return;

		}

	}

	////////////////////////
	//// EXPORT METHODE
	///////////////////////

	/**
	 * Méthode de sauvegarde des points projetés
	 * 
	 * @param output
	 *            le shapefile dans lequel les points sont projetés
	 */
	public void exportPoints(String output) {

		logger.info("-------------------------------------------");
		logger.info("Sauvegarde des résultats de points");

		logger.info("Fichiers brut : " + output);

		ShapefileWriter.write(pproj, output);

	}

	/**
	 * Export sous forme d'un cercle des points
	 * 
	 * @param output
	 *            le shapefile en sortie
	 * @param radius
	 *            le rayon
	 */
	public void exportAsCircle(String output, double radius) {

		logger.info("-------------------------------------------");
		logger.info("Sauvegarde des résultats de polygones");

		logger.info("Fichiers brut : " + output);

		IFeatureCollection<IFeature> featC = TransformToCircle.transform(pproj, counterX,
				1 + (int) ((altZMax - altZMin) / pasZ), radius);

		logger.info("Counter X : " + counterX);

		ShapefileWriter.write(featC, output);

	}

	// Select points on a side
	public IFeatureCollection<IFeature> selectSide(SIDE side) {

		IFeatureCollection<IFeature> iFeatureCollOut = new FT_FeatureCollection<>();

		if (pproj == null) {
			logger.error("Erreur : profile is not generated");
			return iFeatureCollOut;
		}

		for (IFeature feat : this.pproj) {

			double y = Double.parseDouble(feat.getAttribute(BuildingProfileParameters.NAM_ATT_Y).toString());

			if (side.equals(SIDE.BOTH)) {
				iFeatureCollOut.add(feat);
			} else if (side.equals(SIDE.UPSIDE) && y > 0) {
				iFeatureCollOut.add(feat);
			} else if (side.equals(SIDE.DOWNSIDE) && y < 0) {
				iFeatureCollOut.add(feat);
			}

		}

		return iFeatureCollOut;

	}

	public List<Double> getHeightAlongRoad(SIDE side) {

		List<Double> heights = new ArrayList<>();

		if (pproj == null) {
			logger.error("Erreur : profile is not generated");
			return heights;
		}

		for (int i = 0; i < counterX; i++) {

			IFeatureCollection<IFeature> iFeatureCollTemp = this.getPointAtXstep(i * this.getXYStep(), side);

			heights.add(iFeatureCollTemp.size() * this.getZStep());

		}

		return heights;
	}

	// Get the points at the X value
	public IFeatureCollection<IFeature> getPointAtXstep(double xValue, SIDE side) {

		IFeatureCollection<IFeature> iFeatureCollOut = new FT_FeatureCollection<>();

		if (pproj == null) {
			logger.error("Erreur : profile is not generated");
			return iFeatureCollOut;
		}

		for (IFeature feat : this.pproj) {

			double x = Double.parseDouble(feat.getAttribute(BuildingProfileParameters.NAM_ATT_X).toString());

			if (x != xValue) {
				continue;
			}

			double y = Double.parseDouble(feat.getAttribute(BuildingProfileParameters.NAM_ATT_Y).toString());

			if (side.equals(SIDE.BOTH)) {
				iFeatureCollOut.add(feat);
			} else if (side.equals(SIDE.UPSIDE) && y > 0) {
				iFeatureCollOut.add(feat);
			} else if (side.equals(SIDE.DOWNSIDE) && y < 0) {
				iFeatureCollOut.add(feat);
			}

		}

		return iFeatureCollOut;

	}

	///////////////////////////////
	//// Display CODE
	///////////////////////////////

}
