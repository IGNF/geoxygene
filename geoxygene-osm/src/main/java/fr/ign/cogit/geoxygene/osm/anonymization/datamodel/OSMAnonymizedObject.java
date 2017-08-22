package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Classe qui permet de représenter l'ensemble 
 * des versions existantes d'un élément OSM.
 * 
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedObject {
  private OSMPrimitiveType elementType;
  private long id;
  private Map<Integer, OSMAnonymizedResource> elementVersions;
  
  public OSMAnonymizedObject(OSMAnonymizedResource origin) {
    elementVersions = new HashMap<>();
    elementVersions.put(origin.getVersion(), origin);
    id = origin.getId();
    elementType = origin.getGeom().getOSMPrimitiveType();
    
    origin.setDifferentVersions(this);
  }

  public OSMPrimitiveType getElementType() {
    return elementType;
  }
  
  public void addNewVersion(OSMAnonymizedResource newVersion) {
    if(newVersion.getId() != id || 
        newVersion.getGeom().getOSMPrimitiveType() != elementType)
      throw new IllegalArgumentException("Id or type mismatch:\n"+
        "id expected: "+id+", found: "+newVersion.getId()+
        " ; type expected: "+elementType+", found: "+
        newVersion.getGeom().getOSMPrimitiveType());
    
    elementVersions.put(newVersion.getVersion(), newVersion);
    newVersion.setDifferentVersions(this);
  }
  
  public OSMAnonymizedResource getVersion(int version) {
    return elementVersions.get(version);
  }
  
  public OSMAnonymizedResource getPossibleVersion(long idAndVersion) {
    String idRel = ""+idAndVersion;
    String actualId = this.id+"";
    if(!idRel.startsWith(actualId))
      return null;
    String version = idRel.replaceFirst(actualId, "");
    return elementVersions.get(Integer.parseInt(version));
  }
  
  public Set<Integer> getVersionKeySet() {
    return elementVersions.keySet();
  }
  
  public boolean containsVersion(int version) {
    return elementVersions.containsKey(version);
  }
  
  /**
   * Renvoie vrai si cette objet contient une version
   * qui peut correspondre à la concatenation 
   * d'un id et d'une version.
   * @param idAndVersion
   * @return
   */
  public boolean hasPossibleVersion(long idAndVersion) {
    String idRel = ""+idAndVersion;
    String actualId = this.id+"";
    if(!idRel.startsWith(actualId))
      return false;
    String version = idRel.replaceFirst(actualId, "");
    return elementVersions.containsKey(Integer.parseInt(version));
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public int getVersionsCount() {
    return elementVersions.size();
  }

  @Override
  public String toString() {
    String retour = "Type=" + elementType + ", id=" + id+"\n";
    for(Integer key : elementVersions.keySet())
      retour += elementVersions.get(key)+"\n";
    retour.substring(0, retour.length()-2);
    return retour;
  }
  
  
}
