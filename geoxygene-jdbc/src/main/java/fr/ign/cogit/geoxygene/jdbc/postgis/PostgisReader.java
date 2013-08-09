package fr.ign.cogit.geoxygene.jdbc.postgis;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.index.Tiling;


/**
 * 
 * 
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
            // ShapefileReader.read(reader, schemaDefaultFeature, population);
        
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
            /*String valueType = reader.getFieldClass(i).getSimpleName();
            type.setNomField(nomField);
            type.setMemberName(memberName);
            type.setValueType(valueType);
            newFeatureType.addFeatureAttribute(type);
            attLookup.put(new Integer(i), new String[] { nomField, memberName });
            ShapefileReader.logger.log(Level.FINE, I18N.getString("ShapefileReader.AddingAttribute") + i //$NON-NLS-1$
                    + " = " + nomField); */
        }
        // Création d'un schéma associé au featureType 
        /*newFeatureType.setGeometryType((reader.getShapeType() == null) ? GM_Object.class : reader.getShapeType());
        ShapefileReader.logger.log(Level.FINE, "shapeType = " + reader.getShapeType() //$NON-NLS-1$
                + I18N.getString("ShapefileReader.GeometryType") //$NON-NLS-1$
                + newFeatureType.getGeometryType());
        schemaDefaultFeature.setFeatureType(newFeatureType);
        newFeatureType.setSchema(schemaDefaultFeature);
        schemaDefaultFeature.setAttLookup(attLookup);
        population.setFeatureType(newFeatureType);
        for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
            ShapefileReader.logger.log(Level.FINE, "FeatureAttibute = " //$NON-NLS-1$
                    + fa.getMemberName() + "-" + fa.getValueType()); //$NON-NLS-1$
        }*/
        return reader;
    }

}
