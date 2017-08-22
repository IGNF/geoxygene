package fr.ign.cogit.geoxygene.osm.anonymization.algorithm;

/**
 * Classe contenant les informations sur un 
 * changeset utilisées dans le processus 
 * d'échange de contributions pour 
 * pouvoir mettre à jour les caractéristiques 
 * de la base de données pendant le traitement.
 * 
 * @author Matthieu Dufait
 */
 public class ChangesetData {

  /**
   * Identifiant du changeset.
   */
  private long id;
  /**
   * Identifiant de l'utilisateur 
   * ayant contribué le changeset.
   */
  private long uId;
  /**
   * Nombre de nodes 
   * dans le changeset.
   */
  private long nbNode;
  /**
   * Nombre de ways 
   * dans le changeset.
   */
  private long nbWay;
  /**
   * Nombre de relations  
   * dans le changeset.
   */
  private long nbRelation;
  /**
   * boolean indicant si le 
   * changeset a un tag comment.
   */
  private boolean hasTagComment;
  
  /**
   * Constructeur intialisant les attributs
   * à une valeur invalide (-1) 
   * et le booléen à false.
   */
  public ChangesetData() {
    id = uId = nbNode = nbWay = nbRelation = -1;
    hasTagComment = false;
  }

  /**
   * Constructeur intialisant les attributs
   * avec les paramètres.
   * @param id
   * @param uId
   * @param nbNode
   * @param nbWay
   * @param nbRelation
   * @param hasTagComment
   */
  public ChangesetData(long id, long uId, long nbNode, long nbWay,
      long nbRelation, boolean hasTagComment) {
    super();
    this.id = id;
    this.uId = uId;
    this.nbNode = nbNode;
    this.nbWay = nbWay;
    this.nbRelation = nbRelation;
    this.hasTagComment = hasTagComment;
  }

  /**
   * Retourne l'identifiant du changeset.
   * @return id
   */
  public long getId() {
    return id;
  }


  /**
   * Change la valeur de l'identifiant numérique
   * avec la valeur donnée en paramètre.
   * @param id nouvel identifiant.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Retourne l'identifiant de l'utilisateur.
   * @return uId
   */
  public long getuId() {
    return uId;
  }

  /**
   * Change la valeur de l'identifiant numérique
   * de l'utilisateur avec la valeur donnée en paramètre.
   * @param uId
   */
  public void setuId(long uId) {
    this.uId = uId;
  }

  /**
   * Retourne la valeur du nombre 
   * de nodes contribués.
   * @return nbNode
   */
  public long getNbNode() {
    return nbNode;
  }

  /**
   * Change la valeur du nombre de nodes contribués
   * avec la valeur donnée en paramètre.
   * @param nbNode
   */
  public void setNbNode(long nbNode) {
    this.nbNode = nbNode;
  }

  /**
   * Retourne la valeur du nombre 
   * de ways contribués.
   * @return nbWay
   */
  public long getNbWay() {
    return nbWay;
  }

  /**
   * Change la valeur du nombre de ways contribués
   * avec la valeur donnée en paramètre.
   * @param nbWay
   */
  public void setNbWay(long nbWay) {
    this.nbWay = nbWay;
  }

  /**
   * Retourne la valeur du nombre 
   * de relations contribuées.
   * @return nbRelation
   */
  public long getNbRelation() {
    return nbRelation;
  }

  /**
   * Change la valeur du nombre de relations contribuées
   * avec la valeur donnée en paramètre.
   * @param nbRelation
   */
  public void setNbRelation(long nbRelation) {
    this.nbRelation = nbRelation;
  }
  
  /**
   * Retourne un booléen indiquant 
   * si le changeset à un tag comment.
   * @return hasTagComment
   */
  public boolean hasTagComment() {
    return hasTagComment;
  }

  /**
   * Change la valeur de hasTagComment
   * avec la valeur données en paramètre.
   * @param hasTagComment
   */
  public void setHasTagComment(boolean hasTagComment) {
    this.hasTagComment = hasTagComment;
  }

  @Override
  public String toString() {
    return "ChangesetData [id=" + id + ", uId=" + uId + ", nbNode=" + nbNode
        + ", nbWay=" + nbWay + ", nbRelation=" + nbRelation + ", hasTagComment="
        + hasTagComment + "]";
  }
 }