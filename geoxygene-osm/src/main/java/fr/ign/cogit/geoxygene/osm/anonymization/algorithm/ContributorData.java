package fr.ign.cogit.geoxygene.osm.anonymization.algorithm;

import java.io.Serializable;

/**
 * Classe permettant d'enregistrer les données 
 * sur les contributeurs lors de l'anonymisation
 * par échange de contributions afin de conserver
 * la progression de la répartition des contributions
 * entre les contributeurs à mesure des échanges de 
 * changesets.
 * 
 * @author Matthieu Dufait
 */
public class ContributorData implements Serializable {
  private static final long serialVersionUID = -8919315917945950395L;
  
  /**
   * Identifiant de l'utilisateur.
   */
  private long uId;
  /**
   * Nombre total de nodes contribués avant 
   * le processus d'anonymisation.
   */
  private final long originalUserNbNode;
  /**
   * Nombre total de ways contribués avant 
   * le processus d'anonymisation.
   */
  private final long originalUserNbWay;
  /**
   * Nombre total de relations contribuées avant 
   * le processus d'anonymisation.
   */
  private final long originalUserNbRelation;
  /**
   * Nombre total de changeset contribués 
   * avec un tag comment avant 
   * le processus d'anonymisation.
   */
  private final long originalUserNbTagComment;
  /**
   * Nombre total de nodes contribués.
   */
  private long userNbNode;
  /**
   * Nombre total de ways contribués.
   */
  private long userNbWay;
  /**
   * Nombre total de relations contribuées.
   */
  private long userNbRelation;
  /**
   * Nombre total de changeset contribués 
   * avec un tag comment.
   */
  private long userNbTagComment;
  
  /**
   * Constructeur initialisant les attributs à
   * une valeur invalide (-1).
   */
  public ContributorData() {
    this(-1,-1,-1,-1, -1);
  }
  
  /**
   * Constructeur initialisant les attributs
   * avec les valeurs données en paramètres.
   * @param uId
   * @param userNbNode
   * @param userNbWay
   * @param userNbRelation
   */
  public ContributorData(long uId, long userNbNode, long userNbWay,
      long userNbRelation, long userNbTagComment) {
    super();
    this.uId = uId;
    this.originalUserNbNode = this.userNbNode = userNbNode;
    this.originalUserNbWay = this.userNbWay = userNbWay;
    this.originalUserNbRelation = this.userNbRelation = userNbRelation;
    this.originalUserNbTagComment = this.userNbTagComment = userNbTagComment;
  }
  
  /**
   * Constructeur par recopie de ContributorData,
   * intialise les attributs aux valeurs des 
   * attribut du ContributorData donné en paramètre.
   * @param other
   */
  public ContributorData(ContributorData other) {
    super();
    this.uId = other.uId;
    this.userNbNode = other.userNbNode;
    this.userNbWay = other.userNbWay;
    this.userNbRelation = other.userNbRelation;
    this.userNbTagComment = other.userNbTagComment;
    this.originalUserNbNode = other.originalUserNbNode;
    this.originalUserNbWay = other.originalUserNbWay;
    this.originalUserNbRelation = other.originalUserNbRelation;
    this.originalUserNbTagComment = other.originalUserNbTagComment;
  }

  /**
   * Retourne l'identifiant numérique.
   * @return uId
   */
  public long getuId() {
    return uId;
  }

  /**
   * Change la valeur de l'identifiant numérique
   * avec la valeur donnée en paramètre.
   * @param uId nouvel identifiant.
   */
  public void setuId(long uId) {
    this.uId = uId;
  }
  
  /**
   * Retourne le nombre de nodes 
   * contribués par cet utilisateur.
   * @return userNbNode
   */
  public long getUserNbNode() {
    return userNbNode;
  }

  /**
   * Change la valeur du nombre de nodes 
   * contribués par cet utilisateur 
   * avec la valeur donnée en paramètre.
   * @param userNbNode 
   */
  public void setUserNbNode(long userNbNode) {
    this.userNbNode = userNbNode;
  }

  /**
   * Retourne le nombre de ways 
   * contribués par cet utilisateur.
   * @return userNbWay
   */
  public long getUserNbWay() {
    return userNbWay;
  }

  /**
   * Change la valeur du nombre de ways 
   * contribués par cet utilisateur 
   * avec la valeur donnée en paramètre.
   * @param userNbWay
   */
  public void setUserNbWay(long userNbWay) {
    this.userNbWay = userNbWay;
  }

  /**
   * Retourne le nombre de relations 
   * contribuées par cet utilisateur.
   * @return userNbRelation
   */
  public long getUserNbRelation() {
    return userNbRelation;
  }

  /**
   * Change la valeur du nombre de relations 
   * contribuées par cet utilisateur 
   * avec la valeur donnée en paramètre.
   * @param userNbRelation
   */
  public void setUserNbRelation(long userNbRelation) {
    this.userNbRelation = userNbRelation;
  }
  
  /**
   * Retourne le nombre de tags comment
   * ajoutés par cet utilisateur.
   * @return userNbTagComment
   */
  public long getUserNbTagComment() {
    return userNbTagComment;
  }

  /**
   * Change la valeur du nombre de tags comment
   * ajoutés par cet utilisateur 
   * avec la valeur donnée en paramètre.
   * @param userNbTagComment
   */
  public void setUserNbTagComment(long userNbTagComment) {
    this.userNbTagComment = userNbTagComment;
  }

  /**
   * Retourne le nombre de nodes 
   * contribués par cet utilisateur 
   * avant le processus d'anonymisation.
   * @return originalUserNbNode
   */
  public long getOriginalUserNbNode() {
    return originalUserNbNode;
  }

  /**
   * Retourne le nombre de ways 
   * contribués par cet utilisateur 
   * avant le processus d'anonymisation.
   * @return originalUserNbWay
   */
  public long getOriginalUserNbWay() {
    return originalUserNbWay;
  }

  /**
   * Retourne le nombre de relations 
   * contribuées par cet utilisateur 
   * avant le processus d'anonymisation.
   * @return originalUserNbRelation
   */
  public long getOriginalUserNbRelation() {
    return originalUserNbRelation;
  }

  /**
   * Retourne le nombre de tags comment
   * ajoutés par cet utilisateur 
   * avant le processus d'anonymisation.
   * @return originalUserNbTagComment
   */
  public long getOriginalUserNbTagComment() {
    return originalUserNbTagComment;
  }

  @Override
  public String toString() {
    return "ContributorData [uId=" + uId + ", originalUserNbNode="
        + originalUserNbNode + ", originalUserNbWay=" + originalUserNbWay
        + ", originalUserNbRelation=" + originalUserNbRelation
        + ", originalUserNbTagComment=" + originalUserNbTagComment
        + ", userNbNode=" + userNbNode + ", userNbWay=" + userNbWay
        + ", userNbRelation=" + userNbRelation + ", userNbTagComment="
        + userNbTagComment + "]";
  }
}
