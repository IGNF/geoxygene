package fr.ign.cogit.geoxygene.util.conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
	public final static Logger logger = Logger.getLogger(GeOxygeneGeoToolsTypes.class.getName());

	/**
	 * convert a GeoTools SimpleFeature to a iFeature (largely copied/pasted from the code below)
	 * 
	 * @param the
	 *            SimpleFeature
	 * @return a IFeature
	 * @author maxime colomb
	 * @throws Exception
	 */
	public static IFeature convert2IFeature(SimpleFeature feature) throws Exception {
		SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
		schemaDefaultFeature.setNom(feature.getFeatureType().getName().getLocalPart());
		schemaDefaultFeature.setNomSchema(feature.getFeatureType().getName().getLocalPart());
		/** Créer un featuretype de jeu correspondant */
		fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
		newFeatureType.setTypeName(feature.getFeatureType().getName().getLocalPart());
		int nbFields = feature.getFeatureType().getAttributeCount();
		Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
		for (int i = 0; i < nbFields; i++) {
			AttributeType type = new AttributeType();
			String nomField = feature.getFeatureType().getAttributeDescriptors().get(i).getLocalName();
			String memberName = nomField;
			String valueType = feature.getFeatureType().getAttributeDescriptors().get(i).getType().getBinding().getSimpleName();
			type.setNomField(nomField);
			type.setMemberName(memberName);
			type.setValueType(valueType);
			newFeatureType.addFeatureAttribute(type);
			attLookup.put(new Integer(i), new String[] { nomField, memberName });
		}
		/** Création d'un schéma associé au featureType */
		newFeatureType.setGeometryType(geometryType(feature.getFeatureType().getGeometryDescriptor().getType().getBinding()));
		schemaDefaultFeature.setFeatureType(newFeatureType);
		newFeatureType.setSchema(schemaDefaultFeature);
		schemaDefaultFeature.setAttLookup(attLookup);
		int id = Integer.valueOf(0);

		DefaultFeature defaultFeature = new DefaultFeature();
		defaultFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
		defaultFeature.setSchema(schemaDefaultFeature);

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		IGeometry geometry = AdapterFactory.toGM_Object(geom);
		defaultFeature.setGeom(geometry);

		Object[] attributes = new Object[nbFields];
		for (int i = 0; i < nbFields; i++) {
			attributes[i] = feature.getAttribute(i);
		}
		defaultFeature.setAttributes(attributes);
		defaultFeature.setId(id);

		return defaultFeature;
	}

	public static IFeatureCollection<?> convert2IFeatureCollection(SimpleFeatureCollection collection) {
		// logger.info("Schema name = " + collection.getSchema().getName() + " - " + collection.getSchema().getName().getLocalPart());
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
			if (nomField.equals("the_geom") || nomField.equals("geom")) {
				continue;
			}
			String memberName = nomField;
			String valueType = collection.getSchema().getAttributeDescriptors().get(i).getType().getBinding().getSimpleName();
			type.setNomField(nomField);
			type.setMemberName(memberName);
			type.setValueType(valueType);
			newFeatureType.addFeatureAttribute(type);
			attLookup.put(new Integer(i), new String[] { nomField, memberName });
			// logger.info("Attribute " + i + " added " + nomField + " : " + valueType);
		}
		/** Création d'un schéma associé au featureType */
		newFeatureType.setGeometryType(geometryType(collection.getSchema().getGeometryDescriptor().getType().getBinding()));
		// logger.info("Schema Created with " + newFeatureType.getGeometryType());
		schemaDefaultFeature.setFeatureType(newFeatureType);
		newFeatureType.setSchema(schemaDefaultFeature);
		schemaDefaultFeature.setAttLookup(attLookup);
		Population<DefaultFeature> population = new Population<DefaultFeature>(schemaDefaultFeature.getNom());
		population.setFeatureType(newFeatureType);
		SimpleFeatureIterator iterator = collection.features();
		int id = 0;
		try {
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
			// logger.info(population.size() + " Features converted");
		} catch (Exception problem) {
			problem.printStackTrace();
		} finally {
			iterator.close();
		}
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

	public static <Feat extends IFeature> SimpleFeatureCollection convert2FeatureCollection(IFeatureCollection<Feat> featureCollection) throws Exception {
		return convert2FeatureCollection(featureCollection, DefaultGeographicCRS.WGS84);
	}

	/**
	 * doesnt work
	 * 
	 * @param featureCollection
	 * @param crs
	 * @return
	 * @throws Exception
	 */
	public static <Feat extends IFeature> SimpleFeatureCollection convert2FeatureCollection(IFeatureCollection<Feat> featureCollection, CoordinateReferenceSystem crs)
			throws Exception {

		if (featureCollection == null) {
			// logger.warning("FeatureCollection null");
			return null;
		}

		GF_FeatureType featureType = featureCollection.getFeatureType();

		if (featureType == null) {
			if (featureCollection.isEmpty()) {
				// logger.warning("FeatureCollection empty and no featureType on collection");
				return null;
			}
			featureType = featureCollection.get(0).getFeatureType();
		}

		// logger.info("Typename = " + featureType.getTypeName());

		String typeName = featureType.getTypeName();
		if (typeName == null) {
			typeName = "CreatedType";
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);
		builder.setCRS(crs);

		if (featureType != null) {
			builder.add("the_geom", AdapterFactory.toJTSGeometryType(featureType.getGeometryType()), crs);
			builder.setDefaultGeometry("the_geom");
			for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
				Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
				String attributeName = attributeType.getMemberName();
				if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
					attributeName = ((AttributeType) attributeType).getNomField();
				}
				builder.add(attributeName, attributeClass);
			}
			// } else {
			// if (featureCollection.get(0).getFeatureType() != null) {
			// featureType = featureCollection.get(0).getFeatureType();
			// builder.add("geom", AdapterFactory.toJTSGeometryType(featureType.getGeometryType()), crs);
			// for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
			// Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
			// String attributeName = attributeType.getMemberName();
			// if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
			// attributeName = ((AttributeType) attributeType).getNomField();
			// }
			// builder.add(attributeName, attributeClass);
			// }
			// }
		}

		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(builder.buildFeatureType());
		DefaultFeatureCollection collection = new DefaultFeatureCollection();

		int i = 1;
		for (Feat feature : featureCollection) {
			sfBuilder.add(AdapterFactory.toGeometry(new GeometryFactory(), feature.getGeom()));

			if (featureType != null) {
				for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
					sfBuilder.add(feature.getAttribute(attributeType.getMemberName()));
				}
			}

			SimpleFeature simpleFeature = sfBuilder.buildFeature(String.valueOf(i++));
			collection.add(simpleFeature);
		}

		return collection.collection();
	}

	public static <Feat extends IFeature> SimpleFeature convert2SimpleFeature(IFeature feature, CoordinateReferenceSystem crs) throws Exception {

		GF_FeatureType featureType = feature.getFeatureType();

		// logger.info("Typename = " + featureType.getTypeName());

		String typeName = featureType.getTypeName();
		if (typeName == null) {
			typeName = "CreatedType";
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName(typeName);
		builder.setCRS(crs);

		if (featureType != null) {
			builder.add("the_geom", AdapterFactory.toJTSGeometryType(featureType.getGeometryType()), crs);
			builder.setDefaultGeometry("the_geom");
			for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
				Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
				String attributeName = attributeType.getMemberName();
				if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
					attributeName = ((AttributeType) attributeType).getNomField();
				}
				builder.add(attributeName, attributeClass);
			}
		}

		SimpleFeatureBuilder sfBuilder = new SimpleFeatureBuilder(builder.buildFeatureType());

		sfBuilder.add(AdapterFactory.toGeometry(new GeometryFactory(), feature.getGeom()));

		if (featureType != null) {
			for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
				sfBuilder.add(feature.getAttribute(attributeType.getMemberName()));
			}
		}

		SimpleFeature simpleFeature = sfBuilder.buildFeature(null);

		return simpleFeature;
	}
}
