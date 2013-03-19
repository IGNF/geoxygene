package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.Map;

/**
 * Prise en compte de l'orientation des arcs sur le terrain (sens de
 * circulation). Si true : on suppose tous les arcs en double sens. Si false:
 * on suppose tous les arcs en sens unique, celui défini par la géométrie. NB:
 * ne pas confondre cette orientation 'géographique réelle', avec
 * l'orientation de la géométrie.
 * 
 * Utile ensuite pour l'appariement des arcs.
 */
public class ParamDirectionNetworkDataMatching {
  
  private boolean populationsArcsAvecOrientationDouble;
  private String attributOrientation1;
  private String attributOrientation2;
  private Map<Object, Integer> orientationMap1 = null;
  private Map<Object, Integer> orientationMap2 = null;

  /**
   * Constructor
   */
  public ParamDirectionNetworkDataMatching() {
    populationsArcsAvecOrientationDouble = true;
    attributOrientation1 = "orientation";
    attributOrientation2 = "orientation";
  }
  
  public boolean getPopulationsArcsAvecOrientationDouble() {
    return populationsArcsAvecOrientationDouble;
  }
  
  public void setPopulationsArcsAvecOrientationDouble(boolean b) {
    populationsArcsAvecOrientationDouble = b;
  }
  
  public String getAttributOrientation1() {
    return attributOrientation1;
  }
  
  public void setAttributOrientation1(String attributOrientation) {
    attributOrientation1 = attributOrientation;
  }
  
  public String getAttributOrientation2() {
    return attributOrientation2;
  }
  
  public void setAttributOrientation2(String attributOrientation) {
    attributOrientation2 = attributOrientation;
  }
  
  
  public Map<Object, Integer> getOrientationMap1() {
    return orientationMap1;
  }
  
  public void setOrientationMap1 (Map<Object, Integer> mapOrientation) {
    orientationMap1 = mapOrientation;
  }
  
  public Map<Object, Integer> getOrientationMap2() {
    return orientationMap2;
  }
  
  public void setOrientationMap2 (Map<Object, Integer> mapOrientation) {
    orientationMap2 = mapOrientation;
  }
}
