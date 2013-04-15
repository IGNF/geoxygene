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

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paramDirectionNetwork1",
    "paramDirectionNetwork2",
    "paramDistance"
})
@XmlRootElement(name = "ParamNetworkDataMatching")
public class ParamNetworkDataMatching {
  
  @XmlElement(name = "ParamDirectionNetwork1")
  private ParamDirectionNetworkDataMatching paramDirectionNetwork1 = null;
  
  @XmlElement(name = "ParamDirectionNetwork2")
  private ParamDirectionNetworkDataMatching paramDirectionNetwork2 = null;
  
  @XmlElement(name = "ParamDistance")
  private ParamDistanceNetworkDataMatching paramDistance = null;
  
  // private ParamTopoTreatmentNetworkDataMatching paramTopoTreatment = null;
  

  /** A classic logger. */
  static Logger logger = Logger.getLogger(ParamNetworkDataMatching.class.getName());
  
  /**
   * Constructor.
   */
  public ParamNetworkDataMatching() {
    
    paramDirectionNetwork1 = new ParamDirectionNetworkDataMatching();
    paramDirectionNetwork2 = new ParamDirectionNetworkDataMatching();
    paramDistance = new ParamDistanceNetworkDataMatching();
    // paramTopoTreatment = new ParamTopoTreatmentNetworkDataMatching();
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
  
  /*public ParamFilenameNetworkDataMatching getParamDataset() {
    return paramDataset;
  }
  
  public void setParamDataset(ParamFilenameNetworkDataMatching paramDataset) {
    this.paramDataset = paramDataset;
  }*/
  
  public ParamDistanceNetworkDataMatching getParamDistance() {
    return paramDistance;
  }
  
  public void setParamDistance(ParamDistanceNetworkDataMatching paramDistance) {
    this.paramDistance = paramDistance;
  }
  
  /*public ParamTopoTreatmentNetworkDataMatching getParamTopoTreatment() {
    return paramTopoTreatment;
  }
  
  public void setParamTopoTreatment(ParamTopoTreatmentNetworkDataMatching paramTopoTreatment) {
    this.paramTopoTreatment = paramTopoTreatment;
  }*/
  
  /**
   * Transform new structure to old structure.
   * @return ParametresApp
   */
  public ParametresApp paramNDMToParamApp() {
    
    // Create old object paramApp
    ParametresApp param = new ParametresApp();
    
    // Set parameters
    
    // Set dataset
    //param.populationsArcs1 = paramDataset.getPopulationsArcs1();
    //param.populationsArcs2 = paramDataset.getPopulationsArcs2();
    //param.populationsNoeuds1 = paramDataset.getPopulationsNoeuds1();
    //param.populationsNoeuds2 = paramDataset.getPopulationsNoeuds2();
    
    // Set direction param
    param.populationsArcsAvecOrientationDouble = paramDirectionNetwork1.getOrientationDouble();
    param.attributOrientation1 = paramDirectionNetwork1.getAttributOrientation();
    param.attributOrientation2 = paramDirectionNetwork2.getAttributOrientation();
    
    
    if (paramDirectionNetwork1.getOrientationMap() != null) {
      Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(1), 1);
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(2), 2);
      orientationMap1.put(paramDirectionNetwork1.getOrientationMap().get(-1), -1);
      param.orientationMap1 = orientationMap1;
    } else {
      param.orientationMap1 = null;
    }
    if (paramDirectionNetwork2.getOrientationMap() != null) {
      Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(1), 1);
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(2), 2);
      orientationMap2.put(paramDirectionNetwork2.getOrientationMap().get(-1), -1);
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
    param.topologieGraphePlanaire1 = false;
    param.topologieGraphePlanaire2 = false;
    param.topologieFusionArcsDoubles1 = false;
    param.topologieFusionArcsDoubles2 = false;
    param.topologieSeuilFusionNoeuds2 = 0.1;
    param.topologieSeuilFusionNoeuds1 = 0.1;
    param.topologieSurfacesFusionNoeuds1 = null;
    param.topologieSurfacesFusionNoeuds2 = null;
    param.topologieElimineNoeudsAvecDeuxArcs1 = false;
    param.topologieElimineNoeudsAvecDeuxArcs2 = false;
    
    // A trier encore
    param.varianteFiltrageImpassesParasites = false;
    param.projeteNoeuds1SurReseau2 = false;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1 = false;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    param.varianteForceAppariementSimple = true;
    param.varianteRedecoupageArcsNonApparies = true;
    
    param.debugTirets = true;
    param.debugBilanSurObjetsGeo = false;
    param.varianteRedecoupageArcsNonApparies = true;
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
    buffer.append("-----------------------------------------------------------" + "\n");
    buffer.append("Parameters : " + "\n");
    
    // Dataset
    /*buffer.append("Paramètres spécifiant quelles données sont traitées : " + "\n");
    buffer.append("   - Nombre de collection dans le réseau 1 : " + paramDataset.getPopulationsArcs1().size() + "\n");
    for (int i = 0; i < paramDataset.getPopulationsArcs1().size(); i++) {
      buffer.append("   - Nombre d'arcs de la " + i + "-ème collection du réseau 1 = " + paramDataset.getPopulationsArcs1().get(i).size() + "\n");
    }
    buffer.append("   - Nombre de collection dans le réseau 2 : " + paramDataset.getPopulationsArcs2().size() + "\n");
    for (int i = 0; i < paramDataset.getPopulationsArcs2().size(); i++) {
      buffer.append("   - Nombre d'arcs de la " + i + "-ème collection du réseau 2 = " + paramDataset.getPopulationsArcs2().get(i).size() + "\n");
    }*/
    
    // Direction
    buffer.append("Paramètres de prise en compte de l'orientation des arcs sur le terrain : " + "\n");
    buffer.append("   - orientation double = " + paramDirectionNetwork1.getOrientationDouble() + "\n");
    buffer.append("   - attribut de l'orientation simple du réseau 1 = " + paramDirectionNetwork1.getAttributOrientation() + "\n");
    buffer.append("   - attribut de l'orientation simple du réseau 2 = " + paramDirectionNetwork2.getAttributOrientation() + "\n");
    if (paramDirectionNetwork1.getOrientationMap() != null) {
      buffer.append("   - valeurs de l'attribut d'orientation du réseau 1 = " + "\n");
      for (Object key : paramDirectionNetwork1.getOrientationMap().keySet()) {
        buffer.append("      " + key + ", " + paramDirectionNetwork1.getOrientationMap().get(key) + "\n");
      }
    }
    if (paramDirectionNetwork2.getOrientationMap() != null) {
      buffer.append("   - valeurs de l'attribut d'orientation du réseau 2 = " + "\n");
      for (Object key : paramDirectionNetwork2.getOrientationMap().keySet()) {
        buffer.append("      " + key + ", " + paramDirectionNetwork2.getOrientationMap().get(key) + "\n");
      }
    }
    
    // Distance
    buffer.append("Écarts de distance autorisés : " + "\n");
    buffer.append("   - Distance maximale autorisée entre deux noeuds appariés = " + paramDistance.getDistanceNoeudsMax() + "\n");
    buffer.append("   - Distance maximum autorisée entre les arcs des deux réseaux = " + paramDistance.getDistanceArcsMax() + "\n");
    buffer.append("   - Distance minimum sous laquelle l'écart de distance pour divers arcs du réseaux 2 n'a plus aucun sens = " 
        + paramDistance.getDistanceArcsMin() + "\n");
    buffer.append("   - Distance maximale autorisée entre deux noeuds appariés, quand le noeud du réseau 1 est une impasse uniquement = " 
        + paramDistance.getDistanceNoeudsImpassesMax() + "\n");
    
    // Topology treatment
    
    buffer.append("-----------------------------------------------------------" + "\n");
    
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
