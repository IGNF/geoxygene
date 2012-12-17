package fr.ign.cogit.geoxygene.schemageo.schema.exnathalie;
/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at providing an open framework which implements OGC/ISO specifications for the development and deployment of geographic (GIS) applications. It is a open source contribution of the COGIT laboratory at the Institut Géographique National (the French National Mapping Agency). See: http://oxygene-project.sourceforge.net 
 * Copyright (C) 2005 Institut Géographique National 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or any later version. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library (see file LICENSE if present); if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.schema.ConceptualSchema;

public class ContexteSchemasSingleton {

  // Instance unique de la classe
  private static ContexteSchemasSingleton contexteSchemas;

  /** Méthode qui renvoie notre instance de classe "ContexteSchemasSingleton" */
  public static synchronized ContexteSchemasSingleton getContexteSchemaSingleton() {
    if (ContexteSchemasSingleton.contexteSchemas == null) {
      ContexteSchemasSingleton.contexteSchemas = new ContexteSchemasSingleton();
    }
    return ContexteSchemasSingleton.contexteSchemas;
  }

  // Méthode qui empêche le clonage de cette classe
  @Override
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  // Constructeur privé
  private ContexteSchemasSingleton() {
    this.schemasDisponibles = new ArrayList<ConceptualSchema<?, ?, ?, ?, ?, ?>>();
    this.schemasDisponibles.clear();
    /*
     * this.modelisationsSchemaRef = new ArrayList<Modelisation>();
     * this.modelisationsSchemaRef.clear(); this.modelisationsSchemaApp = new
     * ArrayList<Modelisation>(); this.modelisationsSchemaApp.clear();
     */
  }

  /** Liste des schémas disponibles */
  private List<ConceptualSchema<?, ?, ?, ?, ?, ?>> schemasDisponibles;

  public void setSchemasDisponibles(List<ConceptualSchema<?, ?, ?, ?, ?, ?>> s) {
    this.schemasDisponibles = s;
  }

  public void addSchemaDisponible(ConceptualSchema<?, ?, ?, ?, ?, ?> s) {
    if (!this.schemasDisponibles.contains(s)) {
      this.schemasDisponibles.add(s);
    } else {
      return;
    }
  }

  public void removeSchemaDisponible(ConceptualSchema<?, ?, ?, ?, ?, ?> s) {
    if (this.schemasDisponibles.contains(s)) {
      this.schemasDisponibles.remove(s);
    } else {
      return;
    }
  }

  public List<ConceptualSchema<?, ?, ?, ?, ?, ?>> getSchemaDisponibles() {
    return this.schemasDisponibles;
  }

  /** Renvoie un schéma défini par son nom */
  public ConceptualSchema<?, ?, ?, ?, ?, ?> getSchemaDisponibleParNom(String nom) {
      ConceptualSchema<?, ?, ?, ?, ?, ?> s = null;
    Iterator<ConceptualSchema<?, ?, ?, ?, ?, ?>> it = this.schemasDisponibles.iterator();
    while (it.hasNext()) {
        ConceptualSchema<?, ?, ?, ?, ?, ?> schema = it.next();
      if (schema.getNomSchema() == nom) {
        s = schema;
      } else {
        continue;
      }
    }
    return s;
  }

  /**
   * Liste des procedures de représentation disponibles pour le schéma de
   * référence
   */
  /*
   * private List<Modelisation> modelisationsSchemaRef; public void
   * setModelisationsSchemaRef(List<Modelisation>
   * m){this.modelisationsSchemaRef=m;} public void
   * addModelisationsSchemaRef(Modelisation m){ if
   * (!this.modelisationsSchemaRef.
   * contains(m)){this.modelisationsSchemaRef.add(m);} else{return;}} public
   * List<Modelisation> getModelisationsSchemaRef(){return
   * this.modelisationsSchemaRef;}
   */
  /**
   * Liste des procedures de représentation disponibles pour le schéma à
   * apparier
   */
  /*
   * private List<Modelisation> modelisationsSchemaApp; public void
   * setModelisationsSchemaApp(List<Modelisation>
   * m){this.modelisationsSchemaApp=m;} public void
   * addModelisationsSchemaApp(Modelisation m){ if
   * (!this.modelisationsSchemaApp.
   * contains(m)){this.modelisationsSchemaApp.add(m);} else{return;}} public
   * List<Modelisation> getModelisationsSchemaApp(){return
   * this.modelisationsSchemaApp;}
   */

}
