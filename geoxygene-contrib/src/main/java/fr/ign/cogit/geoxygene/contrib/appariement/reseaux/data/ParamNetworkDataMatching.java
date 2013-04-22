/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paramDirectionNetwork1",
    "paramDirectionNetwork2",
    "paramDistance",
    "paramTopoNetwork1",
    "paramTopoNetwork2",
    "paramProjNetwork1",
    "paramProjNetwork2"
})
@XmlRootElement(name = "ParamNetworkDataMatching")
public class ParamNetworkDataMatching {
  
  @XmlElement(name = "ParamDirectionNetwork1")
  private ParamDirectionNetworkDataMatching paramDirectionNetwork1 = null;
  
  @XmlElement(name = "ParamDirectionNetwork2")
  private ParamDirectionNetworkDataMatching paramDirectionNetwork2 = null;
  
  @XmlElement(name = "ParamDistance")
  private ParamDistanceNetworkDataMatching paramDistance = null;
  
  @XmlElement(name = "ParamTopoNetwork1")
  private ParamTopologyTreatmentNetwork paramTopoNetwork1 = null;
  
  @XmlElement(name = "ParamTopoNetwork2")
  private ParamTopologyTreatmentNetwork paramTopoNetwork2 = null;
  
  @XmlElement(name = "ParamProjNetwork1")
  private ParamProjectionNetworkDataMatching paramProjNetwork1 = null;
  
  @XmlElement(name = "ParamProjNetwork2")
  private ParamProjectionNetworkDataMatching paramProjNetwork2 = null;

  /** A classic logger. */
  static Logger logger = Logger.getLogger(ParamNetworkDataMatching.class.getName());
  
  /**
   * Constructor.
   */
  public ParamNetworkDataMatching() {
    paramDirectionNetwork1 = new ParamDirectionNetworkDataMatching();
    paramDirectionNetwork2 = new ParamDirectionNetworkDataMatching();
    paramDistance = new ParamDistanceNetworkDataMatching();
    paramTopoNetwork1 = new ParamTopologyTreatmentNetwork();
    paramTopoNetwork2 = new ParamTopologyTreatmentNetwork();
    paramProjNetwork1 = new ParamProjectionNetworkDataMatching();
    paramProjNetwork2 = new ParamProjectionNetworkDataMatching();
  }
  
  public ParamDirectionNetworkDataMatching getParamDirectionNetwork1() {
    return paramDirectionNetwork1;
  }
  
  public void setParamDirectionNetwork1(ParamDirectionNetworkDataMatching pdnm) {
    paramDirectionNetwork1 = pdnm;
  }
  
  public ParamDirectionNetworkDataMatching getParamDirectionNetwork2() {
    return paramDirectionNetwork2;
  }
  
  public void setParamDirectionNetwork2(ParamDirectionNetworkDataMatching pdnm) {
    paramDirectionNetwork2 = pdnm;
  }
  
  public ParamDistanceNetworkDataMatching getParamDistance() {
    return paramDistance;
  }
  
  public void setParamDistance(ParamDistanceNetworkDataMatching paramDistance) {
    this.paramDistance = paramDistance;
  }
  
  public ParamTopologyTreatmentNetwork getParamTopoNetwork1() {
    return paramTopoNetwork1;
  }
  
  public void setParamTopoNetwork1(ParamTopologyTreatmentNetwork paramTopo) {
    this.paramTopoNetwork1 = paramTopo;
  }
  
  public ParamTopologyTreatmentNetwork getParamTopoNetwork2() {
    return paramTopoNetwork2;
  }
  
  public void setParamTopoNetwork2(ParamTopologyTreatmentNetwork paramTopo) {
    this.paramTopoNetwork2 = paramTopo;
  }
  
  public void setParamProjNetwork1(ParamProjectionNetworkDataMatching paramProj) {
    paramProjNetwork1 = paramProj;
  }
  
  public ParamProjectionNetworkDataMatching getParamProjNetwork1() {
    return paramProjNetwork1;
  }
  
  public void setParamProjNetwork2(ParamProjectionNetworkDataMatching paramProj) {
    paramProjNetwork2 = paramProj;
  }
  
  public ParamProjectionNetworkDataMatching getParamProjNetwork2() {
    return paramProjNetwork2;
  }
  
  /**
   * Transform new structure to old structure.
   * @return ParametresApp
   * @deprecated
   */
  public ParametresApp paramNDMToParamApp() {
    
    // Create old object paramApp
    ParametresApp param = new ParametresApp();
    
    // Set parameters
    
    // Set direction param
    param.populationsArcsAvecOrientationDouble = paramDirectionNetwork1.getOrientationDouble();
    param.attributOrientation1 = paramDirectionNetwork1.getAttributOrientation();
    param.attributOrientation2 = paramDirectionNetwork2.getAttributOrientation();
    if (paramDirectionNetwork1.getOrientationMap() != null) {
      Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(OrientationInterface.SENS_DIRECT), OrientationInterface.SENS_DIRECT);
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(OrientationInterface.DOUBLE_SENS), OrientationInterface.DOUBLE_SENS);
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(OrientationInterface.SENS_INVERSE), OrientationInterface.SENS_INVERSE);
      param.orientationMap1 = orientationMap1;
    } else {
      param.orientationMap1 = null;
    }
    if (paramDirectionNetwork2.getOrientationMap() != null) {
      Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(OrientationInterface.SENS_DIRECT), OrientationInterface.SENS_DIRECT);
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(OrientationInterface.DOUBLE_SENS), OrientationInterface.DOUBLE_SENS);
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(OrientationInterface.SENS_INVERSE), OrientationInterface.SENS_INVERSE);
      param.orientationMap2 = orientationMap2;
    } else {
      param.orientationMap2 = null;
    }
    
    // Ecarts de distance autorisés
    param.distanceArcsMax = paramDistance.getDistanceArcsMax(); 
    param.distanceArcsMin = paramDistance.getDistanceArcsMin(); 
    param.distanceNoeudsMax = paramDistance.getDistanceNoeudsMax(); 
    param.distanceNoeudsImpassesMax = paramDistance.getDistanceNoeudsImpassesMax(); 
    
    // Set topo treatment param
    param.topologieGraphePlanaire1 = paramTopoNetwork1.getGraphePlanaire();
    param.topologieFusionArcsDoubles1 = paramTopoNetwork1.getFusionArcsDoubles();
    param.topologieSeuilFusionNoeuds1 = paramTopoNetwork1.getSeuilFusionNoeuds();
    param.topologieSurfacesFusionNoeuds1 = paramTopoNetwork1.getSurfacesFusionNoeuds();
    param.topologieElimineNoeudsAvecDeuxArcs1 = paramTopoNetwork1.getElimineNoeudsAvecDeuxArcs();
    
    param.topologieGraphePlanaire2 = paramTopoNetwork2.getGraphePlanaire();
    param.topologieFusionArcsDoubles2 = paramTopoNetwork2.getFusionArcsDoubles();
    param.topologieSeuilFusionNoeuds2 = paramTopoNetwork2.getSeuilFusionNoeuds();
    param.topologieSurfacesFusionNoeuds2 = paramTopoNetwork2.getSurfacesFusionNoeuds();
    param.topologieElimineNoeudsAvecDeuxArcs2 = paramTopoNetwork2.getElimineNoeudsAvecDeuxArcs();
    
    // Projection
    param.projeteNoeuds1SurReseau2 = paramProjNetwork1.getProjeteNoeuds1SurReseau2();
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = paramProjNetwork1.getProjeteNoeuds1SurReseau2DistanceNoeudArc(); // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = paramProjNetwork1.getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(); // 50
    param.projeteNoeuds1SurReseau2ImpassesSeulement = paramProjNetwork1.getProjeteNoeuds1SurReseau2ImpassesSeulement();
    
    param.projeteNoeuds2SurReseau1 = paramProjNetwork2.getProjeteNoeuds1SurReseau2();;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = paramProjNetwork2.getProjeteNoeuds1SurReseau2DistanceNoeudArc(); // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = paramProjNetwork2.getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(); // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = paramProjNetwork2.getProjeteNoeuds1SurReseau2ImpassesSeulement();
    
    // Variante
    param.varianteForceAppariementSimple = false;  // true
    param.varianteRedecoupageArcsNonApparies = false;   // true
    param.varianteFiltrageImpassesParasites = false;
    
    // Debug
    param.debugTirets = true;
    param.debugBilanSurObjetsGeo = false;
    param.debugAffichageCommentaires = 2;
    param.debugBuffer = true;
    param.debugPasTirets = 10;
    
    return param;
  }
  
  
  /**
   * Display all parameters (value or info)
   */
  public String toString() {
    
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("Parameters : " + "<br/>");
    
    // Direction
    buffer.append("Paramètres de prise en compte de l'orientation des arcs sur le terrain : " + "<br/>");
    buffer.append("   - orientation double = " + paramDirectionNetwork1.getOrientationDouble() + "<br/>");
    buffer.append("   - attribut de l'orientation simple du réseau 1 = " + paramDirectionNetwork1.getAttributOrientation() + "<br/>");
    buffer.append("   - attribut de l'orientation simple du réseau 2 = " + paramDirectionNetwork2.getAttributOrientation() + "<br/>");
    if (paramDirectionNetwork1.getOrientationMap() != null) {
      buffer.append("   - valeurs de l'attribut d'orientation du réseau 1 = " + "<br/>");
      for (Object key : paramDirectionNetwork1.getOrientationMap().keySet()) {
        buffer.append("      " + key + ", " + paramDirectionNetwork1.getOrientationMap().get(key) + "<br/>");
      }
    }
    if (paramDirectionNetwork2.getOrientationMap() != null) {
      buffer.append("   - valeurs de l'attribut d'orientation du réseau 2 = " + "<br/>");
      for (Object key : paramDirectionNetwork2.getOrientationMap().keySet()) {
        buffer.append("      " + key + ", " + paramDirectionNetwork2.getOrientationMap().get(key) + "<br/>");
      }
    }
    
    // Distance
    buffer.append("Écarts de distance autorisés : " + "<br/>");
    buffer.append("   - Distance maximale autorisée entre deux noeuds appariés = " + paramDistance.getDistanceNoeudsMax() + "<br/>");
    buffer.append("   - Distance maximum autorisée entre les arcs des deux réseaux = " + paramDistance.getDistanceArcsMax() + "<br/>");
    buffer.append("   - Distance minimum sous laquelle l'écart de distance pour divers arcs du réseaux 2 n'a plus aucun sens = " 
        + paramDistance.getDistanceArcsMin() + "<br/>");
    buffer.append("   - Distance maximale autorisée entre deux noeuds appariés, quand le noeud du réseau 1 est une impasse uniquement = " 
        + paramDistance.getDistanceNoeudsImpassesMax() + "<br/>");
    
    // Topology 1
    
    // Topology 2
    
    // Projection 1
    
    // Projection 2
    
    return buffer.toString();
  }
  
  /**
   * Load the parameters from the specified stream.
   * 
   * @param stream stream to load the parameters from
   * @return the parameters loaded from the specified stream
   */
  /*public static ResultNetworkDataMatching unmarshall(InputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(ResultNetworkDataMatching.class);
      Unmarshaller m = context.createUnmarshaller();
      ResultNetworkDataMatching parametresAppData = (ResultNetworkDataMatching) m.unmarshal(stream);
      return parametresAppData;
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new ResultNetworkDataMatching();
  }*/
  
  /**
   * Load the parameters. 
   * If file does not exist, create new empty XML.
   * 
   * @param fileName XML parameter file to load
   * @return ParametresAppData loaded
   */
  /*public static ResultNetworkDataMatching unmarshall(String fileName) {
    try {
      return ResultNetworkDataMatching.unmarshall(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      //ResultNetworkDataMatching.LOGGER
       //   .error("File " + fileName + " could not be read");
      return new ResultNetworkDataMatching();
    }
  }*/
  
}
