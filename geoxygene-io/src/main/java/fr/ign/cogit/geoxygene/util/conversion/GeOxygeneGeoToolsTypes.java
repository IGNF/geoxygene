package fr.ign.cogit.geoxygene.util.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

public class GeOxygeneGeoToolsTypes {
	private final static Logger logger = Logger.getLogger(GeOxygeneGeoToolsTypes.class.getName());

	public static IFeatureCollection<?> convert2IFeatureCollection(SimpleFeatureCollection collection) {
		logger.info("Schema name = " + collection.getSchema().getName() + " - "
				+ collection.getSchema().getName().getLocalPart());
		SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
		schemaDefaultFeature.setNom(collection.getSchema().getName().getLocalPart());
		schemaDefaultFeature.setNomSchema(collection.getSchema().getName().getLocalPart());
		/** Créer un featuretype de jeu correspondant */
		fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
		newFeatureType.setTypeName(collection.getSchema().getName().getLocalPart());
		int nbFields = collection.getSchema().getAttributeCount();
		Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
		for (int i = 0; i < nbFields; i++) {
			AttributeType type = new AttributeType();
			String nomField = collection.getSchema().getAttributeDescriptors().get(i).getLocalName();
			String memberName = nomField;
			String valueType = collection.getSchema().getAttributeDescriptors().get(i).getType().getBinding()
					.getSimpleName();
			type.setNomField(nomField);
			type.setMemberName(memberName);
			type.setValueType(valueType);
			newFeatureType.addFeatureAttribute(type);
			attLookup.put(new Integer(i), new String[] { nomField, memberName });
			logger.info("Attribute " + i + " added " + nomField + " : " + valueType);
		}
		/** Création d'un schéma associé au featureType */
		newFeatureType
				.setGeometryType(geometryType(collection.getSchema().getGeometryDescriptor().getType().getBinding()));
		logger.info("Schema Created with " + newFeatureType.getGeometryType());
		schemaDefaultFeature.setFeatureType(newFeatureType);
		newFeatureType.setSchema(schemaDefaultFeature);
		schemaDefaultFeature.setAttLookup(attLookup);
		Population<DefaultFeature> population = new Population<DefaultFeature>(schemaDefaultFeature.getNom());
		population.setFeatureType(newFeatureType);
		SimpleFeatureIterator iterator = collection.features();
		int id = 0;
		while (iterator.hasNext()) {
			SimpleFeature feature = (SimpleFeature) iterator.next();
			DefaultFeature defaultFeature = new DefaultFeature();
			defaultFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
			defaultFeature.setSchema(schemaDefaultFeature);
			try {
				Geometry geom = (Geometry) feature.getDefaultGeometry();
				IGeometry geometry = AdapterFactory.toGM_Object(geom);
				defaultFeature.setGeom(geometry);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object[] attributes = new Object[nbFields];
			for (int i = 0; i < nbFields; i++) {
				attributes[i] = feature.getAttribute(i);
			}
			defaultFeature.setAttributes(attributes);
			defaultFeature.setId(id++);
			population.add(defaultFeature);
		}
		logger.info(population.size() + " Features converted");
		return population;
	}

	/**
	 * @param type
	 * @return the class of the given geometry type
	 */
	private static Class<? extends GM_Object> geometryType(Class<?> type) {
		if (type == null) {
			return GM_Object.class;
		}
		if (Point.class.isAssignableFrom(type)) {
			return GM_Point.class;
		}
		if (MultiPoint.class.isAssignableFrom(type)) {
			return GM_MultiPoint.class;
		}
		if (LineString.class.isAssignableFrom(type)) {
			return GM_LineString.class;
		}
		if (Polygon.class.isAssignableFrom(type)) {
			return GM_Polygon.class;
		}
		return GM_MultiSurface.class;
	}

	public static <Feat extends IFeature> SimpleFeatureCollection convert2FeatureCollection(
			IFeatureCollection<Feat> featureCollection) throws Exception {
		return convert2FeatureCollection(featureCollection, DefaultGeographicCRS.WGS84);
	}

	/**
	 * 
	 * @param featureCollection
	 * @param crs
	 * @return
	 * @throws Exception
	 */
	public static <Feat extends IFeature> SimpleFeatureCollection convert2FeatureCollection(
			IFeatureCollection<Feat> featureCollection, CoordinateReferenceSystem crs) throws Exception {

		if (featureCollection == null) {
			logger.warning("FeatureCollection null");
			return null;
		}

		GF_FeatureType featureType = featureCollection.getFeatureType();

		if (featureType == null) {
			if (featureCollection.isEmpty()) {
				logger.warning("FeatureCollection empty and no featureType on collection");
				return null;
			}
			featureType = featureCollection.get(0).getFeatureType();
		}

		logger.info("Typename = " + featureType.getTypeName());

		String typeName = featureType.getTypeName();
		if (typeName == null) {
			typeName = "CreatedType";
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);
		builder.setCRS(crs);
		if (featureType != null) {
			builder.add("geom", AdapterFactory.toJTSGeometryType(featureType.getGeometryType()), crs);
			for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
				Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
				String attributeName = attributeType.getMemberName();
				if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
					attributeName = ((AttributeType) attributeType).getNomField();
				}
				builder.add(attributeName, attributeClass);
			}
		} else {
			if (featureCollection.get(0).getFeatureType() != null) {
				featureType = featureCollection.get(0).getFeatureType();
				builder.add("geom", AdapterFactory.toJTSGeometryType(featureType.getGeometryType()), crs);
				for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
					Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
					String attributeName = attributeType.getMemberName();
					if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
						attributeName = ((AttributeType) attributeType).getNomField();
					}
					builder.add(attributeName, attributeClass);
				}
			}
		}

		SimpleFeatureType type = builder.buildFeatureType();
		
		// SimpleFeatureCollection collection =
		// FeatureCollections.newCollection();
		List<SimpleFeature> list = new ArrayList<SimpleFeature>();
		int i = 1;
		for (Feat feature : featureCollection) {
			List<Object> liste = new ArrayList<Object>(0);
			liste.add(AdapterFactory.toGeometry(new GeometryFactory(), feature.getGeom()));
			if (featureType != null) {
				for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
					liste.add(feature.getAttribute(attributeType.getMemberName()));
				}
			}
			SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
			// collection.add(simpleFeature);
			list.add(simpleFeature);
		}
		SimpleFeatureCollection collection = new ListFeatureCollection(type, list);

		return collection;
	}
}
