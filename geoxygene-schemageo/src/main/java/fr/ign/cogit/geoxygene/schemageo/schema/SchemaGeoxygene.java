/**
 * @author julien Gaffuri 8 juil. 2009
 */
package fr.ign.cogit.geoxygene.schemageo.schema;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

/**
 * @author julien Gaffuri 8 juil. 2009
 * 
 */
public class SchemaGeoxygene {

  private static SchemaConceptuelJeu schGeox = null;

  public static SchemaConceptuelJeu getSchemaGeoxygene() {
    if (SchemaGeoxygene.schGeox == null) {
      SchemaGeoxygene.schGeox = new SchemaConceptuelJeu();
      SchemaGeoxygene.schGeox.setDefinition("Schema generique de GeOxygene");
      SchemaGeoxygene.schGeox.setNomSchema("Schema GeOxygene");

      SchemaGeoxygene.schGeox.createFeatureType("TronconDeRoute");
      FeatureType tronconDeRoute = (SchemaGeoxygene.schGeox
          .getFeatureTypeByName("TronconDeRoute"));
      tronconDeRoute
          .setDefinition("Portion de voie de communication destinée aux automobiles, homogène pour l'ensemble des attributs et des relations qui la concernent. Représente uniquement la chaussée, délimitée par les bas-côtés ou les trottoirs.");
      tronconDeRoute.setIsAbstract(false);

      // Attribut Nature
      SchemaGeoxygene.schGeox.createFeatureAttribute(tronconDeRoute, "Nature",
          "string", true);
      AttributeType nature1 = tronconDeRoute
          .getFeatureAttributeByName("Nature");
      nature1
          .setDefinition("Hiérarchiqation du réseau routier basée sur l'importance des tronçons de route pour le trafic routier.");
      SchemaGeoxygene.schGeox.createFeatureAttributeValue(nature1,
          "Autoroutière");
      SchemaGeoxygene.schGeox
          .createFeatureAttributeValue(nature1, "Principale");
      SchemaGeoxygene.schGeox.createFeatureAttributeValue(nature1, "Régionale");

    }
    return SchemaGeoxygene.schGeox;
  }

  /*
   * public static void main(String[] args) { creeSchemaPostGIS(); }
   * 
   * public static void creeSchemaPostGIS() { DataSet.db = new
   * GeodatabaseOjbPostgis(); DataSet.db.begin();
   * 
   * //creation d’un dataset DataSet dataset1 = new DataSet();
   * dataset1.setAppartientA(null); dataset1.setDate("07/2009");
   * dataset1.setId(1); dataset1.setNom("Jeu de données test ");
   * dataset1.setPersistant(true); dataset1.setProduit(new Produit());
   * dataset1.setTypeBD("BD GeOxygene"); dataset1.setModele("Structuré");
   * 
   * dataset1.setSchemaConceptuel(getSchemaGeoxygene());
   * getSchemaGeoxygene().setDataset(dataset1);
   * 
   * DataSet.db.makePersistent(getSchemaGeoxygene());
   * DataSet.db.makePersistent(dataset1); DataSet.db.commit(); }
   */

}
