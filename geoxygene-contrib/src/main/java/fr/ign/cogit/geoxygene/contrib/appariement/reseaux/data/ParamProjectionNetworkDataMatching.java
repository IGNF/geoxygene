package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "projeteNoeuds1SurReseau2",
    "projeteNoeuds1SurReseau2DistanceNoeudArc",
    "projeteNoeuds1SurReseau2DistanceProjectionNoeud",
    "projeteNoeuds1SurReseau2ImpassesSeulement"
})
@XmlRootElement(name = "ParamProjectionNetworkDataMatching")
public class ParamProjectionNetworkDataMatching {
  
  /**
   * Doit on projeter les noeuds du réseau 1 sur le réseau 2 pour découper ce
   * dernier ? Ce traitement réalise un surdécoupage du réseau 2 qui facilite
   * l'appariement dans certains cas (par exemple si les réseaux ont des niveaux
   * de détail proches), mais qui va aussi un peu à l'encontre de la philosophie
   * générale du processus d'appariement. A utiliser avec modération donc.
   */
  @XmlElement(name = "ProjeteNoeuds1SurReseau2")
  public boolean projeteNoeuds1SurReseau2;

  /**
   * Distance max de la projection des noeuds 2 sur le réseau 1. Utile
   * uniquement si projeteNoeuds1SurReseau2 = true.
   */
  @XmlElement(name = "ProjeteNoeuds1SurReseau2DistanceNoeudArc")
  public double projeteNoeuds1SurReseau2DistanceNoeudArc;

  /**
   * Distance min entre la projection d'un noeud sur un arc et les extrémités de
   * cet arc pour créer un nouveau noeud sur le réseau 2. Utile uniquement si
   * projeteNoeuds1SurReseau2 = true.
   */
  @XmlElement(name = "ProjeteNoeuds1SurReseau2DistanceProjectionNoeud")
  public double projeteNoeuds1SurReseau2DistanceProjectionNoeud;

  /**
   * Si true: on ne projete que les impasses du réseau 1 sur le réseau 2 Si
   * false: on projete tous les noeuds du réseau 1 sur le réseau 2. Utile
   * uniquement si projeteNoeuds1SurReseau2 = true.
   */
  @XmlElement(name = "ProjeteNoeuds1SurReseau2ImpassesSeulement")
  public boolean projeteNoeuds1SurReseau2ImpassesSeulement;
  
  /**
   * Default constructor.
   */
  public ParamProjectionNetworkDataMatching() {
    projeteNoeuds1SurReseau2 = false;
    projeteNoeuds1SurReseau2DistanceNoeudArc = 0;
    projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;
    projeteNoeuds1SurReseau2ImpassesSeulement = false;
  }
  
  public boolean getProjeteNoeuds1SurReseau2() {
    return projeteNoeuds1SurReseau2;
  }
  
  public void setProjeteNoeuds1SurReseau2(boolean b) {
    projeteNoeuds1SurReseau2 = b;
  }
  
  public double getProjeteNoeuds1SurReseau2DistanceNoeudArc() {
    return projeteNoeuds1SurReseau2DistanceNoeudArc;
  }
  
  public void setProjeteNoeuds1SurReseau2DistanceNoeudArc(double d) {
    projeteNoeuds1SurReseau2DistanceNoeudArc = d;
  }
  
  public double getProjeteNoeuds1SurReseau2DistanceProjectionNoeud() {
    return projeteNoeuds1SurReseau2DistanceProjectionNoeud;
  }
  
  public void setProjeteNoeuds1SurReseau2DistanceProjectionNoeud(double d) {
    projeteNoeuds1SurReseau2DistanceProjectionNoeud = d;
  }
  
  public boolean getProjeteNoeuds1SurReseau2ImpassesSeulement() {
    return projeteNoeuds1SurReseau2ImpassesSeulement;
  }
  
  public void setProjeteNoeuds1SurReseau2ImpassesSeulement(boolean b) {
    projeteNoeuds1SurReseau2ImpassesSeulement = b;
  }

}
