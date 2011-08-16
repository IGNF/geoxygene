package fr.ign.cogit.geoxygene.shemageo.schema.exnathalie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.schema.SchemaConceptuel;

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
    this.schemasDisponibles = new ArrayList<SchemaConceptuel>();
    this.schemasDisponibles.clear();
    /*
     * this.modelisationsSchemaRef = new ArrayList<Modelisation>();
     * this.modelisationsSchemaRef.clear(); this.modelisationsSchemaApp = new
     * ArrayList<Modelisation>(); this.modelisationsSchemaApp.clear();
     */
  }

  /** Liste des schémas disponibles */
  private List<SchemaConceptuel> schemasDisponibles;

  public void setSchemasDisponibles(List<SchemaConceptuel> s) {
    this.schemasDisponibles = s;
  }

  public void addSchemaDisponible(SchemaConceptuel s) {
    if (!this.schemasDisponibles.contains(s)) {
      this.schemasDisponibles.add(s);
    } else {
      return;
    }
  }

  public void removeSchemaDisponible(SchemaConceptuel s) {
    if (this.schemasDisponibles.contains(s)) {
      this.schemasDisponibles.remove(s);
    } else {
      return;
    }
  }

  public List<SchemaConceptuel> getSchemaDisponibles() {
    return this.schemasDisponibles;
  }

  /** Renvoie un schéma défini par son nom */
  public SchemaConceptuel getSchemaDisponibleParNom(String nom) {
    SchemaConceptuel s = null;
    Iterator<SchemaConceptuel> it = this.schemasDisponibles.iterator();
    while (it.hasNext()) {
      SchemaConceptuel schema = it.next();
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
