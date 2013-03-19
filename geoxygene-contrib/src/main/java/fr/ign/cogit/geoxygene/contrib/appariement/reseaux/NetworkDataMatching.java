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
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.sql.Time;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultatAppariement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;

/**
 * 
 * 
 *
 */
public class NetworkDataMatching {
  
  /** logger. */
  private static final Logger LOGGER = Logger.getLogger(NetworkDataMatching.class.getName());

  /**
   * Appariement de réseaux.
   * @param paramApp, les paramètres de l'appariement
   * @return
   */
  public static ResultatAppariement networkDataMatching(final ParametresApp paramApp) {
    
    ResultatAppariement resultatAppariement = new ResultatAppariement();
    
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("");
      LOGGER.info("NETWORK MATCHING START");
      LOGGER.info("1 = least detailled data; 2 = most detailled data");
      LOGGER.info("");
    }
    
    // //////////////////////////////////////////////
    // STRUCTURATION
    if (LOGGER.isInfoEnabled()) {
      LOGGER
          .info(I18N.getString("AppariementIO.DataStructuring")); //$NON-NLS-1$
      LOGGER.info(I18N
          .getString("AppariementIO.TopologicalStructuing")); //$NON-NLS-1$
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N
          .getString("AppariementIO.StructuringStart" //$NON-NLS-1$
          ) + " " + (new Time(System.currentTimeMillis())).toString());
      LOGGER.debug(I18N
          .getString("AppariementIO.Network1Creation" //$NON-NLS-1$
          ) + " " + (new Time(System.currentTimeMillis())).toString());
    }
    
    ReseauApp reseauRef = AppariementIO.importData(paramApp, true);
    
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N
          .getString("AppariementIO.Network2Creation" //$NON-NLS-1$
          ) + " " + (new Time(System.currentTimeMillis())).toString());
    }
    
    ReseauApp reseauComp = AppariementIO.importData(paramApp, false);
    

//    resultatAppariement.setReseauRef(reseauRef);
//    resultatAppariement.setReseauComp(reseauComp);
//    
//    if (true) return resultatAppariement;

    // NB: l'ordre dans lequel les projections sont faites n'est pas neutre
    if (paramApp.projeteNoeuds2SurReseau1) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(I18N
            .getString("AppariementIO.ProjectionOfNetwork2OnNetwork1" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
      }
      reseauRef.projete(reseauComp,
          paramApp.projeteNoeuds2SurReseau1DistanceNoeudArc,
          paramApp.projeteNoeuds2SurReseau1DistanceProjectionNoeud,
          paramApp.projeteNoeuds2SurReseau1ImpassesSeulement);
    }
    if (paramApp.projeteNoeuds1SurReseau2) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(I18N
            .getString("AppariementIO.ProjectionOfNetwork1OnNetwork2" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
      }
      reseauComp.projete(reseauRef,
          paramApp.projeteNoeuds1SurReseau2DistanceNoeudArc,
          paramApp.projeteNoeuds1SurReseau2DistanceProjectionNoeud,
          paramApp.projeteNoeuds1SurReseau2ImpassesSeulement);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N
          .getString("AppariementIO.AttributeFilling" //$NON-NLS-1$
          ) + (new Time(System.currentTimeMillis())).toString());
    }
    reseauRef.instancieAttributsNuls(paramApp);
    reseauComp.initialisePoids();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(I18N
          .getString("AppariementIO.StructuringFinished")); //$NON-NLS-1$
      LOGGER.info(I18N.getString("AppariementIO.Network1") //$NON-NLS-1$
          + reseauRef.getPopArcs().size()
          + I18N.getString("AppariementIO.Edges") //$NON-NLS-1$
          + reseauRef.getPopNoeuds().size()
          + I18N.getString("AppariementIO.Nodes")); //$NON-NLS-1$
      LOGGER.info(I18N.getString("AppariementIO.Network2") //$NON-NLS-1$
          + reseauComp.getPopArcs().size()
          + I18N.getString("AppariementIO.Edges") //$NON-NLS-1$
          + reseauComp.getPopNoeuds().size()
          + I18N.getString("AppariementIO.Nodes")); //$NON-NLS-1$
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N.getString("AppariementIO.StructuringEnd") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    
    // --------------------------------------------------------------------------------------
    // APPARIEMENT
    // --------------------------------------------------------------------------------------
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(""); //$NON-NLS-1$
      LOGGER
          .info(I18N.getString("AppariementIO.NetworkMatching")); //$NON-NLS-1$
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N
          .getString("AppariementIO.NetworkMatchingStart") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    resultatAppariement = Appariement.appariementReseaux(reseauRef, reseauComp, paramApp);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(I18N
          .getString("AppariementIO.NetworkMatchingFinished")); //$NON-NLS-1$
      LOGGER.info("  " + resultatAppariement.getLinkDataSet().size() + I18N.getString(//$NON-NLS-1$
          "AppariementIO.MatchingLinksFound")); //$NON-NLS-1$
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N
          .getString("AppariementIO.NetworkMatchingEnd") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    
    // --------------------------------------------------------------------------------------
    // EXPORT
    // --------------------------------------------------------------------------------------
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(""); //$NON-NLS-1$
      LOGGER.info(I18N.getString("AppariementIO.Conclusion")); //$NON-NLS-1$
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N.getString("AppariementIO.ExportStart") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    if (paramApp.debugBilanSurObjetsGeo) {
      // FIXME : perturbations liées au nouveau output non maitrisées ici.
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(I18N
            .getString("AppariementIO.LinkTransformation") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      EnsembleDeLiens liensGeneriques = LienReseaux.exportLiensAppariement(
          resultatAppariement.getLinkDataSet(), reseauRef, paramApp);
      Appariement.nettoyageLiens(reseauRef, reseauComp);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(I18N.getString("AppariementIO.MatchingEnd")); //$NON-NLS-1$
      }
      resultatAppariement.setLinkDataSet(liensGeneriques);
      // FIXME : stats dans ce cas sont-elles les mêmes ?
      return resultatAppariement;
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(I18N.getString("AppariementIO.LinkGeometry") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    LienReseaux.exportAppCarteTopo(resultatAppariement.getLinkDataSet(), paramApp);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(I18N.getString("AppariementIO.MatchingEnd")); //$NON-NLS-1$
    }
    
    resultatAppariement.setReseauRef(reseauRef);
    resultatAppariement.setReseauComp(reseauComp);
    
    // return liens;
    return resultatAppariement;
  }
  
  
}
