package fr.ign.cogit.geoxygene.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

/**
 * 
 * @author Balley
 * 
 *         Un produit qui a un producteur, des métadonnées minimales et
 *         notamment un schéma conceptuel (de type SchemaISOProduit) D'autres
 *         métadonnées sont à venir, notamment des MD ISO 19115 et des
 *         spécifications Cogit complètes
 */

public class Produit {

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  protected String nom;

  /**
   * @return the nom
   */
  public String getNom() {
    return this.nom;
  }

  /**
   * @param nom the nom to set
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * 1 = BD 2 = sûrie de cartes
   */
  protected int type;

  /**
   * @return the type
   */
  public int getType() {
    return this.type;
  }

  /**
   * @param type the type to set 1 = BD 2 = sûrie de cartes
   */
  public void setType(int type) {
    this.type = type;
  }

  protected String producteur;

  /**
   * @return the producteur
   */
  public String getProducteur() {
    return this.producteur;
  }

  /**
   * @param producteur the producteur to set
   */
  public void setProducteur(String producteur) {
    this.producteur = producteur;
  }

  protected double echelleMin;

  /**
   * @return the echelleMin
   */
  public double getEchelleMin() {
    return this.echelleMin;
  }

  /**
   * @param echelleMin the echelleMin to set
   */
  public void setEchelleMin(double echelleMin) {
    this.echelleMin = echelleMin;
  }

  protected double echelleMax;

  /**
   * @return the echelleMax
   */
  public double getEchelleMax() {
    return this.echelleMax;
  }

  /**
   * @param echelleMax the echelleMax to set
   */
  public void setEchelleMax(double echelleMax) {
    this.echelleMax = echelleMax;
  }

  protected SchemaConceptuelProduit schemaConceptuel;

  /**
   * @return the schemaConceptuel
   */

  public SchemaConceptuelProduit getSchemaConceptuel() {
    return this.schemaConceptuel;
  }

  /**
   * @param schemaConceptuel the schemaConceptuel to set
   */

  public void setSchemaConceptuel(SchemaConceptuelProduit schemaConceptuel) {
    this.schemaConceptuel = schemaConceptuel;
  }

  /**
   * la liste des jeux de données dont on dispose pour ce produit
   */
  protected List<DataSet> jeuxDisponibles;

  /**
   * @return the jeuxDisponibles
   */
  public List<DataSet> getJeuxDisponibles() {
    return this.jeuxDisponibles;
  }

  /**
   * @param jeuxDisponibles the jeuxDisponibles to set
   */
  public void setJeuxDisponibles(List<DataSet> jeuxDisponibles) {
    this.jeuxDisponibles = jeuxDisponibles;
  }

}
