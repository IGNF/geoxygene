package fr.ign.cogit.geoxygene.shemageo.schema.exnathalie;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.schema.ConceptualSchema;
import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

public class Util {

  /**
   * Méthode statique pour charger un schéma dont on connaît le nom.
   * 
   * @param nomSchema
   * @return le schema qui s'appelle nomSchema
   */
  public static ConceptualSchema<?, ?, ?, ?, ?, ?> loadSchema(String nomSchema) {
      ConceptualSchema<?, ?, ?, ?, ?, ?> schema = null;
    ContexteSchemasSingleton c = ContexteSchemasSingleton
        .getContexteSchemaSingleton();
    DataSet.db = new GeodatabaseOjbPostgis();
    DataSet.db.begin();
    List<SchemaConceptuelJeu> list = DataSet.db
        .loadAll(SchemaConceptuelJeu.class);
    for (ConceptualSchema<?, ?, ?, ?, ?, ?> schemaConceptuel : list) {
      if (schemaConceptuel.getNomSchema().equalsIgnoreCase(nomSchema)) {
        schema = schemaConceptuel;
        schema.initNM();
        c.addSchemaDisponible(schema);
        return schema;
      } else {
        continue;
      }
    }
    return schema;
  }

  /**
   * Méthode qui affiche les classes d'un schéma
   */
  public static void afficheClasses(ConceptualSchema<?, ?, ?, ?, ?, ?> s) {
    List<? extends GF_FeatureType> lesFT = s.getFeatureTypes();
    System.out.println("Les classes du schéma " + s.getNomSchema());
    for (GF_FeatureType featureType : lesFT) {
      System.out.println(featureType.getTypeName());
    }
  }

  /**
   * Méthode statique pour supprimer un schéma dont on connaît le nom. A
   * vérifier avant de l'utiliser...
   * @param nomSchema
   */
  public static void deleteSchema(String nomSchema) {
      ConceptualSchema<?, ?, ?, ?, ?, ?> schema = null;
    ContexteSchemasSingleton c = ContexteSchemasSingleton
        .getContexteSchemaSingleton();
    DataSet.db = new GeodatabaseOjbPostgis();
    DataSet.db.begin();
    List<SchemaConceptuelJeu> list = DataSet.db
        .loadAll(SchemaConceptuelJeu.class);
    for (ConceptualSchema<?, ?, ?, ?, ?, ?> schemaConceptuel : list) {
      if (schemaConceptuel.getNomSchema().equalsIgnoreCase(nomSchema)) {
        schema = schemaConceptuel;
        schema.initNM();
        c.addSchemaDisponible(schema);

      } else {
        continue;
      }
    }
    if (schema != null) {
      DataSet.db.deletePersistent(schema);
      c.removeSchemaDisponible(schema);
      DataSet.db.commit();
    }
  }

}
