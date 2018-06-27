package fr.ign.cogit.geoxygene.sig3d.util.selection;

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 * Class that allows to select 3D feature from a 2D Polygon
 * 
 * @author mbrasebin
 *
 */
public class SpatialFilter3D {

	private static Logger logger = Logger.getLogger(SpatialFilter3D.class);

	/**
	 * Select the feature from fileIn that are included in 2D into the first
	 * feature of cutFile
	 * 
	 * @param fileIn
	 *            the file with features to select
	 * @param fichierDecoupeur
	 *            the file with 1 surfacic feature that is used to select the
	 *            features
	 * @param output
	 *            the output shapefile
	 */
	public static void selectIncluded(String fileIn, String cutFile, String output) {

		IFeatureCollection<IFeature> featIn = ShapefileReader.read(fileIn);

		IFeatureCollection<IFeature> featCut = ShapefileReader.read(cutFile);

		IFeatureCollection<IFeature> featureOut = selectIncluded(featIn, featCut);

		if (featureOut == null) {
			logger.info("Error: input shapefile is empty or null");
		}

		ShapefileWriter.write(featureOut, output);
	}

	/**
	 * Select the feature from featIn that are included in 2D into the first
	 * feature of featCut
	 * 
	 * @param featIn
	 *            the collection with features to select
	 * @param featCut
	 *            the collection with at least 1 surfacic entity used to select
	 *            the faetures
	 * @return a collection with selected features from featIn
	 */
	public static IFeatureCollection<IFeature> selectIncluded(IFeatureCollection<IFeature> featIn,
			IFeatureCollection<IFeature> featCut) {
	

		if (featIn == null || featIn.isEmpty()) {
			return null;
		}

		if (featCut == null || featCut.isEmpty()) {
			return null;
		}

		IFeatureCollection<IFeature> featureOut = new FT_FeatureCollection<IFeature>();

		bouclei: for (IFeature feat : featIn) {

			IGeometry geom = feat.getGeom();

			IDirectPositionList dpl = geom.coord();

			Iterator<IFeature> itF = featCut.select(dpl.get(0), 0.1).iterator();

			int nbP = dpl.size();

			bwhile: while (itF.hasNext()) {

				IFeature featTemp = itF.next();
				IGeometry geomTemp = featTemp.getGeom();
				for (int j = 1; j < nbP; j++) {

					IPoint p = new GM_Point(dpl.get(j));

					if (geomTemp.contains(p)) {
						featureOut.add(feat);
						continue bwhile;
					}

				}

				continue bouclei;

			}

		}

		logger.info("Nombre entités en sortie : " + featureOut.size());

		return featureOut;
	}

	/**
	 * Select the features from fileIn thats intersect in 2D into the first
	 * feature of cutFile
	 * 
	 * @param fileIn
	 *            the file with features to select
	 * @param cutFile
	 *            the file with 1 surfacic feature that is used to select the
	 *            features
	 * @param output
	 *            the output shapefile
	 */
	public static void selectIntersected(String fileIn, String cutFile, String output) {


		IFeatureCollection<IFeature> featIn = ShapefileReader.read(fileIn);

		IFeatureCollection<IFeature> featCut = ShapefileReader.read(cutFile);

		
		IFeatureCollection<IFeature> featureOut = selectIntersected(featIn,featCut );

		ShapefileWriter.write(featureOut, output);
	}
	
	

	/**
	 * Select the feature from featIn that are intersected in 2D into the first
	 * feature of featCut
	 * 
	 * @param featIn
	 *            the collection with features to select
	 * @param featCut
	 *            the collection with at least 1 surfacic entity used to select
	 *            the faetures
	 * @return a collection with selected features from featIn
	 */
	public static IFeatureCollection<IFeature> selectIntersected(IFeatureCollection<IFeature> featIn,
	IFeatureCollection<IFeature> featCut) {

		IFeatureCollection<IFeature> featureOut = new FT_FeatureCollection<IFeature>();
		
		

		int nbElem1 = featIn.size();

		bouclei: for (int i = 0; i < nbElem1; i++) {
			IFeature feat1 = featIn.get(i);

			IGeometry geom = feat1.getGeom();

			IDirectPositionList dpl = geom.coord();

			Iterator<IFeature> itF = featCut.select(dpl.get(0), 0.1).iterator();

			int nbP = dpl.size();

			bwhile: while (itF.hasNext()) {

				IFeature featTemp = itF.next();
				IGeometry geomTemp = featTemp.getGeom();
				for (int j = 1; j < nbP; j++) {

					IPoint p = new GM_Point(dpl.get(j));

					if (!geomTemp.contains(p)) {
						continue bwhile;
					}

				}

				// Tous les points sont dans le polygone, on ne l'ajoute pas
				featureOut.add(feat1);
				continue bouclei;

			}

		}
		
		return featureOut;
	}

}
