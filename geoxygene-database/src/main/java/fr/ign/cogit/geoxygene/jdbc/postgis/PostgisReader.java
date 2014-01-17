package fr.ign.cogit.geoxygene.jdbc.postgis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;


/**
 * 
 * TODO : implements Runnable ?
 * @author Marie-Dominique Van Damme
 */
public class PostgisReader {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(PostgisReader.class.getName());
    
    
    /**
     * 
     * 
     * @param params
     * @param populationName
     * @param dataset
     * @param initSpatialIndex
     * @return
     */
    public static IPopulation<IFeature> read(Map<String,String> params, String tablename, String populationName, IDataSet dataset,
            boolean initSpatialIndex) throws Exception {
        
        // creation de la collection de features
        Population<IFeature> population = new Population<IFeature>(populationName);
        if (dataset != null) {
            dataset.addPopulation(population);
        }
        
        try {
            
            SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
            schemaDefaultFeature.setNom(populationName);
            schemaDefaultFeature.setNomSchema(populationName);
            
            /** Initialise le schéma */
            PGReader reader = PostgisReader.initSchema(params, tablename, schemaDefaultFeature, population,
                    initSpatialIndex);
            if (reader == null) {
                return null;
            } 
            
            /**
             * Parcours de features du fichier et création de Default features équivalents.
             */
            PostgisReader.read(reader, schemaDefaultFeature, population);
        
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e.toString());
            throw e;
        } 
        
        return population;
    }
    
    /**
     * Initialise le schéma utilisé pour les nouveaux features.
     *
     * @see #read(PGReader, SchemaDefaultFeature, IPopulation)
     * @param shapefileName nom du fichier à lire
     * @param schemaDefaultFeature schéma à initialiser
     * @param population population à peupler avec les features
     * @param initSpatialIndex vrai si on souhaite initialiser l'index spatial
     *            de la population
     */
    public static PGReader initSchema(Map<String,String> params, String tablename, SchemaDefaultFeature schemaDefaultFeature,
            IPopulation<IFeature> population, boolean initSpatialIndex) throws Exception {
        
        PGReader reader = null;
        LOGGER.log(Level.INFO, "INIT SCHEMA");
        
        try {
            reader = new PGReader(params, tablename);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage());
            throw e;
        }
        
        double minX = reader.getMinX();
        double maxX = reader.getMaxX();
        double minY = reader.getMinY();
        double maxY = reader.getMaxY();
       
        if (initSpatialIndex) {
            population.initSpatialIndex(Tiling.class, true, new GM_Envelope(minX, maxX, minY, maxY), 10);
        }
        population.setCenter(new DirectPosition((maxX + minX) / 2, (maxY + minY) / 2));
        LOGGER.log(Level.INFO, "spatial index initialised with " + minX + "," + maxX + "," + minY + "," + maxY);
        
        // Créer un featuretype de jeu correspondant 
        FeatureType newFeatureType = new FeatureType();
        newFeatureType.setTypeName(population.getNom());
        int nbFields = reader.getNbFields();
        Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
        for (int i = 0; i < nbFields; i++) {
            AttributeType type = new AttributeType();
            String nomField = reader.getFieldName(i);
            String memberName = reader.getFieldName(i);
            if (reader.getFieldClass(i) != null) {
              String valueType = reader.getFieldClass(i).getSimpleName();
              type.setValueType(valueType);
            } else {
              type.setValueType("");
            }
            type.setNomField(nomField);
            type.setMemberName(memberName);
            newFeatureType.addFeatureAttribute(type);
            attLookup.put(new Integer(i), new String[] { nomField, memberName });
            LOGGER.log(Level.DEBUG, "AddingAttribute " + i + " = " + nomField);
        }
        
        // Création d'un schéma associé au featureType 
        newFeatureType.setGeometryType((reader.getShapeType() == null) ? GM_Object.class : reader.getShapeType());
        LOGGER.log(Level.TRACE, "shapeType = " + reader.getShapeType() + "GeometryType" + newFeatureType.getGeometryType());
        schemaDefaultFeature.setFeatureType(newFeatureType);
        newFeatureType.setSchema(schemaDefaultFeature);
        schemaDefaultFeature.setAttLookup(attLookup);
        population.setFeatureType(newFeatureType);
        for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
            LOGGER.log(Level.TRACE, "FeatureAttibute = " + fa.getMemberName() + "-" + fa.getValueType());
        }
        
        // 
        return reader;
    }
    
    /**
     * Lit la collection de features GeoTools <code> source </code> et crée des
     * default features correspondant en utilisant le schéma <code> schema
     * </code> et les ajoute à la population <code> population </code>.
     * @param schema schéma des features à créer
     * @param population population à laquelle ajouter les features créés
     * @throws IOException renvoie une exception en cas d'erreur de lecture
     */
    public static void read(PGReader reader, SchemaDefaultFeature schema, IPopulation<IFeature> population) throws IOException {
      
        for (int indexFeature = 0; indexFeature < reader.getNbFeatures(); indexFeature++) {
        
            DefaultFeature defaultFeature = new DefaultFeature();
            defaultFeature.setFeatureType(schema.getFeatureType());
            defaultFeature.setSchema(schema);
            defaultFeature.setAttributes(reader.fieldValues[indexFeature]);
            Class<? extends IGeometry> geometryType = schema.getFeatureType().getGeometryType();
        
            try {
                if (reader.geometries[indexFeature] == null) {
                    LOGGER.log(Level.WARN, "null geometry for object " + indexFeature);
                    LOGGER.log(Level.WARN, "NullGeometryObjectIGnored");
                } else {
                    IGeometry geometry = AdapterFactory.toGM_Object(reader.geometries[indexFeature]);
                    if (!geometryType.isAssignableFrom(geometry.getClass())) {
                        // LOGGER.log(Level.TRACE, "Geometry of type " + geometry.getClass().getSimpleName() + " instead of "
                        //        + geometryType.getSimpleName());
                        // TODO make it more robust: a lot of assumptions here
                        if (geometry instanceof GM_MultiSurface<?>) {
                            geometry = ((GM_MultiSurface<?>) geometry).get(0);
                        } else {
                            if (geometry instanceof GM_MultiCurve<?>) {
                                geometry = ((GM_MultiCurve<?>) geometry).get(0);
                            } else {
                                if (geometry instanceof GM_MultiPoint) {
                                    geometry = ((GM_MultiPoint) geometry).get(0);
                                }
                            }
                        }
                    }
                    defaultFeature.setGeom(geometry);
                    defaultFeature.setId(indexFeature);
                    population.add(defaultFeature);
                }
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "ProblemWhileConvertingGeometry");
            }
        }
        
        LOGGER.log(Level.DEBUG, population.size() + " features created for " + reader.getNbFeatures());
          
    }
    
   

}
