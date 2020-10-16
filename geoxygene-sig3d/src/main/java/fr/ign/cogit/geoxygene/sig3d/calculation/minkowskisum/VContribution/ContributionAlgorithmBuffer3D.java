package fr.ign.cogit.geoxygene.sig3d.calculation.minkowskisum.VContribution;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
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
 *          Classe permettant de calculer un Buffer3D visuel par le calculs de
 *          contributions Cet algorithme est décrit dans Bakri[2009]
 *          L'algorithme permet l'affichage dans une carte 3D Ne fonctionne
 *          qu'avec des solides mathématiquement justes TODO : Etendre la
 *          fonction pour des objets ponctuels, linéaires et surfaciques TODO :
 *          Nettoyer la géométrie finale Barki H., Denis F, Dupont Contributing
 *          F., Vertices-based Minkowski sum of a non-convex polyhedron without
 *          fold and a convex polyhedron. . Dans IEEE International Conference
 *          on Shape Modeling and Applications (SMI), IEEE Computer Society
 *          Press ed. Beijing, China. 2009 A visual 3D buffer algorithm for
 *          solids
 * 
 */
public class ContributionAlgorithmBuffer3D {

	private final static Logger logger = LogManager.getLogger(ContributionAlgorithmBuffer3D.class.getName());

	/**
	 * On exécute le buffer 3D sur l'entité feat Val traduit la taille du buffer
	 * Detail le niveau de détail de la sphère qui sera utilisée Carte maintient
	 * le lien pour l'affichage
	 * 
	 * @param feat
	 *            l'entité qui se verra appliqué un buffer
	 * @param val
	 *            la largeur du buffer
	 * @param detail
	 *            le détail des sphères permettant le calcul
	 * @param map3D
	 *            la carte qui sera utilisée pour récupérer le réulstat
	 */
	public static IFeatureCollection<IFeature> offsetting(IFeature feat, double val, int detail) {

		long t = System.currentTimeMillis();

		// On lance l'algorithme
		MinkowskiSum sum = new MinkowskiSum(feat, new DirectPosition(0, 0, 0), val, detail);

		ContributionAlgorithmBuffer3D.logger.info("Time :" + ((System.currentTimeMillis() - t)));

		// On procède aux affichages, ajoute Relation calcule àgalement la
		// position des relations
		ContributionAlgorithmBuffer3D.addRelations(sum.lRelationFaceVertex,
				ContributionAlgorithmBuffer3D.C_FACE_VERTEX);
		ContributionAlgorithmBuffer3D.addLTriangles(sum.lRelationEdgeFace, ContributionAlgorithmBuffer3D.C_EDGE_FACE);
		ContributionAlgorithmBuffer3D.addLTriangles(sum.lRelationVertexFace,
				ContributionAlgorithmBuffer3D.C_VERTEX_FACE);
		
		
	    IFeatureCollection<IFeature> ftColl = new FT_FeatureCollection<IFeature>();
	    IMultiSurface<IOrientableSurface> ims = new GM_MultiSurface<>();
	    ims.addAll(sum.lRelationEdgeFace);
	    ims.addAll(sum.lRelationVertexFace);
	    ftColl.add(new DefaultFeature(ims));
	    
	    return ftColl;

	}

	public static final String C_FACE_VERTEX = "Contribution surfaces";
	public static final String C_EDGE_FACE = "Contributions arrètes";
	public static final String C_VERTEX_FACE = "Contribution angles";

	/**
	 * Fonction permettant d'afficher une liste de triangles dans une carte dans
	 * une couche de nom nom Ecrase la couche de même nom existante
	 * 
	 * @param lTriangles
	 * @param name
	 * @param map3D
	 * @return
	 */
	private static List<IOrientableSurface> addLTriangles(List<Triangle> lTriangles, String name) {

		int nbElements = lTriangles.size();

		ArrayList<IOrientableSurface> lOS2 = new ArrayList<IOrientableSurface>(nbElements);

		// On convertit les triangles en géométrie ISO
		for (int i = 0; i < nbElements; i++) {
			IOrientableSurface os = lTriangles.get(i).toGeoxygeneSurface();
			lOS2.add(os);
		}

		GM_Solid sol = new GM_Solid(lOS2);
		IFeature feat = new DefaultFeature(sol);

		FT_FeatureCollection<IFeature> ftcoll = new FT_FeatureCollection<IFeature>();
		ftcoll.add(feat);

		return lOS2;

	}

	/**
	 * Permet d'ajouter une relation FV à la carte Translate le triangle de
	 * cette relation suivant le point
	 * 
	 * @param rFV1
	 * @param name
	 * @param map3D
	 * @return
	 */

	private static List<GM_OrientableSurface> addRelations(List<RelationFV> rFV1, String name) {

		int nbElem1 = rFV1.size();

		List<GM_OrientableSurface> lOS = new ArrayList<GM_OrientableSurface>(nbElem1);

		FT_FeatureCollection<IFeature> ftColl1 = new FT_FeatureCollection<IFeature>();

		// Pour chaque objet on effectue la translation
		// Attention on ne considère que la relation est 1-1 (cas de la sphère)
		for (int i = 0; i < nbElem1; i++) {

			RelationFV rel = rFV1.get(i);

			Triangle tTemp = rel.getTriangle();

			int nbsomest = rel.getLVertex().size();

			for (int j = 0; j < nbsomest; j++) {

				ftColl1.add(
						new DefaultFeature((tTemp.translateTriangle(rel.getLVertex().get(j))).toGeoxygeneSurface()));

			}
		}

		return lOS;
	}

}
