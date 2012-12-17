package fr.ign.cogit.geoxygene.schemageo.schema.exnathalie;
/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at providing an open framework which implements OGC/ISO specifications for the development and deployment of geographic (GIS) applications. It is a open source contribution of the COGIT laboratory at the Institut Géographique National (the French National Mapping Agency). See: http://oxygene-project.sourceforge.net 
 * Copyright (C) 2005 Institut Géographique National 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or any later version. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library (see file LICENSE if present); if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
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
