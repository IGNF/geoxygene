package fr.ign.cogit.geoxygene.schemageo.schema.exnathalie;
/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at providing an open framework which implements OGC/ISO specifications for the development and deployment of geographic (GIS) applications. It is a open source contribution of the COGIT laboratory at the Institut Géographique National (the French National Mapping Agency). See: http://oxygene-project.sourceforge.net 
 * Copyright (C) 2005 Institut Géographique National 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or any later version. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library (see file LICENSE if present); if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.schema.Produit;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

/**
 * Classe qui crée les schémas bdtopo et bdcarto
 * @author NFAbadie
 * 
 */
public class CreationSchemas {

  public static void main(String[] args) {
    CreationSchemas.creeSchemas();
  }

  @SuppressWarnings("unused")
  private static void deleteSchema() {
    Util.deleteSchema("Catalogue BDTOPO");
    Util.deleteSchema("Catalogue BDCARTO");
  }

  private static void creeSchemas() {
    DataSet.db = new GeodatabaseOjbPostgis();
    DataSet.db.begin();

    // Creation des schemas de donnees: schema de produit et schema de jeu
    SchemaConceptuelProduit schemaProduit = SchemaBDTopoPays12Nat
        .creeSchemaBDTopoPays12();
    SchemaConceptuelJeu monschemabdt = new SchemaConceptuelJeu(schemaProduit);

    SchemaConceptuelProduit schemaProduitBDCarto = SchemaBDCarto3Nat
        .creeSchemaBDCarto3();
    SchemaConceptuelJeu monschemabdc = new SchemaConceptuelJeu(
        schemaProduitBDCarto);

    // Creation du produit correspondant au schéma de produit BDTOPO
    Produit p = new Produit();
    p.setId(1);
    p.setNom("BD TOPO Pays version 1.2");
    p.setProducteur("Institut Géographique National");
    p.setSchemaConceptuel(schemaProduit);
    p.setType(1);
    p.setEchelleMin(0.00004);
    p.setEchelleMax(0.0001);

    // Creation du produit correspondant au schéma de produit BDTOPO
    Produit p1 = new Produit();
    p1.setId(2);
    p1.setNom("BD CARTO version 3");
    p1.setProducteur("Institut Géographique National");
    p1.setSchemaConceptuel(schemaProduitBDCarto);
    p1.setType(1);
    p1.setEchelleMin(0.00001);
    p1.setEchelleMax(0.00002);

    // Creation des dataset
    DataSet dataset1 = new DataSet();
    dataset1.setAppartientA(null);
    dataset1.setDate("Décembre 2002");
    dataset1.setId(1);
    dataset1.setNom("Jeu de données test BD TOPO");
    dataset1.setPersistant(true);
    dataset1.setProduit(p);
    dataset1.setSchemaConceptuel(monschemabdt);
    dataset1.setTypeBD("BD TOPO 1.2");
    dataset1.setModele("Structuré");
    monschemabdt.setDataset(dataset1);

    // SchemaConceptuelJeu s1 =
    // SchemaJeuRoutierBDTOPO.creeSchemaRoutierBDTOPO();
    // DataSet dataset2 = new DataSet();
    // dataset2.setAppartientA(null);
    // dataset2.setDate("Décembre 2002");
    // dataset2.setId(2);
    // dataset2.setProduit(null);
    // dataset2.setNom("Routier BDTOPO");
    // dataset2.setPersistant(true);
    // dataset2.setSchemaConceptuel(s1);
    // s1.setDataset(dataset2);
    // dataset2.setTypeBD("Thème routier de la BD Topo v.1.2");
    // dataset2.setModele("Structuré");

    DataSet dataset4 = new DataSet();
    dataset4.setAppartientA(null);
    dataset4.setDate("Juin 2005");
    dataset4.setId(4);
    dataset4.setNom("Jeu de données test BD CARTO");
    dataset4.setPersistant(true);
    dataset4.setProduit(p1);
    dataset4.setSchemaConceptuel(monschemabdc);
    dataset4.setTypeBD("BD TOPO 1.2");
    dataset4.setModele("Structuré");
    monschemabdc.setDataset(dataset4);

    // SchemaConceptuelJeu s2 =
    // SchemaJeuRoutierBDCARTO.creeSchemaRoutierBDCARTO();
    // DataSet dataset3 = new DataSet();
    // dataset3.setAppartientA(null);
    // dataset3.setDate("Juin 2005");
    // dataset3.setId(3);
    // dataset3.setProduit(null);
    // dataset3.setNom("Routier BDCARTO");
    // dataset3.setPersistant(true);
    // dataset3.setSchemaConceptuel(s2);
    // s2.setDataset(dataset3);
    // dataset3.setTypeBD("Thème routier de la BD Carto V.3");
    // dataset3.setModele("Structuré");

    DataSet.db.makePersistent(schemaProduit);
    DataSet.db.makePersistent(monschemabdt);
    DataSet.db.makePersistent(schemaProduitBDCarto);
    DataSet.db.makePersistent(monschemabdc);
    DataSet.db.makePersistent(p);
    DataSet.db.makePersistent(p1);
    DataSet.db.makePersistent(dataset1);
    // DataSet.db.makePersistent(dataset2);
    // DataSet.db.makePersistent(dataset3);
    DataSet.db.makePersistent(dataset4);
    // DataSet.db.makePersistent(s1);
    // DataSet.db.makePersistent(s2);
    DataSet.db.commit();
  }
}
