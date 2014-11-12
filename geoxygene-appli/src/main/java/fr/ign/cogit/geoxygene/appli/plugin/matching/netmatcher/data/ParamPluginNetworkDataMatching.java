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
package fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;


/**
 * 
 * - Paramètres de l'appariement
 * - Données pour l'appariement
 * - Actions : recalage, transfert des attributs, ...
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paramFilenameNetwork1",
    "paramFilenameNetwork2",
    "doRecalage",
    "doLinkExport",
    "paramNetworkDataMatching"
})
@XmlRootElement(name = "ParamPluginNetworkDataMatching")
public class ParamPluginNetworkDataMatching {
  
  /** Fichiers qui contiennent les shapes du réseau 1. */
  @XmlElement(name = "ParamFilenameNetwork1")
  private ParamFilenamePopulationEdgesNetwork paramFilenameNetwork1 = null;
  
  /** Fichiers qui contiennent les shapes du réseau 2. */
  @XmlElement(name = "ParamFilenameNetwork2")
  private ParamFilenamePopulationEdgesNetwork paramFilenameNetwork2 = null;
  
  /** Paramètres pour l'appariement. */
  @XmlElement(name = "ParamNetworkDataMatching")
  private ParametresApp paramNetworkDataMatching = null;
  
  /** Action du recalage à faire en sortie de l'appariement. */
  @XmlElement(name = "DoRecalage")
  private boolean doRecalage = false;
  
  /** Export des liens d'appariement. */
  @XmlElement(name = "DoLinkExport")
  private boolean doLinkExport = false;
  
  /**
   * Default constructor.
   */
  public ParamPluginNetworkDataMatching() {
    paramFilenameNetwork1 = new ParamFilenamePopulationEdgesNetwork();
    paramFilenameNetwork2 = new ParamFilenamePopulationEdgesNetwork();
    // paramNetworkDataMatching = new ParametresApp();
    doRecalage = false;
  }
  
  public ParamFilenamePopulationEdgesNetwork getParamFilenameNetwork1() {
    return paramFilenameNetwork1;
  }
  
  public ParamFilenamePopulationEdgesNetwork getParamFilenameNetwork2() {
    return paramFilenameNetwork2;
  }
  
  public ParametresApp getParamNetworkDataMatching() {
    return paramNetworkDataMatching;
  }
  
  public void setParamFilenameNetwork1(ParamFilenamePopulationEdgesNetwork pf) {
    paramFilenameNetwork1 = pf;
  }
  
  public void setParamFilenameNetwork2(ParamFilenamePopulationEdgesNetwork pf) {
    paramFilenameNetwork2 = pf;
  }
  
  public void setParamNetworkDataMatching(ParametresApp p) {
    paramNetworkDataMatching = p;
  }

  public void setDoRecalage(boolean b) {
    doRecalage = b;
  }
  
  public boolean getDoRecalage() {
    return doRecalage;
  }

  public void setDoLinkExport(boolean b) {
    doLinkExport = b;
  }
    
  public boolean getDoLinkExport() {
    return doLinkExport;
  }
}
