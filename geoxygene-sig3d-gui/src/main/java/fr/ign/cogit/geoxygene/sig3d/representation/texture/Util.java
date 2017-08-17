package fr.ign.cogit.geoxygene.sig3d.representation.texture;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

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
 *          Classe utilitaire pour les objets manipulant des représentation par
 *          textures
 * 
 *          Class to handle objects that use texturation
 */
public class Util {

	private final static Logger logger = Logger.getLogger(Util.class.getName());

	/**
	 * Génère une géométrie Java3D texturée à partir d'une géométrie de
	 * dimension 2 ou +
	 * 
	 * Renvoie null si la dimension n'est pas bonne.
	 * 
	 * Les textures sont plaqués suivant l'axe des X ou l'axe des Y suivant les
	 * dimensions de l'objet
	 * 
	 * @param imageLength
	 *            la longueur que représente l'image dans le monde réel
	 * @param imageHeigth
	 *            la hauteur que représente l'image dans le monde réel
	 * @return une géométrie Java3D avec les informatinos de textures
	 *         renseignées
	 */
	@SuppressWarnings("unchecked")
	public static GeometryInfo geometryWithTexture(IGeometry geom, double imageLength, double imageHeigth) {

		ArrayList<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();

		if (geom instanceof ISolid) {
			ISolid corps = (ISolid) geom;
			lFacettes.addAll(corps.getFacesList());

		} else if (geom instanceof GM_MultiSolid<?>) {

			GM_MultiSolid<? extends ISolid> multiCorps = (GM_MultiSolid<? extends ISolid>) geom;

			List<? extends ISolid> lOS = multiCorps.getList();

			int nbElements = lOS.size();

			for (int i = 0; i < nbElements; i++) {

				List<IOrientableSurface> lSurf = (lOS.get(i)).getFacesList();

				lFacettes.addAll(lSurf);

			}

		} else if (geom instanceof GM_CompositeSolid) {

			GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;

			List<? extends ISolid> lOS = compSolid.getGenerator();

			int nbElements = lOS.size();

			for (int i = 0; i < nbElements; i++) {
				ISolid s = lOS.get(i);
				lFacettes.addAll(s.getFacesList());
			}

		} else if (geom instanceof GM_MultiSurface<?>) {

			lFacettes.addAll(((GM_MultiSurface<?>) geom).getList());

		} else if (geom instanceof GM_OrientableSurface) {
			lFacettes.add((GM_OrientableSurface) geom);

		} else {

			Util.logger.warn(Messages.getString("Representation.GeomUnk") + " : " + geom.getClass());
			return null;
		}

		// géométrie de l'objet
		GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

		// Nombre de facettes
		int nbFacet = lFacettes.size();

		// On compte le nombres de points
		int npoints = 0;

		// On compte le nombre de polygones(trous inclus)
		int nStrip = 0;

		// Initialisation des tailles de tableaux
		for (int i = 0; i < nbFacet; i++) {
			IOrientableSurface os = lFacettes.get(i);

			npoints = npoints + os.coord().size() - 1 - ((IPolygon) lFacettes.get(i)).getInterior().size();
			nStrip = nStrip + 1 + ((GM_Polygon) os).getInterior().size();
		}

		// Nombre de points
		Point3f[] tabpoints = new Point3f[npoints];
		Vector3f[] normals = new Vector3f[npoints];
		TexCoord2f[] texCoord = new TexCoord2f[npoints];

		// Peut servir à detecter les trous
		int[] strip = new int[nStrip];
		int[] contours = new int[nbFacet];

		// compteurs pour remplir le tableau de points
		int elementajoute = 0;

		// Compteur pour remplir les polygones (trous inclus)
		int nbStrip = 0;

		// Pour chaque face
		for (int i = 0; i < nbFacet; i++) {
			GM_Polygon poly = (GM_Polygon) lFacettes.get(i);

			ApproximatedPlanEquation eq = new ApproximatedPlanEquation(poly);
			Vecteur vect = eq.getNormale();
			vect.normalise();
			// DirectPositionList lPoints = facet.coord();

			// Nombre de ring composant le polygone
			int nbContributions = 1 + poly.getInterior().size();

			// Liste de points utilisés pour définir les faces
			IDirectPositionList lPoints = null;

			// Pour chaque contribution (extérieurs puis intérieursr
			// Pour Java3D la première contribution en strip est le contour
			// Les autres sont des trous

			for (int k = 0; k < nbContributions; k++) {

				// Nombre de points de la contribution
				int nbPointsFace = 0;

				if (k == 0) {

					lPoints = poly.getExterior().coord();

				} else {

					// Contribution de type trou

					lPoints = poly.getInterior(k - 1).coord();

				}

				IDirectPosition dpMin = Util.pointMin(lPoints);

				// Nombres de points de la contribution
				int n = lPoints.size();

				Vecteur axe = vect.prodVectoriel(new Vecteur(0, 0, 1));

				if (axe.norme() < 0.1) {

					axe = vect.prodVectoriel(new Vecteur(0, 1, 0));

					if (axe.norme() < 0.1) {

						axe = vect.prodVectoriel(new Vecteur(1, 0, 0));

					}

				}

				axe.normalise();

				Vecteur vectProject = axe.prodVectoriel(vect);

				for (int j = 0; j < n; j++) {
					// On complète le tableau de points
					IDirectPosition dp = lPoints.get(j);
					Point3f point = new Point3f((float) dp.getX(), (float) dp.getY(), (float) dp.getZ());

					tabpoints[elementajoute] = point;

					Vecteur vectTemp = new Vecteur(dp, dpMin);

					texCoord[elementajoute] = new TexCoord2f((float) ((vectTemp.prodScalaire(axe)) / imageLength),

							(float) (-(vectTemp.prodScalaire(vectProject)) / imageHeigth));

					normals[elementajoute] = new Vector3f((float) vect.getX(), (float) vect.getY(),
							(float) vect.getZ());
					// Un point en plus dans la liste de tous les points
					elementajoute++;

					// Un point en plus pour la contribution en cours
					nbPointsFace++;
				}

				// On indique le nombre de points relatif à la
				// contribution
				strip[nbStrip] = nbPointsFace;
				nbStrip++;
			}

			// Pour avoir des corps séparés, sinon il peut y avoir des trous
			contours[i] = nbContributions;

		}

		// On indique quels sont les points combien il y a de contours et de
		// polygons

		geometryInfo.setTextureCoordinateParams(1, 2);

		geometryInfo.setCoordinates(tabpoints);
		geometryInfo.setStripCounts(strip);
		geometryInfo.setContourCounts(contours);
		geometryInfo.setNormals(normals);

		geometryInfo.setTextureCoordinates(0, texCoord);

		return geometryInfo;

	}

	/**
	 * Indique le point inférieur d'une liste de points
	 * 
	 * @param dpl
	 * @return
	 */
	private static IDirectPosition pointMin(IDirectPositionList dpl) {

		int nbElem = dpl.size();

		double x = Double.POSITIVE_INFINITY;
		double y = Double.POSITIVE_INFINITY;
		double z = Double.POSITIVE_INFINITY;

		for (int i = 0; i < nbElem; i++) {

			IDirectPosition dpTemp = dpl.get(i);

			if (x > dpTemp.getX()) {

				x = dpTemp.getX();
			}

			if (y > dpTemp.getY()) {

				y = dpTemp.getY();
			}

			if (z > dpTemp.getZ()) {
				z = dpTemp.getZ();

			}

		}

		return new DirectPosition(x, y, z);
	}

}
