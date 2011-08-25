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

package fr.ign.cogit.geoxygene.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;

/**
 * Interface pour tout schéma conceptuel. (Application schema dans les normes
 * ISO) Il n'a pas de type de données standard ApplicationSchema. Nous le
 * définissons comme un ensemble d'éléments standards définis dans le package
 * fr.ign.cogit.appli.commun.metadonnees.schemaConceptuel.interfacesISO.
 * @author Nathalie Abadie
 * @author Sandrine Balley
 */
public interface SchemaConceptuel <FT extends GF_FeatureType> {

  /**
   * dèsignation usuelle du schéma
   */
  public String getNomSchema();

  public void setNomSchema(String nom);

  /**
   * Description du schéma
   */
  public String getDefinition();

  public void setDefinition(String def);

  /**
   * Liste des classes appartenant au schéma
   */
  public List<FT> getFeatureTypes();

  public void setFeatureTypes(List<FT> ftList);

  public void addFeatureType(FT ft);

  public void removeFeatureTypeFromSchema(FT ft);

  public FT getFeatureTypeByName(String name);

  /*
   * ******************************************************************
   * méthodes pour manipuler mon schéma
   * ******************************************************************
   */

  // méthodes enlevées, descendues dans schemaISOJeu et SchemaISOProduit

  /*
   * ******************************************************************
   * méthodes pour lister les différents éléments du schéma
   * ******************************************************************
   */

  // méthodes enlevées, descendues dans schemaISOJeu et SchemaISOProduit

  /*
   * ******************************************************************
   * méthodes pour sauvegarder mon schéma
   * ******************************************************************
   */

  /*
   * méthodes enlevées car elles obligeaient un import de classe "outil" dans le
   * modele. Les méthodes save et delete sont implementees de façon statique
   * dans SchemaPersistentOJB
   */

  public void initNM();

}
