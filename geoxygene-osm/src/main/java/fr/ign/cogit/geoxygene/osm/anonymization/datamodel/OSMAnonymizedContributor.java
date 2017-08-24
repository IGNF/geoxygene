package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe représentant un contributeur
 * OpenStreetMap après que le processus 
 * d'anonymisation se soit déroulé.
 * Ce contributeur est donc anonyme et 
 * n'est pas censé représenter un utili-
 * sateur existant en particulier.
 * 
 * Cette classe permet un accès vers 
 * l'ensemble des changeset du contributeur
 * ainsi que son évaluation.
 * 
 * Un nom a été donné par commodité et un id 
 * pour une intégration plus facile en 
 * base de données.
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedContributor {
  private long idUser;
  private OSMQualityAssessment evaluation;
  private Set<OSMAnonymizedChangeSet> editSet;
  
  /**
   * Constructeur par défaut initialisant les 
   * champs à une valeur par défaut sauf 
   * l'identifiant qui est mis à la valeur 
   * invalide de -1 qui doit être redéfinie 
   * dans un traitement ultérieur
   */
  public OSMAnonymizedContributor() {
    this(-1);
  }
  
  /**
   * Constructeur initialisant l'identifiant 
   * avec la valeur fournie en paramètre.
   * Les autres attributs sont initialisés par défaut
   * @param id indentifiant numérique du contributeur
   */
  public OSMAnonymizedContributor(long id) {
    this.setId(id);
    this.evaluation = new OSMQualityAssessment();
    this.editSet = new HashSet<OSMAnonymizedChangeSet>();
  }
  
  /**
   * Accesseur en lecture sur l'identifiant
   * du contributeur.
   * @return id
   */
  public long getId() {
    return idUser;
  }

  /**
   * Accesseur en écriture sur l'id
   * @param id nouvel indentifiant
   */
  public void setId(long id) {
    this.idUser = id;
  }

  /**
   * Accesseur en lecture sur l'évaluation 
   * du contributeur
   * @return evaluation
   */
  public OSMQualityAssessment getEvaluation() {
    return evaluation;
  }

  /**
   * Accesseur en écriture sur l'évaluation 
   * du contibuteur
   * @param eval nouvelle évaluation
   */
  public void setEvaluation(OSMQualityAssessment eval) {
    this.evaluation = eval;
  }
  
  /**
   * Accesseur en lecture et en écriture sur 
   * l'ensemble des changeset associés à ce 
   * contributeur
   * @return editSet
   */
  public Set<OSMAnonymizedChangeSet> getEditSet() {
    return editSet;
  }
}
