/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.agent.micro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionEvent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentAlignement;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneAgregee;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author Julien Perret
 *
 */
@Entity
public class AgentBatiment extends AgentGeographique {
  static Logger logger = Logger.getLogger(AgentBatiment.class.getName());

  /**
   * Constructeur.
   */
  public AgentBatiment() {
    super();
    this.representationClass = Batiment.class;
    this.representationClassString = representationClass.getName();
    AgentGeographiqueCollection collection = AgentGeographiqueCollection
        .getInstance();
    collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
        FeatureCollectionEvent.Type.ADDED));
  }

  /**
   * Constructeur.
   */
  public AgentBatiment(int idGeo) {
    super(idGeo, Batiment.class);
    AgentGeographiqueCollection collection = AgentGeographiqueCollection
        .getInstance();
    collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
        FeatureCollectionEvent.Type.ADDED));
  }

  private AgentUniteBatie uniteBatie;

  /**
   * Renvoie la valeur de l'attribut uniteBatie.
   * @return la valeur de l'attribut uniteBatie
   */
  @Transient
  public AgentUniteBatie getUniteBatie() {
    return this.uniteBatie;
  }

  /**
   * Affecte la valeur de l'attribut uniteBatie.
   * @param uniteBatie l'attribut uniteBatie à affecter
   */
  public void setUniteBatie(AgentUniteBatie uniteBatie) {
    this.uniteBatie = uniteBatie;
  }

  // private AgentZoneElementaireBatie zoneElementaireBatie;

  /**
   * Renvoie la valeur de l'attribut zoneElementaire.
   * @return la valeur de l'attribut zoneElementaire
   */
  @Transient
  public AgentZoneElementaireBatie getZoneElementaireBatie() {
    if (this.getGroupeBatiments() == null) {
      logger.error("groupe NULL !!!");
      return null;
    }
    return this.getGroupeBatiments().getZoneElementaireBatie();
  }

  // public AgentZoneElementaireBatie getZoneElementaireBatie() {return
  // this.zoneElementaireBatie;}

  /**
   * Affecte la valeur de l'attribut zoneElementaire.
   * @param zoneElementaire l'attribut zoneElementaire à affecter
   */
  // public void setZoneElementaireBatie(AgentZoneElementaireBatie
  // zoneElementaire) {this.zoneElementaireBatie = zoneElementaire;}

  private AgentZoneAgregee zoneAgregee;

  /**
   * Renvoie la valeur de l'attribut zoneAgregee.
   * @return la valeur de l'attribut zoneAgregee
   */
  @Transient
  public AgentZoneAgregee getZoneAgregee() {
    return this.zoneAgregee;
  }

  /**
   * Affecte la valeur de l'attribut zoneAgregee.
   * @param zoneAgregee l'attribut zoneAgregee à affecter
   */
  public void setZoneAgregee(AgentZoneAgregee zoneAgregee) {
    this.zoneAgregee = zoneAgregee;
  }

  /**
   * Route la plus proche du bâtiment.
   */
  private AgentTroncon routeLaPlusProche;

  /**
   * @return routeLaPlusProche route la plus proche du bâtiment
   */
  @Transient
  public AgentTroncon getRouteLaPlusProche() {
    return this.routeLaPlusProche;
  }

  /**
   * @param routeLaPlusProche route la plus proche du bâtiment
   */
  public void setRouteLaPlusProche(AgentTroncon routeLaPlusProche) {
    AgentTroncon routePPPrecedent = this.routeLaPlusProche;
    this.routeLaPlusProche = routeLaPlusProche;
    if (((routePPPrecedent == null) && (this.routeLaPlusProche != null))
        || ((routePPPrecedent != null) && ((this.routeLaPlusProche == null) || !routePPPrecedent
            .equals(this.routeLaPlusProche)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "RouteLaPlusProche",
          routePPPrecedent, this.routeLaPlusProche));
    }
  }
  /**
   * Calcul de la route la plus proche du bâtiment
   */
  public void calculRouteLaPlusProche() {
    double distanceMin = Double.MAX_VALUE;
    for (AgentTroncon troncon : this.getZoneElementaireBatie().getTroncons()) {
      if ((AgentTronconRoute.class.isAssignableFrom(troncon.getClass()))
          || (AgentTronconChemin.class.isAssignableFrom(troncon.getClass()))) {
        double distance = this.getGeom().distance(troncon.getGeom());
        if (distance < distanceMin) {
          distanceMin = distance;
          this.setRouteLaPlusProche(troncon);
          this.setDistanceRouteLaPlusProche(distanceMin);
        }
      }
    }
  }

  /**
   * distance à la route la plus proche du bâtiment
   */
  double distanceRouteLaPlusProche = Double.MAX_VALUE;

  /**
   * @param distanceRouteLaPlusProche distance à la route la plus proche du bâtiment
   */
  public void setDistanceRouteLaPlusProche(double distance) {
    double distancePrecedente = this.distanceRouteLaPlusProche;
    this.distanceRouteLaPlusProche = distance;
    if (distancePrecedente != this.distanceRouteLaPlusProche) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "DistanceRouteLaPlusProche",
          distancePrecedente, this.distanceRouteLaPlusProche));
    }
  }

  /**
   * @return distanceRouteLaPlusProche distance à la route la plus proche du bâtiment
   */
  public double getDistanceRouteLaPlusProche() {
    return distanceRouteLaPlusProche;
  }

  private AgentGroupeBatiments groupeBatiments = null;

  /**
   * @return la valeur de l'attribut groupeBatiments
   */
  @ManyToOne
  public AgentGroupeBatiments getGroupeBatiments() {
    return this.groupeBatiments;
  }

  /**
   * @param groupeBatiments l'attribut groupeBatiments à affecter
   */
  public void setGroupeBatiments(AgentGroupeBatiments groupeBatiments) {
    AgentGroupeBatiments groupeBatiPrecedent = this.groupeBatiments;
    this.groupeBatiments = groupeBatiments;
    if (((groupeBatiPrecedent == null) && (this.groupeBatiments != null))
        || ((groupeBatiPrecedent != null) && ((this.groupeBatiments == null) || !groupeBatiPrecedent
            .equals(this.groupeBatiments)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "GroupeBatiments",
          groupeBatiPrecedent, this.groupeBatiments));
    }
  }

  private AgentAlignement alignement = null;

  /**
   * @return la valeur de l'attribut alignement
   */
  @ManyToOne
  public AgentAlignement getAlignement() {
    return this.alignement;
  }

  /**
   * @param alignement l'attribut groupeBatiments à affecter
   */
  public void setAlignement(AgentAlignement alignement) {
    this.alignement = alignement;
  }

  /**
   * Type fonctionnel du bâtiment.
   */
  protected int typeFonctionnel = TypeFonctionnel.Quelconque;

  /**
   * @return typeFonctionnel type fonctionnel du bâtiment
   */
  public int getTypeFonctionnel() {
    return typeFonctionnel;
  }

  /**
   * @param typeFonctionnel type fonctionnel du bâtiment
   */
  public void setTypeFonctionnel(int typeFonctionnel) {
    this.typeFonctionnel = typeFonctionnel;
  }

  @Override
  public ElementRepresentation construireRepresentationCourante() {
    Batiment batiment = (Batiment) super.construireRepresentationCourante();
    if (this.getGroupeBatiments() != null) {
      batiment.setGroupeBatiments((GroupeBatiments) this.getGroupeBatiments().getRepresentationCourante());
    } else {
      logger.error("pas de groupe pour l'agent " + this);
    }
    batiment.setTypeFonctionnel(this.getTypeFonctionnel());
    batiment.setDistanceBatimentLePlusProche(this
        .getDistanceBatimentLePlusProche());
    if (this.getBatimentLePlusProche() != null) {
      batiment.setBatimentLePlusProche((Batiment) this
          .getBatimentLePlusProche().getRepresentationCourante());
    }
    batiment.setDistanceTronconLePlusProche(this
        .getDistanceTronconLePlusProche());
    if (this.getTronconLePlusProche() != null) {
      batiment.setTronconLePlusProche((Troncon) this.getTronconLePlusProche()
          .getRepresentationCourante());
    }
    return batiment;
  }

  @Override
  public void prendreAttributsRepresentation(
      ElementRepresentation representation) {
    super.prendreAttributsRepresentation(representation);
    Batiment batiment = (Batiment) representation;
    this.setTypeFonctionnel(batiment.getTypeFonctionnel());
    // if (batiment.getZoneElementaireUrbaine()==null) {
    // logger.error("le batiment "+batiment+" n'a pas de zone élémentaire");
    // } else {
    // if (batiment.getZoneElementaireUrbaine().getAgentGeographique()==null) {
    // logger.error(batiment.getZoneElementaireUrbaine()+" n'a pas d'agent");
    // } else {
    // this.setZoneElementaireBatie((AgentZoneElementaireBatie)
    // batiment.getZoneElementaireUrbaine().getAgentGeographique());
    // }
    // }

    // Route la plus proche
    if (batiment.getRouteLaPlusProche() != null) {
      this.setRouteLaPlusProche((AgentTronconRoute) batiment
          .getRouteLaPlusProche().getAgentGeographique());
      // FIXME doit on stocker cette distance dans la représentation ?
      this.setDistanceRouteLaPlusProche(this.getGeom().distance(
          batiment.getRouteLaPlusProche().getGeom()));
    }
    // troncon le plus proche
    this.setDistanceTronconLePlusProche(batiment
        .getDistanceTronconLePlusProche());
    if (batiment.getTronconLePlusProche() != null) {
      this.setTronconLePlusProche((AgentTroncon) batiment
          .getTronconLePlusProche().getAgentGeographique());
    } else {
      this.setTronconLePlusProche(null);
    }
    // batiment le plus proche
    this.setDistanceBatimentLePlusProche(batiment
        .getDistanceBatimentLePlusProche());
    if (batiment.getBatimentLePlusProche() != null) {
      this.setBatimentLePlusProche((AgentBatiment) batiment
          .getBatimentLePlusProche().getAgentGeographique());
    } else {
      this.setBatimentLePlusProche(null);
    }
    // GB le plus proche
    if (batiment.getGroupeBatiments() != null) {
      this.setGroupeBatiments((AgentGroupeBatiments) batiment
          .getGroupeBatiments().getAgentGeographique());
    } else {
      logger
          .error("le batiment " + batiment + " n'a pas de groupe de batiment");
    }
  }

  /**
   * @return
   */
  public double getSurfaceBatimentsIntersectes() {
    if (this.getZoneElementaireBatie() == null) {
      return 0;
    }
    double surfaceBatimentsIntersectes = 0;
    for (AgentGroupeBatiments groupeBati : this.getZoneElementaireBatie()
        .getGroupesBatiments()) {
      for (AgentBatiment batiment : groupeBati.getBatiments()) {
        if (!batiment.equals(this)) {
          IGeometry intersection = batiment.getGeom().intersection(getGeom());
          if (intersection != null) {
            surfaceBatimentsIntersectes += intersection.area();
          }
        }
      }
    }
    if (logger.isTraceEnabled()) {
      logger.trace("SurfaceBatimentsIntersectes = "
          + surfaceBatimentsIntersectes);
    }
    return surfaceBatimentsIntersectes;
  }

  /**
   * @return
   */
  public double getSurfaceDepassement() {
    if (this.getZoneElementaireBatie() == null) {
      return 0;
    }
    IGeometry difference = getGeom().difference(
        this.getZoneElementaireBatie().getGeom());
    if (difference != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("SurfaceDepassement = " + difference.area());
      }
      return difference.area();
    }
    return 0;
  }

  /**
   * @return
   */
  public double getDifferenceTaille() {
    AgentZoneElementaireBatie agentZoneElementaireBatie = this
        .getGroupeBatiments().getZoneElementaireBatie();
    double surfaceBatiments = 0.0;
    int nbBatiments = 0;
    for (AgentBatiment batiment : agentZoneElementaireBatie.getBatiments()) {
      if (!batiment.equals(this)) {
        surfaceBatiments += batiment.getGeom().area();
        nbBatiments++;
      }
    }
    double tailleMoyenneBatiments = surfaceBatiments / nbBatiments;
    if (logger.isDebugEnabled()) {
      logger.debug("tailleMoyenneBatiments = " + tailleMoyenneBatiments);
    }
    double differenceTaille = tailleMoyenneBatiments - this.getGeom().area();
    return differenceTaille;
  }

  /**
   * groupe de bâtiments le plus proche
   */
  private AgentGroupeBatiments groupeBatimentsLePlusProche;

  /**
   * @return groupeBatimentsLePlusProche groupe de bâtiments le plus proche
   */
  @Transient
  public AgentGroupeBatiments getGroupeBatimentsLePlusProche() {
    return this.groupeBatimentsLePlusProche;
  }

  /**
   * @param groupeBatimentsLePlusProche groupe de bâtiments le plus proche
   */
  public void setGroupeBatimentsLePlusProche(
      AgentGroupeBatiments groupeBatimentsLePlusProche) {
    AgentGroupeBatiments groupeBatiPPPrecedent = this.groupeBatimentsLePlusProche;
    this.groupeBatimentsLePlusProche = groupeBatimentsLePlusProche;
    if (((groupeBatiPPPrecedent == null) && (this.groupeBatimentsLePlusProche != null))
        || ((groupeBatiPPPrecedent != null) && ((this.groupeBatimentsLePlusProche == null) || !groupeBatiPPPrecedent
            .equals(this.groupeBatimentsLePlusProche)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "GroupeBatimentsLePlusProche",
          groupeBatiPPPrecedent, this.groupeBatimentsLePlusProche));
    }
  }

  /**
   * distance au groupe de bâtiments le plus proche
   */
  double distanceGroupeBatimentsLePlusProche = Double.MAX_VALUE;

  /**
   * @return distanceGroupeBatimentsLePlusProche distance au groupe de bâtiments
   *         le plus proche
   */
  public double getDistanceGroupeBatimentsLePlusProche() {
    return distanceGroupeBatimentsLePlusProche;
  }

  /**
   * @param distanceGroupeBatimentsLePlusProche distance au groupe de bâtiments
   *          le plus proche
   */
  public void setDistanceGroupeBatimentsLePlusProche(double distance) {
    double distancePrecedente = this.distanceGroupeBatimentsLePlusProche;
    distanceGroupeBatimentsLePlusProche = distance;
    if (distancePrecedente != this.distanceGroupeBatimentsLePlusProche) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED,
          "DistanceGroupeBatimentsLePlusProche", distancePrecedente,
          this.distanceGroupeBatimentsLePlusProche));
    }
  }

  /**
   * Calcul du groupe de bâtiments le plus proche du bâtiment
   */
  public void calculGroupeBatimentsLePlusProche() {
    double distanceMinGroupe = Double.MAX_VALUE;
    for (AgentGroupeBatiments groupeBati : this.getZoneElementaireBatie()
        .getGroupesBatiments()) {
      if (!groupeBati.equals(this.getGroupeBatiments())) {
        double distance = this.getGeom().distance(groupeBati.getGeom());
        if (distance < distanceMinGroupe) {
          distanceMinGroupe = distance;
          this.setDistanceGroupeBatimentsLePlusProche(distance);
          this.setGroupeBatimentsLePlusProche(groupeBati);
        }
      }
    }
  }

  /**
   * Troncon le plus proche du bâtiment.
   */
  private AgentTroncon tronconLePlusProche = null;

  /**
   * @return tronconLePlusProche troncon le plus proche du bâtiment
   */
  @Transient
  public AgentTroncon getTronconLePlusProche() {
    return this.tronconLePlusProche;
  }

  /**
   * @param tronconLePlusProche troncon le plus proche du bâtiment
   */
  public void setTronconLePlusProche(AgentTroncon tronconLePlusProche) {
    AgentTroncon tronconPPPrecedent = this.tronconLePlusProche;
    this.tronconLePlusProche = tronconLePlusProche;
    if (((tronconPPPrecedent == null) && (this.tronconLePlusProche != null))
        || ((tronconPPPrecedent != null) && ((this.tronconLePlusProche == null) || !tronconPPPrecedent
            .equals(this.tronconLePlusProche)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "TronconLePlusProche",
          tronconPPPrecedent, this.tronconLePlusProche));
    }
  }

  /**
   * Calcul du troncon le plus proche du bâtiment
   */
  public void calculTronconLePlusProche() {
    double distanceMin = Double.MAX_VALUE;
    for (AgentTroncon troncon : this.getZoneElementaireBatie().getTroncons()) {
      double distance = this.getGeom().distance(troncon.getGeom());
      if (distance < distanceMin) {
        distanceMin = distance;
        this.setTronconLePlusProche(troncon);
        this.setDistanceTronconLePlusProche(distanceMin);
      }
    }
  }

  /**
   * distance au troncon le plus proche du bâtiment
   */
  double distanceTronconLePlusProche = Double.MAX_VALUE;

  /**
   * @return distanceTronconLePlusProche distance au troncon le plus proche du
   *         bâtiment
   */
  public double getDistanceTronconLePlusProche() {
    return distanceTronconLePlusProche;
  }

  /**
   * @param distanceTronconLePlusProche distance au troncon le plus proche du
   *          bâtiment
   */
  public void setDistanceTronconLePlusProche(double distance) {
    double distancePrecedente = this.distanceTronconLePlusProche;
    distanceTronconLePlusProche = distance;
    if (distancePrecedente != this.distanceTronconLePlusProche) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "DistanceTronconLePlusProche",
          distancePrecedente, this.distanceTronconLePlusProche));
    }
  }

  /**
   * Troncon Parallele le plus proche du bâtiment.
   */
  private AgentTroncon tronconParalleleLePlusProche = null;

  /**
   * @return tronconParalleLePlusProche troncon Parallele le plus proche du
   *         bâtiment
   */
  @Transient
  public AgentTroncon getTronconParalleleLePlusProche() {
    return this.tronconParalleleLePlusProche;
  }

  /**
   * @param tronconParalleleLePlusProche troncon Parallele le plus proche du
   *          bâtiment
   */
  public void setTronconParalleleLePlusProche(
      AgentTroncon tronconParalleleLePlusProche) {
    AgentTroncon tronconPPPrecedent = this.tronconParalleleLePlusProche;
    this.tronconParalleleLePlusProche = tronconParalleleLePlusProche;
    if (((tronconPPPrecedent == null) && (this.tronconParalleleLePlusProche != null))
        || ((tronconPPPrecedent != null) && ((this.tronconParalleleLePlusProche == null) || !tronconPPPrecedent
            .equals(this.tronconParalleleLePlusProche)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "TronconParalleleLePlusProche",
          tronconPPPrecedent, this.tronconParalleleLePlusProche));
    }
  }

  /**
   * Calcul du troncon parallele le plus proche du bâtiment
   */
  public void calculTronconParalleleLePlusProche() {
    double distanceMin = Double.MAX_VALUE;
    for (AgentTroncon troncon : this.getZoneElementaireBatie().getTroncons()) {
      // Orientation du tronçon
      ILineString geometrieTroncon = (ILineString) troncon.getGeom();
      double orientationTroncon = JtsUtil.projectionPointOrientationTroncon(
          this.getGeom().centroid(), geometrieTroncon);
      // Orientation du bâtiment
      Polygon polygon = null;
      try {
        polygon = (Polygon) AdapterFactory.toGeometry(new GeometryFactory(),
            this.getGeom());
        if (polygon != null) {
          // Détermination de l'angle de rotation du bâtiment
          double orientationBatiment = MesureOrientationV2
              .getOrientationGenerale(polygon);
          // Si la valeur est égale à 999.9 ça peut vouloir dire que le batiment
          // est carré
          if (orientationBatiment == 999.9) {
            // dans ce cas on utilise les murs du batiment
            MesureOrientationFeuille mesureOrientation = new MesureOrientationFeuille(
                polygon, Math.PI * 0.5);
            double orientationCotes = mesureOrientation
                .getOrientationPrincipale();
            if (orientationCotes != -999.9) {
              orientationBatiment = orientationCotes;
            }
          }
          // On Détermine l'angle entre le troncon et le bâtiment
          double angle = orientationTroncon - orientationBatiment;
          if (Math.abs(angle) < Math.PI / 6) {
            double distance = this.getGeom().distance(troncon.getGeom());
            if (distance < distanceMin) {
              distanceMin = distance;
              this.setTronconParalleleLePlusProche(troncon);
              this.setDistanceTronconParalleleLePlusProche(distanceMin);
            }
          }
        }
      } catch (Exception e) {
        logger.error("Erreur sur le bâtiment : " + this.getGeom());
        logger.error(e.getCause());
        return;
      }
    }
  }

  /**
   * distance au troncon Parallele le plus proche du bâtiment
   */
  double distanceTronconParalleleLePlusProche = Double.MAX_VALUE;

  /**
   * @return distanceTronconParalleleLePlusProche distance au troncon Parallele
   *         le plus proche du bâtiment
   */
  public double getDistanceTronconParalleleLePlusProche() {
    return distanceTronconParalleleLePlusProche;
  }

  /**
   * @param distanceTronconParalleleLePlusProche distance au troncon Parallele
   *          le plus proche du bâtiment
   */
  public void setDistanceTronconParalleleLePlusProche(double distance) {
    double distancePrecedente = this.distanceTronconParalleleLePlusProche;
    distanceTronconParalleleLePlusProche = distance;
    if (distancePrecedente != this.distanceTronconParalleleLePlusProche) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED,
          "DistanceTronconParalleleLePlusProche", distancePrecedente,
          this.distanceTronconParalleleLePlusProche));
    }
  }

  public double getSatisfactionTronconParalleleLePlusProche() {
    AgentZoneElementaireBatie zoneElem = this.getZoneElementaireBatie();
    if (zoneElem == null) {
      logger.error("zoneElem NULL!!!");
      logger.error("agent " + this);
      logger.error(this.getGeom());
      return 100;
    }
    // On récupère la valeur du troncon le plus proche objectif dans la Méthode
    // de peuplement à appliquer
    String methodePeuplement = zoneElem.getMethodePeuplement();
    ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement
        .getInstance().getParametresMethodesPeuplement(methodePeuplement);

    // Calcul dmin et dmax
    // double distDefaut = 10;
    double distanceObjectifMin = -1.0; // distDefaut-(2*10/100);
    double distanceObjectifMax = -1.0; // distDefaut+(2*10/100);
    if (parametresPeuplement.getDistanceRoute().getMoyenne() != -1) {
      double distanceObjectifMoy = parametresPeuplement.getDistanceRoute()
          .getMoyenne();
      double ecartType = distanceObjectifMoy / 5;
      if (parametresPeuplement.getDistanceRoute().getEcartType() != -1) {
        ecartType = parametresPeuplement.getDistanceRoute().getEcartType();
      }
      distanceObjectifMin = distanceObjectifMoy - 2 * ecartType;
      distanceObjectifMax = distanceObjectifMoy + 2 * ecartType;
    } else if ((parametresPeuplement.getDistanceRoute().getMinimum() != -1)
        && (parametresPeuplement.getDistanceRoute().getMaximum() != -1)) {
      distanceObjectifMin = parametresPeuplement.getDistanceRoute()
          .getMinimum();
      distanceObjectifMax = parametresPeuplement.getDistanceRoute()
          .getMaximum();
    }
    // On calcule la satisfaction
    double satisfactionRP = 0;
    if (this.getTronconParalleleLePlusProche() != null) {
      double distance = this.getDistanceTronconParalleleLePlusProche();
      logger.debug("distance : " + distance);
      if ((distanceObjectifMin != -1) && (distanceObjectifMax != -1)) {
        if ((distance > distanceObjectifMax)
            || (distance < distanceObjectifMin)) {
          satisfactionRP = 100 - (Math.min(
              Math.abs(distance - distanceObjectifMax),
              Math.abs(distance - distanceObjectifMin)) * 5);
        } else {
          satisfactionRP = 100;
        }
      } else {
        satisfactionRP = 100;
      }
    } else {
      satisfactionRP = 100;
    }
    logger.debug("satisfaction distance Route Parallèle : " + satisfactionRP);

    return satisfactionRP;
  }

  public double getSatisfactionTronconLePlusProche() {
    AgentZoneElementaireBatie zoneElem = this.getZoneElementaireBatie();
    if (zoneElem == null) {
      logger.error("zoneElem NULL!!!");
      logger.error("agent " + this);
      logger.error(this.getGeom());
      return 100;
    }
    // On récupère la valeur du troncon le plus proche objectif dans la Méthode
    // de peuplement à appliquer
    String methodePeuplement = zoneElem.getMethodePeuplement();
    ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement
        .getInstance().getParametresMethodesPeuplement(methodePeuplement);

    // Calcul dmin et dmax
    // double distDefaut = 10;
    double distanceObjectifMin = -1.0; // distDefaut-(2*10/100);
    double distanceObjectifMax = -1.0; // distDefaut+(2*10/100);
    if (parametresPeuplement.getDistanceRoute().getMoyenne() != -1) {
      double distanceObjectifMoy = parametresPeuplement.getDistanceRoute()
          .getMoyenne();
      double ecartType = distanceObjectifMoy / 5;
      if (parametresPeuplement.getDistanceRoute().getEcartType() != -1) {
        ecartType = parametresPeuplement.getDistanceRoute().getEcartType();
      }
      distanceObjectifMin = distanceObjectifMoy - 2 * ecartType;
      distanceObjectifMax = distanceObjectifMoy + 2 * ecartType;
    } else {
      if ((parametresPeuplement.getDistanceRoute().getMinimum() != -1)
          && (parametresPeuplement.getDistanceRoute().getMaximum() != -1)) {
        distanceObjectifMin = parametresPeuplement.getDistanceRoute()
        .getMinimum();
        distanceObjectifMax = parametresPeuplement.getDistanceRoute()
        .getMaximum();
      }
    }

    // On calcule la satisfaction
    double satisfactionR = 0;
    if (this.getTronconLePlusProche() != null) {
      double distance = this.getDistanceTronconLePlusProche();
      logger.debug("distance : " + distance);
      if ((distanceObjectifMin != -1) && (distanceObjectifMax != -1)) {
        if ((distance > distanceObjectifMax)
            || (distance < distanceObjectifMin)) {
          satisfactionR = 100 - (Math.min(
              Math.abs(distance - distanceObjectifMax),
              Math.abs(distance - distanceObjectifMin)) * 5);
        } else {
          satisfactionR = 100;
        }
      } else {
        satisfactionR = 100;
      }
    } else {
      satisfactionR = 100;
    }
    logger.debug("satisfaction distance Route : " + satisfactionR);
    return satisfactionR;
  }

  public double getSatisfactionBatimentLePlusProche() {
    AgentZoneElementaireBatie zoneElem = this.getZoneElementaireBatie();
    if (zoneElem == null) {
      logger.error("zoneElem NULL!!!");
      logger.error("agent " + this);
      logger.error(this.getGeom());
      return 100;
    }
    // On récupère la valeur du batiment le plus proche objectif dans la Méthode
    // de peuplement à appliquer
    String methodePeuplement = zoneElem.getMethodePeuplement();
    ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement
        .getInstance().getParametresMethodesPeuplement(methodePeuplement);

    // Calcul dmin et dmax
    // double distDefaut = 10;
    double distanceObjectifMin = -1.0; // distDefaut-(2*10/100);
    double distanceObjectifMax = -1.0; // distDefaut+(2*10/100);
    if (parametresPeuplement.getDistanceBatiment().getMoyenne() != -1) {
      double distanceObjectifMoy = parametresPeuplement.getDistanceBatiment()
          .getMoyenne();
      double ecartType = distanceObjectifMoy / 5;
      if (parametresPeuplement.getDistanceBatiment().getEcartType() != -1) {
        ecartType = parametresPeuplement.getDistanceBatiment().getEcartType();
      }
      distanceObjectifMin = distanceObjectifMoy - 2 * ecartType;
      distanceObjectifMax = distanceObjectifMoy + 2 * ecartType;
    } else if ((parametresPeuplement.getDistanceBatiment().getMinimum() != -1)
        && (parametresPeuplement.getDistanceBatiment().getMaximum() != -1)) {
      distanceObjectifMin = parametresPeuplement.getDistanceBatiment()
          .getMinimum();
      distanceObjectifMax = parametresPeuplement.getDistanceBatiment()
          .getMaximum();
    }

    // On calcule la satisfaction
    double satisfactionB = 0;
    if (this.getBatimentLePlusProche() != null) {
      double distance = this.getDistanceBatimentLePlusProche();
      logger.debug("distance : " + distance);
      if ((distanceObjectifMin != -1) && (distanceObjectifMax != -1)) {
        if ((distance > distanceObjectifMax)
            || (distance < distanceObjectifMin)) {
          satisfactionB = 100 - (Math.min(
              Math.abs(distance - distanceObjectifMax),
              Math.abs(distance - distanceObjectifMin)) * 5);
        } else {
          satisfactionB = 100;
        }
      } else {
        satisfactionB = 100;
      }
    } else {
      satisfactionB = 100;
    }
    logger.debug("satisfaction distance Batiment : " + satisfactionB);

    // double distanceObjectif = 10;
    // if (parametresPeuplement.getDistanceBatiment().getMoyenne()!=-1){
    // distanceObjectif =
    // parametresPeuplement.getDistanceBatiment().getMoyenne();
    // }
    // On calcule la satisfaction
    // double satisfactionB = 0;
    // double erreurToleree = 1; // en pourcent
    // if (this.getBatimentLePlusProche()!=null){
    // double distance = this.getDistanceBatimentLePlusProche();
    // logger.debug("distance : "+distance);
    // double distanceObjectifMax =
    // distanceObjectif+((erreurToleree*distanceObjectif)/100);
    // double distanceObjectifMin =
    // distanceObjectif-((erreurToleree*distanceObjectif)/100);
    // if ((distance>distanceObjectifMax)||(distance<distanceObjectifMin)){
    // satisfactionB =
    // 100-(Math.min(Math.abs(distance-distanceObjectifMax),Math.abs(distance-distanceObjectifMin))*5);
    // }else{
    // satisfactionB = 100;
    // }
    // } else {
    // satisfactionB = 100;
    // }

    return satisfactionB;
  }

  public double getSatisfactionOrientation() {
    AgentZoneElementaireBatie zoneElem = this.getZoneElementaireBatie();
    if (zoneElem == null) {
      logger.error("zoneElem NULL!!!");
      logger.error("agent " + this);
      logger.error(this.getGeom());
      return 100;
    }
    // On récupère l'information sur l'orientation dans le Méthode de peuplement
    String methodePeuplement = zoneElem.getMethodePeuplement();
    ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement
        .getInstance().getParametresMethodesPeuplement(methodePeuplement);
    // On calcule la satisfaction
    double satisfactionO = 0;
    double erreurToleree = Math.PI / 180; // en radians
    if (parametresPeuplement.getParalleleRoute()) {
      // on cherche l'orientation du troncon le plus proche du centroid du
      // batiment
      if (this.getTronconLePlusProche() == null) {
        return satisfactionO = 100;
      }
      AgentTroncon tronconPP = this.getTronconLePlusProche();
      GM_LineString geometrieTroncon = (GM_LineString) tronconPP.getGeom();
      double orientationTroncon = JtsUtil.projectionPointOrientationTroncon(
          this.getGeom().centroid(), geometrieTroncon);
      logger.debug("orientT " + orientationTroncon);
      // On calcule l'élongation
      Polygon polygon = null;
      try {
        polygon = (Polygon) AdapterFactory.toGeometry(new GeometryFactory(),
            this.getGeom());
      } catch (Exception e) {
        logger.error("La géométrie du bâtiment n'est pas un polygone");
      }
      // double elongation = 0;
      double orientationBatiment = 0;
      boolean carre = false;
      double orientationGeneraleRoute = 0;
      if (polygon != null) {
        // elongation = JtsUtil.elongation(polygon);
        // logger.debug("elongation "+ elongation);
        // if (elongation>0.98){ // batiment quasi carré ou carré : orientation
        // principale des murs
        MesureOrientationFeuille mesureOrientation = new MesureOrientationFeuille(
            polygon, Math.PI * 0.5);
        orientationBatiment = mesureOrientation.getOrientationPrincipale();
        carre = true;
        orientationGeneraleRoute = orientationBatiment - orientationTroncon;
        if (orientationGeneraleRoute <= -0.5 * Math.PI)
          orientationGeneraleRoute += Math.PI * 0.5;
        else if (orientationGeneraleRoute > 0.5 * Math.PI)
          orientationGeneraleRoute -= Math.PI * 0.5;
        if (orientationGeneraleRoute <= -Math.PI / 4)
          orientationGeneraleRoute += Math.PI * 0.5;
        else if (orientationGeneraleRoute > Math.PI / 4)
          orientationGeneraleRoute -= Math.PI * 0.5;
        // }else{// sinon orientation générale du batiment
        // orientationBatiment =
        // MesureOrientationV2.getOrientationGenerale(polygon);
        // // Orientation générale du bâtiment par rapport à la route (en
        // radian, dans l'intervalle ]-Pi/2, Pi/2], par rapport a l'axe Ox)
        // orientationGeneraleRoute = orientationBatiment - orientationTroncon;
        // if (orientationGeneraleRoute<=-0.5*Math.PI) orientationGeneraleRoute
        // += Math.PI;
        // else if (orientationGeneraleRoute>0.5*Math.PI)
        // orientationGeneraleRoute -= Math.PI;
        // }
      }
      logger.debug("orientB " + orientationBatiment);
      // Calcul de la satisfaction
      if (Math.abs(orientationGeneraleRoute) < erreurToleree) {
        satisfactionO = 100;
      } else {
        if (carre) {
          satisfactionO = 100 - (Math.abs(orientationGeneraleRoute)
              / (Math.PI / 4) * 100);
        } else {
          satisfactionO = 100 - (Math.abs(orientationGeneraleRoute)
              / (Math.PI / 2) * 100);
        }
      }
    }
    logger.debug("satisf " + satisfactionO);
    return satisfactionO;
  }

  /**
   * bâtiment le plus proche du bâtiment.
   */
  protected AgentBatiment batimentLePlusProche;

  /**
   * @return batimentLePlusProche bâtiment le plus proche du bâtiment
   */
  @ManyToOne
  public AgentBatiment getBatimentLePlusProche() {
    return this.batimentLePlusProche;
  }

  /**
   * @param batimentLePlusProche bâtiment le plus proche du bâtiment
   */
  public void setBatimentLePlusProche(AgentBatiment batimentLePlusProche) {
    AgentBatiment batiPPPrecedent = this.batimentLePlusProche;
    this.batimentLePlusProche = batimentLePlusProche;
    if (((batiPPPrecedent == null) && (this.batimentLePlusProche != null))
        || ((batiPPPrecedent != null) && ((this.batimentLePlusProche == null) || !batiPPPrecedent
            .equals(this.batimentLePlusProche)))) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "BatimentLePlusProche",
          batiPPPrecedent, this.batimentLePlusProche));
    }
  }

  /**
   * distance au bâtiment le plus proche du bâtiment
   */
  double distanceBatimentLePlusProche = Double.MAX_VALUE;

  /**
   * @param distanceBatimentLePlusProche distance au bâtiment le plus proche du
   *          bâtiment
   */
  public void setDistanceBatimentLePlusProche(double distance) {
    double distancePrecedente = this.distanceBatimentLePlusProche;
    this.distanceBatimentLePlusProche = distance;
    if (distancePrecedente != this.distanceBatimentLePlusProche) {
      AgentGeographiqueCollection collection = AgentGeographiqueCollection
          .getInstance();
      collection.fireActionPerformed(new AgentCollectionEvent(collection, this,
          FeatureCollectionEvent.Type.CHANGED, "DistanceBatimentLePlusProche",
          distancePrecedente, this.distanceBatimentLePlusProche));
    }
  }

  /**
   * @return distanceBatimentLePlusProche distance au bâtiment le plus proche du
   *         bâtiment
   */
  public double getDistanceBatimentLePlusProche() {
    return this.distanceBatimentLePlusProche;
  }

  /**
   * Calcul du bâtiment le plus proche du bâtiment
   */
  public void calculBatimentLePlusProche() {
    double distanceMinBatiment = Double.MAX_VALUE;
    for (AgentBatiment batiment : this.getZoneElementaireBatie().getBatiments()) {
      double distance = batiment.getGeom().distance(this.getGeom());
      if ((distance < distanceMinBatiment) && (!batiment.equals(this))) {
        distanceMinBatiment = distance;
        this.setBatimentLePlusProche(batiment);
        this.setDistanceBatimentLePlusProche(distanceMinBatiment);
      }
    }
    // On vérifie pour tous les bâtiments que le nouveau bâtiment n'est pas
    // devenu le bâtiment le plus proche
    for (AgentBatiment batiment : this.getZoneElementaireBatie().getBatiments()) {
      double distance = batiment.getGeom().distance(this.getGeom());
      if ((distance < batiment.getDistanceBatimentLePlusProche())
          && (!batiment.equals(this))) {
        // On ajoute le batiment à la liste des agents modifiés
        logger.debug("le batiment " + batiment + " avant bati le pp à : "
            + batiment.getDistanceBatimentLePlusProche()
            + " après bati le pp à : " + distance);
        batiment.setBatimentLePlusProche(this);
        batiment.setDistanceBatimentLePlusProche(distance);
      }
    }
    // On vérifie pour les batiments ayant le nouveau bâtiment comme plus proche
    // voisin que ça n'est pas modifié
    for (AgentBatiment batiment : this.getZoneElementaireBatie().getBatiments()) {
      if (batiment.getBatimentLePlusProche() != null) {
        if (batiment.getBatimentLePlusProche().equals(this)) {
          double distanceMinBatiment2 = Double.MAX_VALUE;
          AgentBatiment batiLePP = null;
          for (AgentBatiment batiment2 : this.getZoneElementaireBatie()
              .getBatiments()) {
            double distance = batiment2.getGeom().distance(batiment.getGeom());
            if ((distance < distanceMinBatiment2)
                && (!batiment.equals(batiment2))) {
              distanceMinBatiment2 = distance;
              batiLePP = batiment2;
            }
          }
          if (((batiLePP != null) && (!batiLePP.equals(batiment
              .getBatimentLePlusProche())))
              || (Math.abs(distanceMinBatiment2
                  - batiment.getDistanceBatimentLePlusProche()) > 0.001)) {
            logger.debug("le batiment " + batiment + " avant bati le pp à : "
                + batiment.getDistanceBatimentLePlusProche()
                + " après bati le pp à : " + distanceMinBatiment2);
            batiment.setBatimentLePlusProche(batiLePP);
            batiment.setDistanceBatimentLePlusProche(distanceMinBatiment2);
          }
        }
      }
    }
  }

  // Mise à jour de groupe de bâtiments
  @SuppressWarnings("unchecked")
  public void miseAJourGB() {

    List<AgentGroupeBatiments> listeGBATester = new ArrayList<AgentGroupeBatiments>();
    AgentGroupeBatiments agentGroupeBatiments = this.getGroupeBatiments();
    if (logger.isDebugEnabled())
      logger
          .debug("Mise à jour du groupe de bâtiments " + agentGroupeBatiments);
    AgentZoneElementaireBatie agentZoneElementaireBatie = agentGroupeBatiments
        .getZoneElementaireBatie();
    if (logger.isDebugEnabled())
      logger.debug("nbgrbat avant : "
          + agentZoneElementaireBatie.getGroupesBatiments().size());

    double seuilBuffer = 15.0;
    double seuilDistance = 30.0;
    Set<AgentBatiment> listeBatiments = agentGroupeBatiments.getBatiments();
    // -- On supprime le groupe de batiments si il n'y a pas de batiments dedans
    // --
    if (listeBatiments.isEmpty()) {
      if (logger.isDebugEnabled())
        logger.debug("Suppression du groupe de batiments vide : "
            + agentGroupeBatiments.getGeom());
      agentGroupeBatiments.remove();
      agentZoneElementaireBatie.removeComposant(agentGroupeBatiments);
    } else {
      listeGBATester.add(agentGroupeBatiments);
      // -- Première étape : On recalcule la géométrie en cas de
      // déplacement/suppression de batiments --
      //IGeometry geometrie = JtsUtil.fermeture(listeBatiments, seuilBuffer);
      IGeometry geometrie = JtsUtil.bufferPolygones(listeBatiments, seuilBuffer);
      if ((geometrie instanceof GM_Polygon)
          && (!geometrie.equals(agentGroupeBatiments.getGeom()))) {
        if (logger.isDebugEnabled())
          logger.debug("la géométrie du groupe de bâtiments a été modifiée");
        if (logger.isDebugEnabled())
          logger.debug("geometrie avant " + agentGroupeBatiments.getGeom());
        agentGroupeBatiments.setGeom(geometrie);
        if (logger.isDebugEnabled())
          logger.debug("geometrie après " + agentGroupeBatiments.getGeom());
      } else if (geometrie instanceof GM_MultiSurface) {// cas où un groupe se
                                                        // sépare en plusieurs
                                                        // groupes
        // On crée de nouveaux groupes de batiments
        if (logger.isDebugEnabled()) {
          logger.debug("le groupe de bâtiments se sépare en plusieurs groupes");
        }
        for (GM_Polygon nouvelleGeometrie : ((GM_MultiSurface<GM_Polygon>) geometrie)
            .getList()) {
          // On cherche les batiments contenus dans la géométrie
          Set<AgentBatiment> listeBatiments2 = new HashSet<AgentBatiment>();
          for (AgentBatiment batiment : agentGroupeBatiments.getBatiments()) {
            if (nouvelleGeometrie.intersects(batiment.getGeom())) {
              listeBatiments2.add(batiment);
            }
          }
          AgentGroupeBatiments nouveauGroupe = new AgentGroupeBatiments(
              (GM_Polygon) nouvelleGeometrie, agentZoneElementaireBatie,
              listeBatiments2, true);
          if (logger.isDebugEnabled())
            logger.debug("Groupe Batiment ajouté : " + nouveauGroupe.getGeom()
                + " avec " + nouveauGroupe.getBatiments().size());
          nouveauGroupe.instancierContraintes();
          listeGBATester.add(nouveauGroupe);
        }
        listeGBATester.remove(agentGroupeBatiments);
        // On supprime agentGroupeBatiments
        if (logger.isDebugEnabled())
          logger.debug("Suppression du groupe de batiments : "
              + agentGroupeBatiments.getGeom());
        agentGroupeBatiments.remove();
        agentZoneElementaireBatie.removeComposant(agentGroupeBatiments);
      }

      // -- deuxième étape : On fusionne avec les groupes à une distance
      // inférieure à la distance seuil (de 15 m) --
      while (!listeGBATester.isEmpty()) {
        AgentGroupeBatiments agentATester = listeGBATester.get(0);
        logger.debug("dist pp GB : "
            + agentATester.getDistanceGroupeBatimentsLePlusProche());
        if (agentATester.getDistanceGroupeBatimentsLePlusProche() < seuilDistance) {
          if (logger.isDebugEnabled())
            logger.debug("il existe des groupes de batiments à moins de "
                + seuilDistance + " m du GB : " + agentATester.getGeom()
                + " avec " + agentATester.getBatiments());
          // On crée la liste des groupes à supprimer et des batiments du
          // nouveau groupe à créer
          List<AgentGroupeBatiments> listeGroupesASupprimer = new ArrayList<AgentGroupeBatiments>();
          Set<AgentBatiment> listeBatimentsNouveauGroupe = new HashSet<AgentBatiment>();
          for (AgentBatiment bat : agentATester.getBatiments()) {
            listeBatimentsNouveauGroupe.add(bat);
          }
          for (AgentGroupeBatiments groupe : agentZoneElementaireBatie
              .getGroupesBatiments()) {
            if ((!groupe.equals(agentATester)) && (!groupe.isDeleted())) {
              double distance = groupe.getGeom().distance(
                  agentATester.getGeom());
              if (distance < seuilDistance) {
                listeGroupesASupprimer.add(groupe);
                listeBatimentsNouveauGroupe.addAll(groupe.getBatiments());
              }
            }
          }
          if (listeGroupesASupprimer.size() > 0) {
            // On crée une liste de groupe à ne pas effacer
            List<AgentGroupeBatiments> listeAGarder = new ArrayList<AgentGroupeBatiments>();
            // On ajoute le/les nouveaux groupes de batiment
            // TODO chekck if bufferpolygones or fermeture
            IGeometry resultat = JtsUtil.bufferPolygones(listeBatimentsNouveauGroupe, seuilBuffer);
            if (resultat instanceof GM_Polygon) {
              AgentGroupeBatiments nouveauGroupe = new AgentGroupeBatiments(
                  (GM_Polygon) resultat, agentZoneElementaireBatie,
                  listeBatimentsNouveauGroupe, true);
              if (logger.isDebugEnabled())
                logger.debug("Groupe Batiment ajouté : "
                    + nouveauGroupe.getGeom());
              nouveauGroupe.instancierContraintes();
              // On ajoute le groupe de bâtiment à la liste des groupe à tester
              listeGBATester.add(nouveauGroupe);

              // On supprime les groupes qui ont fusioné
              while (!listeGroupesASupprimer.isEmpty()) {
                AgentGroupeBatiments agentGroupeBati = listeGroupesASupprimer
                    .get(0);
                if (logger.isDebugEnabled())
                  logger.debug("Suppression du groupe de batiments : "
                      + agentGroupeBati.getGeom());
                agentGroupeBati.remove();
                agentZoneElementaireBatie.removeComposant(agentGroupeBati);
                listeGroupesASupprimer.remove(agentGroupeBati);
                // Si ce groupe est dans la liste de GB à tester on le supprime
                if (listeGBATester.contains(agentGroupeBati)) {
                  listeGBATester.remove(agentGroupeBati);
                }
              }
            }
            // Dans le cas où un groupe se sépare en plusieurs il faut vérifier
            // qu'on ne supprime pas un groupe pour recréer le même
            else if (resultat instanceof GM_MultiSurface) {// cas où un groupe
                                                           // se sépare en 1 ou
                                                           // plusieurs groupes
              for (GM_Polygon nouvelleGeometrie : ((GM_MultiSurface<GM_Polygon>) resultat)
                  .getList()) {
                // On cherche les batiments contenus dans la géométrie
                Set<AgentBatiment> listeBatiments2 = new HashSet<AgentBatiment>();
                for (AgentBatiment batiment : agentZoneElementaireBatie
                    .getBatiments()) {
                  if (nouvelleGeometrie.intersects(batiment.getGeom())) {
                    listeBatiments2.add(batiment);
                  }
                }
                // On vérifie que le nouveau groupe de bâtiment n'est pas un
                // ancien groupe
                AgentGroupeBatiments groupeIdent = null;
                for (AgentGroupeBatiments groupeB : agentZoneElementaireBatie
                    .getGroupesBatiments()) {
                  // Est ce qu'il peut y avoir un pb avec des groupes
                  // supprimés????
                  if (listeBatiments2.equals(groupeB.getBatiments())) {
                    groupeIdent = groupeB;
                    if (logger.isDebugEnabled())
                      logger
                          .debug("le nouveau groupe correspond à un ancien groupe : "
                              + groupeIdent.getGeom());
                  }
                }
                // si le nouveau groupe ne correspond pas à un ancien groupe on
                // le crée
                if (groupeIdent == null) {
                  AgentGroupeBatiments nouveauGroupe = new AgentGroupeBatiments(
                      (GM_Polygon) nouvelleGeometrie,
                      agentZoneElementaireBatie, listeBatiments2, true);
                  if (logger.isDebugEnabled())
                    logger.debug("Groupe Batiment ajouté : "
                        + nouveauGroupe.getGeom());
                  nouveauGroupe.instancierContraintes();
                  // On ajoute le groupe de bâtiment à la liste des groupe à
                  // tester
                  listeGBATester.add(nouveauGroupe);
                } else {
                  listeAGarder.add(groupeIdent);
                }
              }
              // On supprime les groupes qui ont fusioné
              while (!listeGroupesASupprimer.isEmpty()) {
                AgentGroupeBatiments agentGroupeBati = listeGroupesASupprimer
                    .get(0);
                if (!listeAGarder.contains(agentGroupeBati)) {
                  if (logger.isDebugEnabled())
                    logger.debug("Suppression du groupe de batiments : "
                        + agentGroupeBati.getGeom());
                  agentGroupeBati.remove();
                  agentZoneElementaireBatie.removeComposant(agentGroupeBati);
                  listeGroupesASupprimer.remove(agentGroupeBati);
                  // Si ce groupe est dans la liste de GB à tester on le vire
                  if (listeGBATester.contains(agentGroupeBati)) {
                    listeGBATester.remove(agentGroupeBati);
                  }
                } else {
                  listeGroupesASupprimer.remove(agentGroupeBati);
                }
              }
            }
            // On supprime agentATester si il ne fait pas partie des groupes à
            // garder
            if (!listeAGarder.contains(agentATester)) {
              if (logger.isDebugEnabled())
                logger.debug("Suppression du groupe de batiments : "
                    + agentATester.getGeom());
              agentATester.remove();
              agentZoneElementaireBatie.removeComposant(agentATester);
            }
            // On supprime agentATester de la liste de GB à tester
            listeGBATester.remove(agentATester);
          }
        } else {// On supprime agentATester de la liste de GB à tester
          listeGBATester.remove(agentATester);
        }
      }
      if (logger.isDebugEnabled())
        logger.debug("nbgrbat : "
            + agentZoneElementaireBatie.getGroupesBatiments().size());
    }
  }

}
