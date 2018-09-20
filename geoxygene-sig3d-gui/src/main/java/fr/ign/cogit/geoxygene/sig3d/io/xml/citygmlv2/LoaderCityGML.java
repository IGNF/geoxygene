package fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityModel;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.CG_VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * @version 1.7
 * 
 *          Class to load CityGML data in full schema (schema package) with it
 *          representation (representation.citygml package)
 */
public class LoaderCityGML {

	public static boolean CLEAN_GEOX_GEOM = false;

	public static VectorLayer read(File f, String context, String layerName, boolean generateRepresentation)
			throws CityGMLReadException, JAXBException {

		Context.CITY_GML_CONTEXT = context;

		CityGMLReader reader = LoaderCityGML.getCityGMLInputFactory().createCityGMLReader(f);
		CityGML citygml = reader.nextFeature();

		CG_CityModel cityModel = new CG_CityModel((CityModel) citygml);

		if (CLEAN_GEOX_GEOM) {
			return cleanGeomGeox(cityModel);
		}

		int nbElem = cityModel.size();

		if(generateRepresentation){
			for (CG_CityObject cGO : cityModel.getElements()) {

				if (cGO != null) {
					RP_CityObject.generateCityObjectRepresentation(cGO, cityModel.getlCGA());
				}

			}
			
			for (int i = 0; i < nbElem; i++) {

				IFeature feat = cityModel.get(i);

				if (feat.getRepresentation() == null) {

					cityModel.remove(i);
					i--;
					nbElem--;

				}

			}
		}
	



		return new CG_VectorLayer(cityModel, layerName);

	}

	private static VectorLayer cleanGeomGeox(CG_CityModel cityModel) {
		int nbElem = cityModel.size();

		IFeatureCollection<IFeature> featC = new FT_FeatureCollection<>();

		IPoint p = new GM_Point(cityModel.getCenter());

		for (int i = 0; i < nbElem; i++) {

			CG_CityObject cGO = cityModel.get(0);
			cityModel.remove(0);
			if (cGO == null) {
				continue;
			}
			RP_CityObject.generateCityObjectRepresentation(cGO, cityModel.getlCGA());

			if (cGO.getRepresentation() == null) {
				continue;
			}

			IFeature feat = new DefaultFeature(p);
			feat.setRepresentation(cGO.getRepresentation());

			cGO.setRepresentation(null);
			featC.add(feat);
		}

		return new VectorLayer(featC, "NoGeom");

	}

	private static CityGMLInputFactory in = null;

	private static CityGMLInputFactory getCityGMLInputFactory() throws JAXBException, CityGMLReadException {

		if (LoaderCityGML.in == null) {

			CityGMLContext ctx = new CityGMLContext();
			JAXBBuilder builder = ctx.createJAXBBuilder();

			LoaderCityGML.in = builder.createCityGMLInputFactory();

		}
		return LoaderCityGML.in;

	}
}
