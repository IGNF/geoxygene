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
/**
 *
 */
package fr.ign.cogit.appli.geopensim.feature.meso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.EspaceVide;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.ChargeurTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Cette classe représente les zone élémentaires urbaines (les îlots), c'est à
 * dire les surfaces bâties entourées de tronçons du réseau incluant les
 * tronçons de routes, de chemins, de voies ferrées et de cours d'eau. Dans la
 * version actuelle, la géométrie d'une zone élémentaire correspond à la
 * géométrie de la face topologique créée par les axiales de tronçons
 * Définissant la zone. Dans une prochaine version, cette géométrie pourra se
 * voir soustraire la géométrie des tronçons sus-mentionnés ainsi que les
 * surfaces d'eau par exemple. TODO résoudre le problème des zones élémentaires
 * possédant une multi-géométrie. Une solution consiste à donner une
 * multi-géométrie à toutes les zones élémentaires. TODO Ajouter les
 * alignements...
 * 
 * @author Julien Perret
 */

@Entity
@AttributeOverride(name = "estTrouDe", column = @Column(name = "esttroude"))
public class ZoneElementaireUrbaine extends ZoneSurfaciqueUrbaine implements
    ZoneElementaire {

  protected ZoneElementaireImpl zoneElementaireImpl = new ZoneElementaireImpl();

  /**
   * Constructeur vide
   */
  public ZoneElementaireUrbaine() {
    super();
  }

  /**
   * Constructeur par copie
   */
  public ZoneElementaireUrbaine(ZoneElementaireUrbaine zone) {
    this.setGeom((GM_Object) zone.getGeom().clone());
    this.setFeatureType(zone.getFeatureType());
    this.setTopo(zone.getTopo());
    this.setBordeUniteUrbaine(zone.getBordeUniteUrbaine());
    this.setClassificationFonctionnelle(zone.getClassificationFonctionnelle());
  }

  /**
   * Constructeur à partir d'une géométrie
   */
  public ZoneElementaireUrbaine(IPolygon polygone) {
    super(polygone);
  }

  public static ZoneElementaireUrbaine newInstance() {
    return new ZoneElementaireUrbaine();
  }

  public static ZoneElementaireUrbaine newInstance(IPolygon polygone) {
    return new ZoneElementaireUrbaine(polygone);
  }

  public int getIdGeo() {
    return this.getAgentGeographique().getIdGeo();
  }

  private boolean infinite = false;

  public boolean isInfinite() {
    return this.infinite;
  }

  public void setInfinite(boolean infinite) {
    this.infinite = infinite;
  }

  // EspacesVides
  protected List<EspaceVide> espacesVides = new ArrayList<EspaceVide>();

  /**
   * @return espacesVides
   */
  @OneToMany
  @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
  public List<EspaceVide> getEspacesVides() {
    return espacesVides;
  }

  /**
   * @param espacesVides espacesVides à Définir
   */
  public void setEspacesVides(List<EspaceVide> espacesVides) {
    this.espacesVides = espacesVides;
    for (EspaceVide e : this.espacesVides) {
      e.setZoneElementaireUrbaine(this);
    }
  }

  /**
   * Renvoie l'espace vide d'indice <b>i</b>.
   * @param i indice de l'espace vide
   * @return l'espace vide d'indice <b>i</b>.
   */
  public EspaceVide getEspaceVide(int i) {
    return this.espacesVides.get(i);
  }

  /**
   * Ajoute un espace vide à la zone élémentaire.
   * @param e un espace vide
   */
  public void addEspaceVide(EspaceVide e) {
    if (e == null)
      return;
    this.espacesVides.add(e);
    e.setZoneElementaireUrbaine(this);
  }

  /**
   * Supprime un espace vide de la zone élémentaire.
   * @param e un espace vide
   */
  public void removeEspaceVide(EspaceVide e) {
    if (e == null)
      return;
    this.espacesVides.remove(e);
    e.setZoneElementaireUrbaine(null);
  }

  /**
   * Vide la liste des espaces vides.
   */
  public void emptyEspacesVide() {
    for (EspaceVide e : this.espacesVides) {
      e.setZoneElementaireUrbaine(null);
    }
    this.espacesVides.clear();
  }

  // Groupes batiments
  protected List<GroupeBatiments> groupesBatiments = new ArrayList<GroupeBatiments>();

  /**
   * Renvoie la liste des groupes de batiments
   * @return la liste des groupes de batiments
   */
  @OneToMany
  public List<GroupeBatiments> getGroupesBatiments() {
    return groupesBatiments;
  }

  /**
   * Affecte la liste des groupes de batiments
   * @param groupesBatiments la liste des groupes de batiments
   */
  public void setGroupesBatiments(List<GroupeBatiments> groupesBatiments) {
    this.groupesBatiments = groupesBatiments;
  }

  /**
   * Renvoie le groupe de bâtiments d'indice <b>i</b>.
   * @param i indice du groupe de bâtiments
   * @return le groupe de bâtiments d'indice <b>i</b>.
   */
  public GroupeBatiments getGroupeBatiments(int i) {
    return this.groupesBatiments.get(i);
  }

  /**
   * Ajoute un groupe de bâtiments à la zone élémentaire.
   * @param g le groupe de bâtiments à ajouter
   */
  public void addGroupeBatiments(GroupeBatiments g) {
    if (g == null)
      return;
    this.groupesBatiments.add(g);
  }

  /**
   * enlève un groupe de bâtiments à la zone élémentaire.
   * @param g le groupe de bâtiments à enlever
   */
  public void removeGroupeBatiments(GroupeBatiments g) {
    if (g == null)
      return;
    this.groupesBatiments.remove(g);
  }

  /**
   * Vide la liste des groupes de bâtiments.
   */
  public void emptyGroupesBatiments() {
    this.groupesBatiments.clear();
  }

  // Groupes routes
  protected List<GroupeRoutesDansIlot> groupesRoutes = new ArrayList<GroupeRoutesDansIlot>();

  /**
   * Renvoie la liste des groupes de routes de la zone élémentaire.
   * @return la liste des groupes de routes de la zone élémentaire.
   */
  @OneToMany
  public List<GroupeRoutesDansIlot> getGroupesRoutes() {
    return groupesRoutes;
  }

  /**
   * Affecte la liste des groupes de routes de la zone élémentaire.
   * @param groupesRoutes la liste des groupes de routes de la zone élémentaire
   *          à affecter.
   */
  public void setGroupesRoutes(List<GroupeRoutesDansIlot> groupesRoutes) {
    this.groupesRoutes = groupesRoutes;
  }

  /**
   * Renvoie le groupe de routes d'indice <b>i</b>.
   * @param i indice du groupe de routes
   * @return le groupe de routes d'indice <b>i</b>.
   */
  public GroupeRoutesDansIlot getGroupeRoutes(int i) {
    return this.groupesRoutes.get(i);
  }

  /**
   * Ajoute un groupe de routes à la zone élémentaire
   * @param g groupe de routes à ajouter à la zone élémentaire
   */
  public void addGroupeRoutes(GroupeRoutesDansIlot g) {
    if (g == null)
      return;
    this.groupesRoutes.add(g);
  }

  /**
   * enlève un un groupe de routes à la zone élémentaire
   * @param g un groupe de routes à enlever à la zone élémentaire
   */
  public void removeGroupeRoutes(GroupeRoutesDansIlot g) {
    if (g == null)
      return;
    this.groupesRoutes.remove(g);
  }

  /**
   * Vide la liste des groupes de routes de la zone élémentaire.
   */
  public void emptyGroupesRoutes() {
    this.groupesRoutes.clear();
  }

  // Classification fonctionnelle Urbaine
  protected int classificationFonctionnelle;

  /**
   * @return classificationFonctionnelle
   */
  public int getClassificationFonctionnelle() {
    return classificationFonctionnelle;
  }

  /**
   * @param classificationFonctionnelle classificationFonctionnelle à Définir
   */
  public void setClassificationFonctionnelle(int classificationFonctionnelle) {
    this.classificationFonctionnelle = classificationFonctionnelle;
  }

  // Batiments
  protected int nombreBatimentsTrousCompris = 0;

  /**
   * @return nombreBatimentsTrousCompris
   */
  public int getNombreBatimentsTrousCompris() {
    return nombreBatimentsTrousCompris;
  }

  /**
   * @param nombreBatimentsTrousCompris nombreBatimentsTrousCompris à Définir
   */
  public void setNombreBatimentsTrousCompris(int nombreBatimentsTrousCompris) {
    this.nombreBatimentsTrousCompris = nombreBatimentsTrousCompris;
  }

  // Type Fonctionnel
  protected int typeFonctionnel;

  /**
   * @return type fonctionnel de la zone élémentaire urbaine
   */
  public int getTypeFonctionnel() {
    return typeFonctionnel;
  }

  /**
   * @param typeFonctionnel type fonctionnel de la zone élémentaire urbaine
   */
  public void setTypeFonctionnel(int typeFonctionnel) {
    this.typeFonctionnel = typeFonctionnel;
  }

  /**
   * Contruit les espaces vides entre les bâtiments de l'îlot. Cela se fait en 5
   * étapes :
   * <ul>
   * <li>buffer interieur sur le contour de la zone élémentaire urbaine, pour ne
   * pas Détecter comme espace vide l'espace situé entre le bord de l'îlot et la
   * première rangée de bâtiments (distance de 20 m) et filtre de
   * Douglas-Peucker sur ce buffer interieur (seuil de 1m)
   * <li>buffer de 20m autour des bâtiments et filtre de Douglas-Peucker (seuil
   * de 1m) sur ce buffer interieur
   * <li>on soustrait la zone (2) à la zone (1)
   * <li>on supprime les plus petits (seuil de 100m)
   * <li>buffer de 20m autour des espaces vides conservés (pour les faire aller
   * jusqu'aux bâtiments), et filtrage par Douglas-Peucker (seuil de 1m)
   * </ul>
   */
  @SuppressWarnings("unchecked")
  public void construireEspacesVides() {
    if (this.getBatiments().isEmpty()) {
      this.addEspaceVide(EspaceVide.newInstance(this.getGeom()));
      return;
    }
    double seuilDouglasPeucker = 1.0;
    double seuilBuffer = 20.0;
    IGeometry bufferInterne = this.getGeometrie().buffer(-seuilBuffer);
    if ((bufferInterne == null) || (bufferInterne.isEmpty())) {
      if (logger.isDebugEnabled()) {
        logger.debug("Buffer interne vide");
        logger.debug("Zone élémentaire traitée " + this);
        logger.debug(this.getGeom());
      }
      return;
    }
    List<IGeometry> listeBuffersBatiments = new ArrayList<IGeometry>();
    for (Batiment batiment : this.getBatiments()) {
      listeBuffersBatiments.add(batiment.getGeom().buffer(seuilBuffer));
    }
    IGeometry bufferBatiments = JtsAlgorithms.union(listeBuffersBatiments);
    IGeometry difference = null;
    try {
      Geometry jtsBufferInterne = JtsGeOxygene.makeJtsGeom(bufferInterne);
      jtsBufferInterne = TopologyPreservingSimplifier.simplify(
          jtsBufferInterne, seuilDouglasPeucker);
      Geometry jtsBufferBatiments = JtsGeOxygene.makeJtsGeom(bufferBatiments);
      jtsBufferBatiments = TopologyPreservingSimplifier.simplify(
          jtsBufferBatiments, seuilDouglasPeucker);
      if (jtsBufferInterne.isEmpty())
        difference = null;// ne devrait pas arriver
      else {
        Geometry jtsDifference = jtsBufferInterne
            .difference(jtsBufferBatiments);
        difference = JtsGeOxygene.makeGeOxygeneGeom(jtsDifference);
      }
    } catch (Exception e) {
      logger
          .error("## CALCUL DE DIFFERENCE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
      if (logger.isDebugEnabled()) {
        logger.debug(e.getMessage());
        logger.debug("Les géométries concernées sont :");
        logger.debug(bufferInterne);
        logger.debug(bufferBatiments);
      }
    }
    if (difference == null) {
      logger.error("Erreur pendant le calcul de la différence.");
      if (logger.isDebugEnabled()) {
        logger.debug("Zone élémentaire traitée " + this);
        logger.debug("Buffer interne " + bufferInterne);
        logger.debug("Buffer sur les bâtiments " + bufferBatiments);
      }
      return;
    }
    if (difference.isEmpty()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Différence vide");
        logger.debug("Zone élémentaire traitée " + this);
      }
      return;
    }
    IGeometry resultat = difference.buffer(seuilBuffer);
    resultat = Filtering.DouglasPeucker(resultat, seuilDouglasPeucker);
    if (resultat instanceof GM_Polygon) {// on a un seul polygone comme
      // espace vide
      if (resultat.area() >= 100.0)
        this.addEspaceVide(EspaceVide.newInstance(resultat));
    } else if (resultat instanceof GM_MultiSurface) {// on a un ensemble de
      // polygones comme espaces
      // vides
      for (GM_Polygon geometrie : ((GM_MultiSurface<GM_Polygon>) resultat)
          .getList()) {
        // on a un polygone comme nouvel espace vide
        if (resultat.area() >= 100.0)
          this.addEspaceVide(EspaceVide.newInstance(geometrie));
      }
    } else { // sinon, on a pas d'espace vide
      if (logger.isDebugEnabled()) {
        logger.debug("Pas d'espace vide construit");
        if (resultat != null) {
          logger.debug("géométrie de l'intersection de type "
              + resultat.getClass());
        } else {
          logger.debug("résultat null");
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug(this.getEspacesVides().size() + " espaces vides créés.");
    }
  }

  /**
   * Contruit les groupes de bâtiments présents dans la zone élémentaire et
   * lance le calcul des alignements dans ces groupes.
   */
  @SuppressWarnings("unchecked")
  public void construireGroupes(Collection<Batiment> batiments) {
    if (logger.isDebugEnabled()) {
      logger.debug("Début construireGroupes sur " + this.getGeom());
    }
    double seuilBuffer = 15.0;
    if (batiments.size() != 0) {// Si il y a des batiments dans la
                                          // zone élémentaire
      IGeometry resultat = JtsUtil.bufferPolygones(batiments,
          seuilBuffer);
      if (resultat instanceof GM_Polygon) {// on a un seul groupe de bâtiments
                                           // dans la zone élémentaire urbaine
        if (logger.isDebugEnabled()) {
          logger.debug("Groupe de type polygone " + resultat);
        }
        GroupeBatiments groupe = new GroupeBatiments(this, batiments,
            resultat);
        this.addGroupeBatiments(groupe);
        groupe.setDateSourceSaisie(this.getDateSourceSaisie());
        groupe.construireAlignements();
      } else if (resultat instanceof GM_MultiSurface) {// on a plusieurs groupes
                                                       // de bâtiments
        if (logger.isDebugEnabled()) {
          logger.debug("Groupe de type multi polygone " + resultat);
        }
        for (GM_Polygon geometrie : ((GM_MultiSurface<GM_Polygon>) resultat)
            .getList()) {
          List<Batiment> listeBatiments = new ArrayList<Batiment>(0);
          for (Batiment batiment : batiments) {
            if (geometrie.intersects(batiment.getGeom())) {
              listeBatiments.add(batiment);
            }
          }
          GroupeBatiments groupe = new GroupeBatiments(this, listeBatiments,
              geometrie);
          this.addGroupeBatiments(groupe);
          groupe.setDateSourceSaisie(this.getDateSourceSaisie());
          groupe.construireAlignements();
        }
        if (logger.isDebugEnabled()) {
          logger.debug(this.getGroupesBatiments().size()
              + " groupes de bâtiments créés.");
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Fin construireGroupes");
    }
  }

  /**
   * Contruit les groupes de routes présents dans l'îlot TODO contruire les
   * groupes de routes
   */
  public void construireGroupesRoutes() {
  }

//  protected Collection<Batiment> batiments = new ArrayList<Batiment>();

  @Override
  @OneToMany
  public Collection<Batiment> getBatiments() {
    Collection<Batiment> batiments = new ArrayList<Batiment>(0);
    for (GroupeBatiments groupe : this.getGroupesBatiments()) {
      batiments.addAll(groupe.getBatiments());
    }
    return batiments;
  }

//  /**
//   * @param batiments bâtiments de la surface bâtie
//   */
//  public void setBatiments(Collection<Batiment> batiments) {
//    this.batiments = batiments;
//    this.nombreBatiments = batiments.size();
//  }
//  /**
//   * @param e bâtiment de la surface bâtie
//   */
//  public void addBatiment(Batiment e) {
//    if (e == null) {
//      return;
//    }
//    this.batiments.add(e);
//    this.nombreBatiments = this.batiments.size();
//    e.setZoneElementaireUrbaine(this);
//  }

//  /**
//   * @param c bâtiments de la surface bâtie
//   */
//  public void addAllBatiment(Collection<Batiment> c) {
//    this.batiments.addAll(c);
//    this.nombreBatiments = this.batiments.size();
//  }

//  /**
//   * @param o bâtiment de la surface bâtie
//   */
//  public void removeBatiment(Batiment o) {
//    if (o == null) {
//      return;
//    }
//    this.batiments.remove(o);
//    this.nombreBatiments = this.batiments.size();
//    o.setZoneElementaireUrbaine(null);
//  }

//  /**
//   * Vide les bâtiments de la surface bâtie
//   */
//  public void clearBatiments() {
//    for (Batiment b : this.batiments) {
//      b.setZoneElementaireUrbaine(null);
//    }
//    this.batiments.clear();
//    this.nombreBatiments = this.batiments.size();
//  }

//  /**
//   * @param o bâtiment
//   * @return vrai si la surface bâtie contient le bâtiment passé en paramètre,
//   *         faux sinon
//   */
//  public boolean containsBatiments(Object o) {
//    return this.batiments.contains(o);
//  }

//  @Override
//  public int sizeBatiments() {
//    return this.batiments.size();
//  }

  @Override
  public void qualifier() {
    super.qualifier();
    // trous
    this.setNombreTrous(this.getTrous().size());
    // batiments
    for (Batiment batiment : this.getBatiments()) {
      batiment.qualifier();
    }
    this.nombreBatimentsTrousCompris = this.nombreBatiments;
    for (ZoneElementaire trou : this.getTrous()) {
      this.nombreBatimentsTrousCompris += ((ZoneElementaireUrbaine) trou)
          .getBatiments().size();
    }
    // troncons
    this.setNombreTroncons(this.getTroncons().size());
    // densite
    double sommeAiresBatiments = 0.0;
    // on calcule l'intersection avec la zone élémentaire au cas où le
    // bâtiment soit à cheval sur plusieurs zones
    for (Batiment batiment : this.getBatiments()) {
        IGeometry intersection = batiment.getGeom().intersection(getGeom());
      if (intersection != null) {
        sommeAiresBatiments += intersection.area();
      }
    }
    // densite
    if (this.geom != null && this.getAire() != 0.0) {
      this.densite = sommeAiresBatiments / this.getAire();
    }
    // voisinages
    for (Troncon troncon : this.getTroncons()) {
      for (ZoneElementaire voisin : troncon.getZonesElementaires()) {
        if ((voisin != this) && (!this.getVoisins().contains(voisin))) {
          this.addVoisin(voisin);
        }
      }
    }
    // tailleBatiments;
    // rapport Bati
    int nbPetitsBatiments = 0;
    int nbGrandsBatiments = 0;
    for (Batiment batiment : this.getBatiments()) {
      if (batiment.getAire() < 200)
        nbPetitsBatiments++;
      else
        nbGrandsBatiments++;
    }
    this
        .setRapportBati((this.getNombreBatiments() > 0) ? ((double) nbPetitsBatiments / (double) this
            .getNombreBatiments())
            : Double.NaN);
    // elongation batiments
    double sommeElongationPondereeBatiments = 0;
    for (Batiment batiment : this.getBatiments()) {
      sommeElongationPondereeBatiments += batiment.getElongation()
          * batiment.getAire();
    }
    if (sommeAiresBatiments != 0) {
      this.setMoyenneElongationBatiments(sommeElongationPondereeBatiments
          / sommeAiresBatiments);
    } else {
      this.setMoyenneElongationBatiments(0.0);
    }

    // typeFonctionnel;

    // classificationUrbaine;
    this.setClassificationFonctionnelle(ClassificationZoneElementaire
        .classifierZoneElementaireIJDMM(this));
    // groupes
    for (GroupeBatiments groupe : this.getGroupesBatiments())
      groupe.qualifier();
    // espaces vides
    for (EspaceVide e : this.getEspacesVides()) {
      e.setDateSourceSaisie(this.getDateSourceSaisie());
      e.qualifier();
    }

    // distance entre batiments
    // FIXME il faut trier les triangles
    if (this.getBatiments().size()>2) {
        this.buildTriangulation();
    } else {
        if (this.getBatiments().size()==2) {
            Iterator<Batiment> it = this.getBatiments().iterator();
            this.setMoyenneDistanceBatiments(it.next().getGeom().distance(it.next().getGeom()));
        }
    }
    // distance moyenne entre les bâtiments et leur plus proche voisin
    if (this.getBatiments().size()>1){
        for(Batiment batiment:this.getBatiments()){
            if (batiment!=null)
                if (batiment.getGeom()!=null)
                    if (batiment.getBatimentLePlusProche()!=null)
                        if (batiment.getBatimentLePlusProche().getGeom()!=null)
                            distanceMoyennePlusProcheBatiment += batiment.getGeom().distance(batiment.getBatimentLePlusProche().getGeom());
                        else logger.error("null closest building geometry");
                    else logger.error("null closest building");
                else logger.error("null building geometry");
            else logger.error("null building");
        }
        this.setDistanceMoyennePlusProcheBatiment(distanceMoyennePlusProcheBatiment/this.getBatiments().size());
    }
    this.buildRoadTriangulation();
  }

  /**
     *
     */
  private void buildRoadTriangulation() {
    // distance max à la route
    TriangulationJTS carte = new TriangulationJTS("triangulation");
    try {
      List<Troncon> tronconsRoutiers = new ArrayList<Troncon>(0);
      for (Troncon troncon : this.getTroncons())
        if ((troncon instanceof TronconRoute)
            || (troncon instanceof TronconChemin)) {
          tronconsRoutiers.add(troncon);
        }
      ChargeurTriangulation.importSegments(tronconsRoutiers, carte);
      if (carte.getPopNoeuds().size() <= 2) {
        return;
      }
      GM_Aggregate<IGeometry> aggregate = new GM_Aggregate<IGeometry>();
      for (Troncon t : tronconsRoutiers) {
        aggregate.add(t.getGeom());
      }
      carte.triangule("czevBQ"); // Ajouter v pour activer le voronoi Q pour
                                 // quiet
      for (Noeud noeudVoronoi : carte.getPopVoronoiVertices()) {
        if (this.getGeom().contains(noeudVoronoi.getGeom())) {
          double distance = noeudVoronoi.getGeom().distance(aggregate);
          if (distance > this.distanceALaRoute) {
            this.distanceALaRoute = distance;
            this.pointLePlusLoinDeLaRoute = noeudVoronoi.getGeometrie();
          }
        }
      }
      if (logger.isDebugEnabled())
        logger.debug("distance = " + this.distanceALaRoute);
      // ShapefileWriter.write(carte.getPopVoronoiEdges(),"/tmp/voronoi_edges_"+(nbVoronoi++)+".shp");

    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  /**
     *
     */
  private void buildTriangulation() {
    if (logger.isDebugEnabled())
      logger.debug("Triangulation de la zone élémentaire de géométrie "
          + this.getGeom());
    TriangulationJTS carte = new TriangulationJTS("triangulation");
    try {
      ChargeurTriangulation.importCentroidesPolygones(
          new FT_FeatureCollection<Batiment>(this.getBatiments()), carte);
      if (carte.getPopNoeuds().size() > 2) {
        if (logger.isDebugEnabled())
          logger.debug("Initialisation de l'index spatial");
        carte.getPopNoeuds().initSpatialIndex(Tiling.class, true);
        carte.getPopArcs().initSpatialIndex(Tiling.class, false);
        if (logger.isDebugEnabled())
          logger.debug("Fusion des noeuds");
        carte.fusionNoeuds(1.0);
        if (carte.getPopNoeuds().size() > 2) {
          carte.triangule("czeBQ"); // Ajouter v pour activer le voronoi Q pour
                                    // quiet
          for (Arc arc : carte.getPopArcs()) {
            // distance entre centroides
            // TODO on considère qu'on a un seul correspondant par noeud
            Batiment ini = (Batiment) arc.getNoeudIni().getCorrespondant(0);
            Batiment fin = (Batiment) arc.getNoeudFin().getCorrespondant(0);
            this.setMoyenneDistanceBatiments(this.getMoyenneDistanceBatiments()
                + ini.getGeom().distance(fin.getGeom()));
          }
          if (!carte.getPopArcs().isEmpty())
            this.setMoyenneDistanceBatiments(this.getMoyenneDistanceBatiments()
                / carte.getPopArcs().size());
          if (logger.isDebugEnabled())
            logger
                .debug("moyenneDistanceBatiments=" + moyenneDistanceBatiments);
        }
      } else {
        if (logger.isDebugEnabled())
          logger.debug("Triangulation annulée car pas assez de noeuds : "
              + carte.getPopNoeuds().size());
      }
    } catch (Exception e) {
      logger.error("Echec de la Triangulation");
      e.printStackTrace();
    }
  }

  static int nbVoronoi = 0;
  private double moyenneDistanceBatiments = 0.0;

  /**
   * Renvoie la valeur de l'attribut moyenneDistanceBatiments.
   * 
   * @return la valeur de l'attribut moyenneDistanceBatiments
   */
  public double getMoyenneDistanceBatiments() {
    return this.moyenneDistanceBatiments;
  }

  /**
   * Affecte la valeur de l'attribut moyenneDistanceBatiments.
   * 
   * @param moyenneDistanceBatiments l'attribut moyenneDistanceBatiments à
   *          affecter
   */
  public void setMoyenneDistanceBatiments(double moyenneDistanceBatiments) {
    this.moyenneDistanceBatiments = moyenneDistanceBatiments;
  }

  private double distanceMoyennePlusProcheBatiment = 0.0;

  /**
   * @return distanceMoyennePlusProcheBatiment
   */
  public double getDistanceMoyennePlusProcheBatiment() {
    return this.distanceMoyennePlusProcheBatiment;
  }

  /**
   * @param distanceMoyennePlusProcheBatiment
   */
  public void setDistanceMoyennePlusProcheBatiment(
      double distanceMoyennePlusProcheBatiment) {
    this.distanceMoyennePlusProcheBatiment = distanceMoyennePlusProcheBatiment;
  }

  static int nbCarte = 0;

  @OneToMany
  @Override
  public Set<Troncon> getTroncons() {
    return zoneElementaireImpl.getTroncons();
  }

  @Override
  public void setTroncons(Set<Troncon> troncons) {
    zoneElementaireImpl.setTroncons(troncons);/*
                                               * if (troncons==null) return;
                                               * for(Troncon troncon:troncons)
                                               * troncon.addZoneElementaire
                                               * (this);
                                               */
  }

  @Override
  public void addTroncon(Troncon troncon) {
    zoneElementaireImpl.addTroncon(troncon);
    troncon.addZoneElementaire(this);
  }

  @Override
  public void removeTroncon(Troncon troncon) {
    zoneElementaireImpl.removeTroncon(troncon);
    troncon.removeZoneElementaire(this);
  }

  @Override
  public void emptyTroncons() {
    zoneElementaireImpl.emptyTroncons();
  }

  @Override
  public int getNombreTroncons() {
    return zoneElementaireImpl.getNombreTroncons();
  }

  @Override
  public void setNombreTroncons(int nombreTroncons) {
    zoneElementaireImpl.setNombreTroncons(nombreTroncons);
  }

  @OneToMany
  @Override
  public Set<ZoneElementaire> getTrous() {
    return zoneElementaireImpl.getTrous();
  }

  @Override
  public void setTrous(Set<ZoneElementaire> trous) {
    zoneElementaireImpl.setTrous(trous);/*
                                         * if (trous==null) return; for
                                         * (ZoneElementaire zone:trous)
                                         * zone.setEstTrouDe(this);
                                         */
  }

  @Override
  public void addTrou(ZoneElementaire zoneElementaire) {
    zoneElementaireImpl.addTrou(zoneElementaire);
    zoneElementaire.setEstTrouDe(this);
  }

  @Override
  public void removeTrou(ZoneElementaire zoneElementaire) {
    zoneElementaireImpl.removeTrou(zoneElementaire);
    zoneElementaire.setEstTrouDe(null);
  }

  @Override
  public void emptyTrous() {
    zoneElementaireImpl.emptyTrous();
  }

  @ManyToOne
  @Override
  public ZoneElementaire getEstTrouDe() {
    return zoneElementaireImpl.getEstTrouDe();
  }

  @Override
  public void setEstTrouDe(ZoneElementaire estTrouDe) {
    zoneElementaireImpl.setEstTrouDe(estTrouDe);
  }

  @Override
  public boolean estTrou() {
    return zoneElementaireImpl.estTrou();
  }

  @Override
  public int getNombreTrous() {
    return zoneElementaireImpl.getNombreTrous();
  }

  @Override
  public void setNombreTrous(int nombreTrous) {
    zoneElementaireImpl.setNombreTrous(nombreTrous);
  }

  @ManyToMany
  @Override
  public Set<ZoneElementaire> getVoisins() {
    return zoneElementaireImpl.getVoisins();
  }

  @Override
  public void setVoisins(Set<ZoneElementaire> voisins) {
    zoneElementaireImpl.setVoisins(voisins);/*
                                             * for(ZoneElementaire
                                             * voisin:voisins)
                                             * voisin.addVoisin(this);
                                             */
  }

  @Override
  public void addVoisin(ZoneElementaire voisin) {
    zoneElementaireImpl.addVoisin(voisin);
    if (!this.getVoisins().contains(voisin))
      voisin.addVoisin(this);
  }

  @Override
  public void removeVoisin(ZoneElementaire voisin) {
    zoneElementaireImpl.removeVoisin(voisin);
    if (this.getVoisins().contains(voisin))
      voisin.removeVoisin(this);
  }

  @Override
  public void emptyVoisins() {
    for (Troncon troncon : this.getTroncons())
      troncon.removeZoneElementaire(this);
    zoneElementaireImpl.emptyVoisins();
  }

  @ManyToOne
  @Override
  public ZoneAgregee<ZoneElementaire> getZoneAgregee() {
    return zoneElementaireImpl.getZoneAgregee();
  }

  @Override
  public void setZoneAgregee(ZoneAgregee<ZoneElementaire> zoneAgregee) {
    zoneElementaireImpl.setZoneAgregee(zoneAgregee);
  }

  @Override
  public boolean getBordeUniteUrbaine() {
    return zoneElementaireImpl.getBordeUniteUrbaine();
  }

  @Override
  public void setBordeUniteUrbaine(boolean bordeUniteUrbaine) {
    zoneElementaireImpl.setBordeUniteUrbaine(bordeUniteUrbaine);
  }

  private double rapportBati = 0;

  /**
   * Renvoie la valeur de l'attribut rapportBati. Il correspond au rapport du
   * nombre de petits batiments sur le nombre de grands batiments dans la zone
   * élémentaire urbaine. Le seuil utilisé pour Déterminer si un bati est grand
   * est de 200m².
   * 
   * @return la valeur de l'attribut rapportBati
   */
  public double getRapportBati() {
    return this.rapportBati;
  }

  /**
   * Affecte la valeur de l'attribut rapportBati. Il correspond au rapport du
   * nombre de petits batiments sur le nombre de grands batiments dans la zone
   * élémentaire urbaine. Le seuil utilisé pour Déterminer si un bati est grand
   * est de 200m².
   * 
   * @param rapportBati l'attribut rapportBati à affecter
   */
  public void setRapportBati(double rapportBati) {
    this.rapportBati = rapportBati;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> Unite<Elem, Agreg> getUnite() {
    return (Unite<Elem, Agreg>) this.uniteUrbaine;
  }

  @Override
  public <Elem extends ZoneElementaire, Agreg extends ZoneAgregee<Elem>> void setUnite(
      Unite<Elem, Agreg> unite) {
    if (unite instanceof UniteUrbaine) {
      this.uniteUrbaine = (UniteUrbaine) unite;
    }
  }

  @Override
  public String toString() {
    String resultat = "";
    ElementRepresentation.NUMBER_FORMAT.setMinimumFractionDigits(2);
    if (this.getComportement() != null) {
      resultat += " [" + this.getComportement().getClass().getSimpleName()
          + "]";
    }
    resultat += this.getClass().getSimpleName() + " " + this.getId() + " [ "
        + ElementRepresentation.NUMBER_FORMAT.format(this.getSatisfaction())
        + " ] - densite = "
        + ElementRepresentation.NUMBER_FORMAT.format(this.getDensite()) + " ("
        + this.getDateSourceSaisie() + ") ";
    resultat += this.getGroupesBatiments().size() + " groupes - "
        + this.getBatiments().size() + " batiments - " + this.getNombreTroncons()
        + " troncons ";
    return resultat;
  }

  private int distanceAuCentre = 0;

  /**
   * Renvoie la distance au centre de l'Unité
   * 
   * @return la distance au centre de l'Unité
   */
  public int getDistanceAuCentre() {
    return this.distanceAuCentre;
  }

  /**
   * @param distanceAuCentre la distance au centre de l'Unité
   */
  public void setDistanceAuCentre(int distanceAuCentre) {
    this.distanceAuCentre = distanceAuCentre;
  }

  private double distanceALaRoute = 0;

  public double getDistanceALaRoute() {
    return this.distanceALaRoute;
  }

  public void setDistanceALaRoute(double distanceALaRoute) {
    this.distanceALaRoute = distanceALaRoute;
  }

  IPoint pointLePlusLoinDeLaRoute = null;

  public IPoint getPointLePlusLoinDeLaRoute() {
    return this.pointLePlusLoinDeLaRoute;
  }

  public void setPointLePlusLoinDeLaRoute(IPoint pointLePlusLoinDeLaRoute) {
    this.pointLePlusLoinDeLaRoute = pointLePlusLoinDeLaRoute;
  }

  private double maxDensiteVoisins = 0;

  public double getMaxDensiteVoisins() {
    for (ZoneElementaire voisin : this.getVoisins()) {
      if (voisin instanceof ZoneElementaireUrbaine) {
        ZoneElementaireUrbaine voisinUrbain = (ZoneElementaireUrbaine) voisin;
        this.maxDensiteVoisins = Math.max(this.maxDensiteVoisins, voisinUrbain
            .getDensite());
      }
    }
    return this.maxDensiteVoisins;
  }

  public void setMaxDensiteVoisins(double maxDensiteVoisins) {
    this.maxDensiteVoisins = maxDensiteVoisins;
  }

  public double getMoyenneDensiteVoisins() {
    double moyenne = 0;
    int nbVoisins = 0;
    for (ZoneElementaire voisin : this.getVoisins()) {
      if (voisin instanceof ZoneElementaireUrbaine) {
        ZoneElementaireUrbaine voisinUrbain = (ZoneElementaireUrbaine) voisin;
        moyenne += voisinUrbain.getDensite();
        nbVoisins++;
      }
    }
    return moyenne / nbVoisins;
  }
//  public double getSatisfaction() {
//    this.getAgentGeographique().calculerSatisfaction();
//    return this.getAgentGeographique().getSatisfaction();
//  }
}
