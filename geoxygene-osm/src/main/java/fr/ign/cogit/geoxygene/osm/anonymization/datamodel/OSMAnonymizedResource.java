package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMEditType;
import fr.ign.cogit.geoxygene.osm.schema.OsmCaptureTool;
import fr.ign.cogit.geoxygene.osm.schema.OsmSource;

/**
 * Classe représentant les méta-données d'un élément OpenStreetMap après
 * anonymisation
 * 
 * Les données n'étant pas destinées à être mise à jour, beaucoup d'attributs
 * sont immutables.
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedResource  {
  private AnonymizedPrimitiveGeomOSM geom;
  private long id;
  private int version;
  private Boolean visible;
  private OSMAnonymizedChangeSet changeset;
  private Map<String, String> tags;
  
  private OsmSource source = OsmSource.UNKNOWN;
  private OsmCaptureTool captureTool = OsmCaptureTool.UNKNOWN;
  private OSMEditType editType = OSMEditType.UNCLASSIFIED;
  
  private OSMAnonymizedObject differentVersions;

  /**
   * Constructeur initialisant tous les attributs de la classe
   * Les attributs objets peuvent être null
   * @param geom ne peut pas être null malgré que ce soit un attrbut
   *    objet, cela permet de faciliter certains traitements en 
   *    considérant que le type de primitive est défini
   * @param id
   * @param version
   * @param visible can be null
   * @param user can be null
   * @param changeset can be null
   * @param tags can be null
   * @param source can be null
   * @param captureTool can be null
   * @param editType can be null
   * @param versionPrecedente can be null
   * @param versionSuivante can be null
   */
  public OSMAnonymizedResource(AnonymizedPrimitiveGeomOSM geom, long id,
      int version, Boolean visible, OSMAnonymizedChangeSet changeset, 
      Map<String, String> tags, OsmSource source, 
      OsmCaptureTool captureTool, OSMEditType editType) {
    super();
    if(geom == null)
      throw new IllegalArgumentException("The given primitive can't be null");
    this.geom = geom;
    if(this.geom != null)
      this.geom.setObjet(this);
    
    this.id = id;
    this.version = version;
    this.visible = visible;
    this.changeset = changeset;
    
    // ajout de la resource courante dans le changeset
    this.changeset.getElementsModified().add(this);
    this.tags = tags;
    if(this.tags == null)
      tags = new HashMap<String, String>();
    if(source != null)
      this.source = source;
    if(captureTool != null)
      this.captureTool = captureTool;
    if(editType != null)
      this.editType = editType;
    
    differentVersions = null;
  }

  /**
   * Accesseur en lecture sur l'id de l'élément
   * @return
   */
  public long getId() {
    return id;
  }

  /**
   * Accesseur en écriture sur l'identifiant
   * @return
   */
  public void setId(long newId) {
    this.id = newId;
  }

  /**
   * Accesseur en lecture sur la primitive géographique
   * @return geom
   */
  public AnonymizedPrimitiveGeomOSM getGeom() {
    return geom;
  }

  /**
   * Accesseur en écriture sur la primitive géographique
   * référence la classe courante dans la primitive en même temps
   * @param geom
   */
  public void setGeom(AnonymizedPrimitiveGeomOSM geom) {
    this.geom = geom;
    if(this.geom != null)
      this.geom.setObjet(this);
  }

  /**
   * Accesseur en lecture sur la version
   * @return version
   */
  public int getVersion() {
    return version;
  }

  /**
   * Accesseur en écriture sur la version
   * @param version
   */
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Accesseur en lecture sur la visibilité
   * @return visible
   */
  public Boolean isVisible() {
    return visible;
  }

  /**
   * Accesseur en écriture sur la visibilité
   * @param visible
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Accesseur en lecture sur le changeset 
   * @return changeset
   */
  public OSMAnonymizedChangeSet getChangeset() {
    return changeset;
  }

  /**
   * Accesseur en écriture sur le changeset
   * @param changeset
   */
  public void setChangeset(OSMAnonymizedChangeSet changeset) {
    this.changeset = changeset;
  }

  /**
   * Accesseur en lecture et en écriture sur les tags
   * @return tags
   */
  public Map<String, String> getTags() {
    return tags;
  }

  /**
   * Accesseur en lecture sur la source
   * @return source
   */
  public OsmSource getSource() {
    return source;
  }

  /**
   * Accesseur en écriture sur la source
   * @param source
   */
  public void setSource(OsmSource source) {
    this.source = source;
  }

  /**
   * Accesseur en lecture sur l'outil de capture
   * @return captureTool
   */
  public OsmCaptureTool getCaptureTool() {
    return captureTool;
  }

  /**
   * Accesseur en écriture sur l'outil de capture
   * @param captureTool
   */
  public void setCaptureTool(OsmCaptureTool captureTool) {
    this.captureTool = captureTool;
  }

  /**
   * Accesseur en lecture sur le type d'édition
   * @return editType
   */
  public OSMEditType getEditType() {
    return editType;
  }

  /**
   * Accesseur en écriture sur le type d'édition
   * @param editType
   */
  public void setEditType(OSMEditType editType) {
    this.editType = editType;
  }
  
  public OSMAnonymizedObject getDifferentVersions() {
    return differentVersions;
  }

  public void setDifferentVersions(OSMAnonymizedObject differentVersions) {
    this.differentVersions = differentVersions;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return (int) id;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    OSMAnonymizedResource other = (OSMAnonymizedResource) obj;
    if (id != other.id)
      return false;
    if (version != other.version)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Id=" + id + ", Version=" + version + ", Visible=" + visible + 
        ", changeset=" + changeset.getIdChangeset() + "\n" + geom;
  }
  
  
 
}

/*
 * public class OSMResou
 * 
 *  * 
 * @Override public String toString() { return "id: " + id + " v." + version; }
 * 
 * public void addTag(String cle, String valeur) { tags.put(cle, valeur); }
 * 
 * public boolean isFeature() { 
 * if (!(this.geom instanceof OSMNode)) return
 * true; if (this.tags.size() == 0) return false; return true; }
 * 
 * public void writeToPostGIS(Connection connection, String queryOSM) {
 * Statement stat; try { stat = connection.createStatement();
 * stat.executeQuery(queryOSM);
 * 
 * } catch (SQLException e) { // do nothing // e.printStackTrace(); }
 * 
 * } }
 */
