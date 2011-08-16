/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.schema.util.persistance;

import java.io.File;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

/**
 * Pour tester la persistence des MdDataset, Produit, SchemaConceptuelJeu et
 * schémaConceptuelProduit dans Oracle ou Postgis.
 * @author Sandrine Balley
 */

public class Test {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // generationSchema();
    // ecritureSchemaProduit();
    // ecritureSchemaJeu();
    // persistenceSchemaProduit();
    // persistenceSchemaJeu();
    // dupliqueSchemaProduitEcritSchemaJeu();
    // dupliqueSchemaProduitEcritSchemaJeuXML();
    // litSchemaJeuXML();
    // litSchemaProduitXML();
    Test.litSchemaJeuXML();

  }

  /**
   * cree un schema jeu et l'ecrit dans la base oracle
   **/
  /*
   * public static void ecritureSchemaJeu(){ SchemaConceptuelJeu schemaJeu = new
   * SchemaConceptuelJeu(); schemaJeu.createFeatureType("route!"); FeatureType
   * ft = new FeatureType(); ft.setTypeName("batiment!");
   * ft.setSchema(schemaJeu); schemaJeu.createFeatureAttribute(ft, "nature!",
   * "String", true);
   * 
   * 
   * schemaJeu.createFeatureAttributeValue((AttributeType)ft.getFeatureAttributeI
   * (0), "valeur1 !");
   * 
   * 
   * schemaJeu.createFeatureAttributeValue((AttributeType)ft.getFeatureAttributeI
   * (0), "valeur2 !");
   * System.out.println("ft dans ce schema : "+schemaJeu.getFeatureTypes());
   * schemaJeu.createFeatureAssociation("acces!",(FeatureType)schemaJeu.
   * getFeatureTypeI(0), (FeatureType)schemaJeu.getFeatureTypeI(1),
   * "donne acces à !", "est accedé par !");
   * 
   * 
   * System.out.println("asso dans ce schéma : "+schemaJeu.getFeatureAssociations
   * ()); System.out.println("schema produit cree");
   * 
   * System.out.println("att dans ce schéma : "+schemaJeu.getFeatureAttributes(
   * ).size()); System.out.println("val enumerees de cet attribut = "+schemaJeu.
   * getFeatureAttributes().get(0).getValuesDomain().size());
   * System.out.println("att Values dans ce schéma : "+schemaJeu.
   * getFeatureAttributeValues().size()); System.out.println("schema jeu cree");
   * schemaJeu.setDefinition("créé a la volée pdt un test"); MdDataSet.db = new
   * MdGeodatabase(); MdDataSet.db.begin();
   * MdDataSet.db.makePersistent(schemaJeu); MdDataSet.db.commit(); }
   */

  /**
   * cree un schema produit et l'ecrit dans la base oracle
   **/
  /*
   * public static void ecritureSchemaProduit(){ SchemaConceptuelProduit
   * schemaProduit = new SchemaConceptuelProduit();
   * schemaProduit.createFeatureType("route !"); FeatureType ft = new
   * FeatureType(); ft.setTypeName("batiment !"); ft.setSchema(schemaProduit);
   * schemaProduit.createFeatureAttribute(ft, "nature!", "String", true);
   * schemaProduit.createFeatureAttributeValue((AttributeType)ft.
   * getFeatureAttributeI(0), "valeur1 !");
   * schemaProduit.createFeatureAttributeValue((AttributeType)ft.
   * getFeatureAttributeI(0), "valeur2 !");
   * 
   * System.out.println("ft dans ce schema : "+schemaProduit.getFeatureTypes())
   * ;
   * 
   * schemaProduit.createFeatureAssociation("acces !",(FeatureType)schemaProduit
   * .getFeatureTypeI(0), (FeatureType)schemaProduit.getFeatureTypeI(1),
   * "donne acces à !", "est accedé par !");
   * System.out.println("asso dans ce schéma : "+schemaProduit.
   * getFeatureAssociations()); System.out.println("schema produit cree");
   * schemaProduit.setDefinition("créé a la volée pdt un test"); MdDataSet.db =
   * new MdGeodatabase(); MdDataSet.db.begin();
   * MdDataSet.db.makePersistent(schemaProduit); MdDataSet.db.commit(); }
   */

  /**
   * cree un schemaProduit, le duplique en un schéma jeu et écrit celui-ci dans
   * la base oracle
   */
  /*
   * public static void creationSchemaProduitJeuVolee(){ SchemaISOProduit
   * schemaProd = new SchemaISOProduit(); schemaProd.createFeatureType("route");
   * FeatureType ft = new FeatureType(); ft.setTypeName("batiment");
   * ft.setSchema(schemaProd); schemaProd.createFeatureAttribute(ft, "nature",
   * "String");
   * System.out.println("ft dans ce schema : "+schemaProd.getFeatureTypes());
   * schemaProd.createFeatureAssociation("acces",(FeatureType)schemaProd.
   * getFeatureTypeI(0), (FeatureType)schemaProd.getFeatureTypeI(1),
   * "donne acces à", "est accedé par");
   * 
   * 
   * System.out.println("asso dans ce schéma : "+schemaProd.getFeatureAssociations
   * ()); System.out.println("schema produit cree"); SchemaISOJeu schemaJeu =
   * new SchemaISOJeu(schemaProd); System.out.println("schema jeu cree");
   * schemaJeu.setDefinition("créé à la volée pdt un test");
   * //schemaJeu.setId(2); DataSetCommun.db = new GeodatabaseCommun();
   * DataSetCommun.db.begin(); DataSetCommun.db.makePersistent(schemaJeu);
   * DataSetCommun.db.commit(); }
   */

  /**
   * lit un schemaProduit, le duplique en un schemaJeu et ecrit celui-ci dans la
   * BD
   */
  public static void dupliqueSchemaProduitEcritSchemaJeu() {
    DataSet.db = new GeodatabaseOjbPostgis();
    List<SchemaConceptuelProduit> listSchemas = DataSet.db
        .loadAll(SchemaConceptuelProduit.class);
    System.out.println("nb de schémas = " + listSchemas.size());
    SchemaConceptuelProduit schemaProduit = listSchemas.get(0);

    SchemaConceptuelJeu schemaJeu = new SchemaConceptuelJeu(schemaProduit);
    System.out.println("schema jeu cree");
    schemaJeu.setDefinition("créé à la volée pdt un test");
    DataSet.db.begin();
    DataSet.db.makePersistent(schemaJeu);
    DataSet.db.commit();

  }

  /**
   * lit un schemaProduit de la base, le duplique en un schemaJeu et ecrit
   * celui-ci dans un fichier XML
   */
  public static void dupliqueSchemaProduitEcritSchemaJeuXML() {
    DataSet.db = new GeodatabaseOjbPostgis();
    File fileSchema = new File(
        "D:/Users/Balley/java/workspace/GeOxygene/src/fr/ign/cogit/appli/sissi/terranumerica/ressources/schemaConceptuelSerialise.xml");
    List<SchemaConceptuelProduit> listSchemas = DataSet.db
        .loadAll(SchemaConceptuelProduit.class);
    System.out.println("nb de schémas = " + listSchemas.size());
    SchemaConceptuelProduit schemaProduit = listSchemas.get(0);

    SchemaConceptuelJeu schemaJeu = new SchemaConceptuelJeu(schemaProduit);
    System.out.println("schema jeu cree");
    schemaJeu.setDefinition("créé à la volée pdt un test");
    ParserSchemaConceptuel parser = new ParserSchemaConceptuel();
    parser.ecritSchemaConceptuelJeu(schemaJeu, fileSchema);

  }

  /**
   * charge un schémaJeu de la base Oracle, le lit puis l'efface de la base
   */
  public static void persistenceSchemaJeu() {
    System.out.println("teste persistence schemaISOJeu");
    DataSet.db = new GeodatabaseOjbPostgis();
    DataSet.db.begin();
    List<SchemaConceptuelJeu> listSchemas = DataSet.db
        .loadAll(SchemaConceptuelJeu.class);
    System.out.println("nb de schémas = " + listSchemas.size());
    SchemaConceptuelJeu schemaJeu = listSchemas.get(0);
    System.out.println("Le 2eme = ");
    System.out.println("id = " + schemaJeu.getId());
    System.out.println(schemaJeu.getNomSchema());
    System.out.println(schemaJeu.getDefinition());
    System.out.println("associations "
        + schemaJeu.getFeatureAssociations().size());
    System.out.println("featureTypes " + schemaJeu.getFeatureTypes().size());
    System.out.println("featureTypes "
        + schemaJeu.getFeatureTypeI(0).getTypeName());
    // System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(1).getTypeName());
    // System.out.println("AssoTypes "+schemaJeu.getFeatureAssociations().get(0).getTypeName());

    schemaJeu.initNM();
    /*
     * System.out.println("\nroles de ft 1 : ");
     * 
     * System.out.println(schemaJeu.getFeatureTypeI(0).getRoleI(0).getMemberName
     * ()); System.out.println("\nassos de ft 1 : ");
     * System.out.println(schemaJeu.getFeatureTypeI(0).getMemberOf().get(0).
     * getTypeName()); System.out.println("\nmembres de asso 1 : ");
     * System.out.println(schemaJeu.getFeatureAssociations().get(0).
     * getLinkBetweenI(0).getTypeName());
     * System.out.println(schemaJeu.getFeatureAssociations().get(0).
     * getLinkBetweenI(1).getTypeName());
     * System.out.println("\nasso et ft de role 1 : ");
     * System.out.println(schemaJeu.getAssociationRoles().get(0).
     * getAssociationType().getTypeName());
     * 
     * System.out.println(schemaJeu.getAssociationRoles().get(0).getFeatureType
     * ().getTypeName());
     */

    /*
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(3).getTypeName
     * ()); System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(4).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(5).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(6).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(7).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(8).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(9).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(10).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(11).
     * getTypeName());
     */

    System.out.println("maintenant je l'efface");
    SchemaPersistentOJB.deleteSchema(schemaJeu);
    DataSet.db.commit();

  }

  /**
   * charge un schémaJeu de la base Oracle, le lit puis l'efface de la base
   */
  public static void persistenceSchemaProduit() {
    System.out.println("teste persistence schemaISOProduit");
    DataSet.db = new GeodatabaseOjbPostgis();
    DataSet.db.begin();
    List<SchemaConceptuelProduit> listSchemas = DataSet.db
        .loadAll(SchemaConceptuelProduit.class);
    System.out.println("nb de schémas = " + listSchemas.size());
    SchemaConceptuelProduit schemaProduit = listSchemas.get(2);
    System.out.println("Le 2eme = ");
    System.out.println("id = " + schemaProduit.getId());
    System.out.println(schemaProduit.getNomSchema());
    System.out.println(schemaProduit.getDefinition());
    System.out.println("associations "
        + schemaProduit.getFeatureAssociations().size());
    System.out
        .println("featureTypes " + schemaProduit.getFeatureTypes().size());
    System.out.println("featureTypes "
        + schemaProduit.getFeatureTypeI(0).getTypeName());
    System.out.println("featureTypes "
        + schemaProduit.getFeatureTypeI(1).getTypeName());
    System.out.println("AssoTypes "
        + schemaProduit.getFeatureAssociations().get(0).getTypeName());

    schemaProduit.initNM();

    System.out.println("\nroles de ft 1 : ");
    System.out.println(schemaProduit.getFeatureTypeI(0).getRoleI(0)
        .getMemberName());

    System.out.println("\nassos de ft 1 : ");
    System.out.println(schemaProduit.getFeatureTypeI(0).getMemberOf().get(0)
        .getTypeName());

    System.out.println("\nmembres de asso 1 : ");
    System.out.println(schemaProduit.getFeatureAssociations().get(0)
        .getLinkBetweenI(0).getTypeName());
    System.out.println(schemaProduit.getFeatureAssociations().get(0)
        .getLinkBetweenI(1).getTypeName());

    System.out.println("\nasso et ft de role 1 : ");
    System.out.println(schemaProduit.getAssociationRoles().get(0)
        .getAssociationType().getTypeName());
    System.out.println(schemaProduit.getAssociationRoles().get(0)
        .getFeatureType().getTypeName());
    /*
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(3).getTypeName
     * ()); System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(4).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(5).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(6).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(7).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(8).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(9).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(10).
     * getTypeName());
     * System.out.println("featureTypes "+schemaJeu.getFeatureTypeI(11).
     * getTypeName());
     */

    System.out.println("maintenant je l'efface");
    SchemaPersistentOJB.deleteSchema(schemaProduit);
    DataSet.db.commit();

  }

  /**
   * Génère un objet SchemaConceptuel pour faire des tests. Pour pouvoir être
   * utilisé ce schéma doit être affecté à un jeu
   * (MdDataSet.setSchemaConceptuel()). Il doit pointer vers des noms de classe
   * et de champs java qui correspondent à votre implémentation et qui sont
   * référencées dans votre mapping.
   * @return un objet schémaConceptuelJeu miniature composé de deux classes et
   *         une association
   */
  public static SchemaConceptuelJeu generationSchema() {

    SchemaConceptuelJeu schemaJeu = new SchemaConceptuelJeu();
    schemaJeu.setNomSchema("routes et batiments");
    schemaJeu.setDefinition("schéma miniature créé a la volée pdt un test");

    // creation d'une premiere classe
    schemaJeu.createFeatureType("Tronçon de route");

    // creation d'une deuxieme classe
    FeatureType ft = new FeatureType();
    ft.setTypeName("batiment");

    ft.setSchema(schemaJeu);
    schemaJeu.createFeatureAttribute(ft, "utilisation", "String", true);
    schemaJeu.createFeatureAttributeValue(ft
        .getFeatureAttributeByName("utilisation"), "administratif");
    schemaJeu.createFeatureAttributeValue(ft
        .getFeatureAttributeByName("utilisation"), "habitation");
    // le champ java d'implémentation :

    // création d'une assoication
    schemaJeu.createFeatureAssociation("acces", (FeatureType) schemaJeu
        .getFeatureTypeI(0), (FeatureType) schemaJeu.getFeatureTypeI(1),
        "donne acces à", "est accedé par");

    // renseignement des classes et champs java d'implémentation
    ((FeatureType) schemaJeu.getFeatureTypeByName("Tronçon de route"))
        .setNomClasse("donnees.bdcarto.TronconRoute");
    ((FeatureType) schemaJeu.getFeatureTypeByName("batiment"))
        .setNomClasse("donnees.bdcarto.Batiment");
    ((FeatureType) schemaJeu.getFeatureTypeByName("batiment"))
        .getFeatureAttributeByName("utilisation").setNomField("fonction");

    // lecture du schéma créé
    System.out.println("ft dans ce schema : " + schemaJeu.getFeatureTypes());
    System.out.println("asso dans ce schéma : "
        + schemaJeu.getFeatureAssociations());
    System.out.println("schema produit cree");
    System.out.println("att dans ce schéma : "
        + schemaJeu.getFeatureAttributes().size());
    System.out.println("val enumerees de cet attribut = "
        + schemaJeu.getFeatureAttributes().get(0).getValuesDomain().size());

    return schemaJeu;
  }

  /**
   * construit un objet SchemaConceptuelJeu a partir d'un fichier XML
   * correspondant au modele geoxygene et lit cet objet
   */
  public static void litSchemaJeuXML() {

    SchemaConceptuelJeu schemaJeu = new SchemaConceptuelJeu();

    File file = new File(
        "src/fr/ign/cogit/appli/commun/metadonnees/schemaConceptuel/util/persistance/exempleSchemaConceptuel.xml");
    ParserSchemaConceptuel parser = new ParserSchemaConceptuel();
    schemaJeu = parser.litSchemaConceptuelJeuXML(file);

    // lecture du schéma créé
    System.out.println("ft dans ce schema : " + schemaJeu.getFeatureTypes());
    System.out.println("asso dans ce schéma : "
        + schemaJeu.getFeatureAssociations());
    System.out.println("att dans ce schéma : "
        + schemaJeu.getFeatureAttributes().size());

    System.out.println("val enumerees de cet attribut = "
        + schemaJeu.getFeatureAttributes().get(0).getValuesDomain().size());
    System.out.println("\nroles de ft 1 : ");
    System.out
        .println(schemaJeu.getFeatureTypeI(0).getRoleI(0).getMemberName());

    System.out.println("\nassos de ft 1 : ");
    System.out.println(schemaJeu.getFeatureTypeI(0).getMemberOf().get(0)
        .getTypeName());

    System.out.println("\nmembres de asso 1 : ");
    System.out.println(schemaJeu.getFeatureAssociations().get(0)
        .getLinkBetweenI(0).getTypeName());
    System.out.println(schemaJeu.getFeatureAssociations().get(0)
        .getLinkBetweenI(1).getTypeName());

    System.out.println("\nasso et ft de role 1 : ");
    System.out.println(schemaJeu.getAssociationRoles().get(0)
        .getAssociationType().getTypeName());
    System.out.println(schemaJeu.getAssociationRoles().get(0).getFeatureType()
        .getTypeName());

  }

  /**
   * construit un objet SchemaConceptuelProduit a partir d'un fichier XML
   * correspondant au modele geoxygene et lit cet objet
   */
  public static void litSchemaProduitXML() {

    SchemaConceptuelProduit schemaProduit = new SchemaConceptuelProduit();

    File file = new File(
        "src/fr/ign/cogit/appli/commun/metadonnees/schemaConceptuel/util/persistance/exempleSchemaConceptuel.xml");
    ParserSchemaConceptuel parser = new ParserSchemaConceptuel();
    schemaProduit = parser.litSchemaConceptuelProduitXML(file);

    // lecture du schéma créé
    System.out
        .println("ft dans ce schema : " + schemaProduit.getFeatureTypes());
    System.out.println("asso dans ce schéma : "
        + schemaProduit.getFeatureAssociations());
    System.out.println("att dans ce schéma : "
        + schemaProduit.getFeatureAttributes().size());

    System.out.println("val enumerees de cet attribut = "
        + schemaProduit.getFeatureAttributes().get(0).getValuesDomain().size());
    System.out.println("\nroles de ft 1 : ");
    System.out.println(schemaProduit.getFeatureTypeI(0).getRoleI(0)
        .getMemberName());

    System.out.println("\nassos de ft 1 : ");
    System.out.println(schemaProduit.getFeatureTypeI(0).getMemberOf().get(0)
        .getTypeName());

    System.out.println("\nmembres de asso 1 : ");
    System.out.println(schemaProduit.getFeatureAssociations().get(0)
        .getLinkBetweenI(0).getTypeName());
    System.out.println(schemaProduit.getFeatureAssociations().get(0)
        .getLinkBetweenI(1).getTypeName());

    System.out.println("\nasso et ft de role 1 : ");
    System.out.println(schemaProduit.getAssociationRoles().get(0)
        .getAssociationType().getTypeName());
    System.out.println(schemaProduit.getAssociationRoles().get(0)
        .getFeatureType().getTypeName());

  }

}
