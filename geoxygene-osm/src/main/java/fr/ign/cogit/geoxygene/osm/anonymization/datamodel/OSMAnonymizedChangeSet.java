package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.AnonymizedDate;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Classe permettant de stocker les informations sur les 
 * changesets d'OpenStreetMap que l'on peut obtenir 
 * à partir de la base des changesets ou des informations 
 * provenant des fichiers diffs.
 * 
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedChangeSet {
  
  private long idChangeset;
  private OSMAnonymizedContributor user;
  private AnonymizedDate creationDate;
  private Date timeOpened;
  private Boolean open; 
  private Integer commentsCount;
  private Map<String, String> tags;
  private List<OSMAnonymizedResource> elementsModified;
  private OSMQualityAssessment evaluation;
  
  /**
   * Constructeur initialisant les attributs avec les 
   * valeurs données en paramètres
   * @param idChangeset
   * @param user
   * @param creationDate
   * @param timeOpened
   * @param open
   * @param commentsCount
   */
  public OSMAnonymizedChangeSet(long idChangeset,
      OSMAnonymizedContributor user, AnonymizedDate creationDate,
      Date timeOpened, Boolean open, Integer commentsCount) {
    this.idChangeset = idChangeset;
    this.user = user;
    this.user.getEditSet().add(this);
    this.creationDate = creationDate;
    this.timeOpened = timeOpened;
    this.open = open;
    this.commentsCount = commentsCount;
    this.tags = new HashMap<String, String>();
    this.elementsModified = new ArrayList<OSMAnonymizedResource>();
  }
  
  /**
   * Accesseur en lecture sur l'identifiant
   * @return idChangeset
   */
  public long getIdChangeset() {
    return idChangeset;
  }

  /**
   * Accesseur en écriture sur l'identifiant
   * @param idChangeset
   */
  public void setIdChangeset(long idChangeset) {
    this.idChangeset = idChangeset;
  }

  /**
   * Accesseur en lecture sur le contributeur OSM
   * @return user
   */
  public OSMAnonymizedContributor getUser() {
    return user;
  }

  /**
   * Accesseur en écriture sur le contributeur OSM
   * @param user
   */
  public void setUser(OSMAnonymizedContributor user) {
    this.user = user;
  }

  /**
   * Accesseur en lecture sur la date de création
   * @return creationDate
   */
  public AnonymizedDate getCreationDate() {
    return creationDate;
  }
  
  /**
   * Accesseur en écriture sur la date de création
   * @param creationDate
   */
  public void setCreationDate(AnonymizedDate creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Accesseur en lecture sur le temps d'ouverture 
   * @return timeOpened
   */
  public Date getTimeOpened() {
    return timeOpened;
  }
  
  /**
   * Accesseur en lecture indiquant si le 
   * changeset est ouvert
   * @return open
   */
  public Boolean isOpen() {
    return open;
  }

  /**
   * Accesseur en écriture sur le booleen open
   * @param open
   */
  public void setOpen(boolean open) {
    this.open = open;
  }

  /**
   * Accesseur en lecture sur commentsCount
   * renvoi -1 si commentsCount est null
   * @return commentsCount
   */
  public int getCommentsCount() {
    return commentsCount == null ? -1 : commentsCount;
  }

  /**
   * Accesseur en écriture sur commentsCount
   * Prend des int en argument car on considère 
   * que cette méthode ne doit pas être utiliser
   * pour mettre la variable à null
   * @param commentsCount
   */
  public void setCommentsCount(int commentsCount) {
    this.commentsCount = commentsCount;
  }

  /**
   * Accesseur en lecture et écriture sur le 
   * tableau des tags
   * @return tags
   */
  public Map<String, String> getTags() {
    return tags;
  }

  /**
   * Accesseur en lecture et écriture sur la liste 
   * d'éléments modifiés au cours du changeset
   * @return elementsModified
   */
  public List<OSMAnonymizedResource> getElementsModified() {
    return elementsModified;
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
  public void setEvaluation(OSMQualityAssessment evaluation) {
    this.evaluation = evaluation;
  }
  
  /*
   * Source: http://mathforum.org/library/drmath/view/63767.html
   * Now we can lay aside the question of terminology and consider your 
   * figure, whatever we call it. I'll call it a lat-long rectangle. I 
   * helped a student with the same problem some time ago. We started with 
   * the formula for the area of the earth between a line of latitude and 
   * the north pole (the area of a spherical cap, listed in the Dr. Math 
   * FAQ on Geometric Formulas).
   *
   *   A = 2*pi*R*h
   *
   * where R is the radius of the earth and h is the perpendicular distance 
   * from the plane containing the line of latitude to the pole. We can 
   * calculate h using trigonometry as
   *
   *   h = R*(1-sin(lat))
   *
   * Thus the area north of a line of latitude is
   *
   *   A = 2*pi*R^2(1-sin(lat))
   *
   * The area between two lines of latitude is the difference between the 
   * area north of one latitude and the area north of the other latitude:
   *
   *   A = |2*pi*R^2(1-sin(lat2)) - 2*pi*R^2(1-sin(lat1))|
   *     = 2*pi*R^2 |sin(lat1) - sin(lat2)|
   *
   * The area of a lat-long rectangle is proportional to the difference in 
   * the longitudes. The area I just calculated is the area between 
   * longitude lines differing by 360 degrees. Therefore the area we seek 
   * is
   *
   *   A = 2*pi*R^2 |sin(lat1)-sin(lat2)| |lon1-lon2|/360
   *     = (pi/180)R^2 |sin(lat1)-sin(lat2)| |lon1-lon2|
   *
   * - Doctor Rick, The Math Forum
   */
  /**
   * Calcule la superficie approximative du rectangle 
   * englobant le changeset.
   * @return la superfice approximative
   */
  public double getSurfaceArea()
  {
    // TODO a tester, le calcul de la superficie marche, je sais pas pour la lecture du changeset
    final double earthRadius = 6371.0;
    double minLon = 0.0, maxLon = 0.0, minLat = 0.0, maxLat = 0.0;
    OSMAnonymizedNode node;
    OSMAnonymizedWay way;
    // lecture des éléments du changeset pour chercher les
    // coordonnnées extreme afin d'être utilisées dans le calcul.
    for(OSMAnonymizedResource res : elementsModified) {
      AnonymizedPrimitiveGeomOSM geom = res.getGeom();
      if(geom.getOSMPrimitiveType() == OSMPrimitiveType.node) {
        node = (OSMAnonymizedNode) geom;
        minLat = Math.min(minLat, node.getLat());
        minLon = Math.min(minLon, node.getLon());
        maxLat = Math.max(maxLat, node.getLat());
        maxLon = Math.max(maxLon, node.getLon());
      } else if (geom.getOSMPrimitiveType() == OSMPrimitiveType.way) {
        way = (OSMAnonymizedWay) geom;
        for(OSMAnonymizedObject obj : way.getComposedOf()) {
          node = (OSMAnonymizedNode) obj.getVersion(Collections.max(obj.getVersionKeySet())).getGeom();
          minLat = Math.min(minLat, node.getLat());
          minLon = Math.min(minLon, node.getLon());
          maxLat = Math.max(maxLat, node.getLat());
          maxLon = Math.max(maxLon, node.getLon());
        }
      }
    }
    // calcul de l'aire suivant la formule en commentaire ci-dessus.
    double area = Math.PI/180* earthRadius * earthRadius;
    area *= Math.abs(Math.sin(minLat*Math.PI/180)-Math.sin(maxLat*Math.PI/180));
    area *= Math.abs(maxLon - minLon);
    
    return area;
  }
}
