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

package fr.ign.cogit.geoxygene.contrib.appariement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Resultat de l'appariement : lien entre des objets homologues de deux bases de
 * données. Un lien a aussi une géométrie qui est sa représentation graphique.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 */

public class Lien extends FT_Feature {

  /** Les objets d'une BD pointés par le lien */
  protected List<IFeature> objetsRef = new ArrayList<IFeature>();

  /**
   * @return reference object list
   */
  public List<IFeature> getObjetsRef() {
    return this.objetsRef;
  }

  /**
   * @param liste reference object list
   */
  public void setObjetsRef(List<IFeature> liste) {
    this.objetsRef = liste;
  }

  /**
   * @param objet reference object
   */
  public void addObjetRef(IFeature objet) {
    this.objetsRef.add(objet);
  }

  /**
   * @param objets reference object list
   */
  public void addObjetsRef(List<IFeature> objets) {
    this.objetsRef.addAll(objets);
  }

  /** Les objets de l'autre BD pointés par le lien */
  protected List<IFeature> objetsComp = new ArrayList<IFeature>();
  
  /**
   * @return comparison object list
   */
  public List<IFeature> getObjetsComp() {
    return this.objetsComp;
  }

  /**
   * @param liste comparison object list
   */
  public void setObjetsComp(List<IFeature> liste) {
    this.objetsComp = liste;
  }

  /**
   * @param objet comparison object to add to the list
   */
  public void addObjetComp(IFeature objet) {
    this.objetsComp.add(objet);
  }

  /**
   * @param objets comparison object list to add to the list
   */
  public void addObjetsComp(List<IFeature> objets) {
    this.objetsComp.addAll(objets);
  }

  /**
   * Estimation de la qualité du lien d'appariement. Entre 0 et 1 en général
   */
  private double evaluation;

  /**
   * @return matching link evaluation
   */
  public double getEvaluation() {
    return this.evaluation;
  }

  /**
   * @param evaluation matching link evaluation
   */
  public void setEvaluation(double evaluation) {
    this.evaluation = evaluation;
  }

  /** Liste d'indicateurs utilisés pendant les calculs d'appariement */
  protected List<Object> indicateurs = new ArrayList<Object>();

  /**
   * @return indicators
   */
  public List<Object> getIndicateurs() {
    return this.indicateurs;
  }

  /**
   * @param liste indicators
   */
  public void setIndicateurs(List<Object> liste) {
    this.indicateurs = liste;
  }

  /**
   * @param objet an indicator
   */
  public void addIndicateur(Object objet) {
    this.indicateurs.add(objet);
  }

  /** Texte libre pour décrire le lien d'appariement */
  protected String commentaire = new String();

  /**
   * @return comment
   */
  public String getCommentaire() {
    return this.commentaire;
  }

  /**
   * @param commentaire comment
   */
  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  /** Texte libre pour décrire le nom du procesus d'appariement. */
  protected String nom = new String();

  /**
   * @return matching name
   */
  public String getNom() {
    return this.nom;
  }

  /**
   * @param nom matching name
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  /** Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud"). */
  protected String type = new String();

  /**
   * @return matching type
   */
  public String getType() {
    return this.type;
  }

  /**
   * @type matching type
   */
  public void setType(String type) {
    this.type = type;
  }

  /** Texte libre pour décrire les objets de la BD1 pointés. */
  protected String reference = new String();

  /**
   * @return reference database description
   */
  public String getReference() {
    return this.reference;
  }

  /**
   * @param reference reference database description
   */
  public void setReference(String reference) {
    this.reference = reference;
  }

  /** Texte libre pour décrire les objets de la BD2 pointés. */
  protected String comparaison = new String();

  /**
   * @return comparison database description
   */
  public String getComparaison() {
    return this.comparaison;
  }

  /**
   * @param comparaison comparison database description
   */
  public void setComparaison(String comparaison) {
    this.comparaison = comparaison;
  }

  // ////////////////////////////////////////////////////
  // Methodes utiles à la manipulation des liens
  // ////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////
  // POUR TOUS LES LIENS
  // ////////////////////////////////////////////////////
  /**
   * recopie les valeurs de lienACopier dans this
   * @param lienACopier link to copy
   */
  public void copie(Lien lienACopier) {
    this.setObjetsComp(lienACopier.getObjetsComp());
    this.setObjetsRef(lienACopier.getObjetsRef());
    this.setEvaluation(lienACopier.getEvaluation());
    this.setGeom(lienACopier.getGeom());
    this.setIndicateurs(lienACopier.getIndicateurs());
    this.setCorrespondants(lienACopier.getCorrespondants());
    this.setCommentaire(lienACopier.getCommentaire());
    this.setNom(lienACopier.getNom());
    this.setType(lienACopier.getType());
    this.setReference(lienACopier.getReference());
    this.setComparaison(lienACopier.getComparaison());
  }

  // /////////////////////////////////////////
  // Pour calcul de la géométrie des liens
  // /////////////////////////////////////////

  // ////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////
  // ATTENTION
  //
  // LES CODES CI-DESSOUS PERMETTENT DE CREER DES GEOMETRIES
  // COMPLEXES QUI...
  // 1/ SONT UTILES POUR AVOIR UNE REPRESENTATION FINE
  // 2/ MAIS NE SONT PAS TRES BLINDEES (code en cours d'affinage)
  //
  // A UTILSER AVEC PRECAUTION DONC
  // ////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////

  /**
   * Définit des petits tirets entre 2 lignes pour représenter un lien
   * d'appariement
   * @param LS1 a linestring
   * @param LS2 a linestring
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tirets(ILineString LS1,
      ILineString LS2, double pas) {
    double long1, long2;
    int nb_tirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<IOrientableCurve>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = LS1.length();
    long2 = LS2.length();
    nb_tirets = (int) (long1 / pas);
    for (i = 0; i <= nb_tirets; i++) {
      tiret = new GM_LineString();
      pt1 = Operateurs.pointEnAbscisseCurviligne(LS1, i * pas);
      pt2 = Operateurs.pointEnAbscisseCurviligne(LS2, i * pas * long2 / long1);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret.addControlPoint(pt1);
      tiret.addControlPoint(pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit des petits tirets entre 1 ligne et un point pour représenter un
   * lien d'appariement
   * @param LS1 a linestring
   * @param PT a point
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tirets(ILineString LS1,
      IPoint PT, double pas) {
    double long1;
    int nb_tirets;
    IMultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<IOrientableCurve>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = LS1.length();
    nb_tirets = (int) (long1 / pas);
    for (i = 0; i <= nb_tirets; i++) {
      tiret = new GM_LineString();
      pt1 = Operateurs.pointEnAbscisseCurviligne(LS1, i * pas);
      pt2 = PT.getPosition();
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret.addControlPoint(pt1);
      tiret.addControlPoint(pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant les
   * milieux des lignes
   * @param LS1 a linestring
   * @param LS2 a linestring
   * @return link geometry
   */
  public static ILineString tiret(ILineString LS1, ILineString LS2) {
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(Operateurs.milieu(LS1));
    tiret.addControlPoint(Operateurs.milieu(LS2));
    return tiret;
  }

  /**
   * Définit la géométrie d'un lien entre 1 ligne et un point par un trait
   * reliant le milieu de la ligne au point
   * @param LS1 a linestring
   * @param PT a point
   * @return link geometry
   */
  public static ILineString tiret(ILineString LS1, IPoint PT) {
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(Operateurs.milieu(LS1));
    tiret.addControlPoint(PT.getPosition());
    return tiret;
  }

  /**
   * Définit des petits tirets entre 2 lignes pour représenter un lien
   * d'appariement. NB: projete les points sur l'arc LS2, plutot que de se baser
   * sur l'abscisse curviligne
   * @param LS1 a linestring
   * @param LS2 a linestring
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tiretsProjetes(ILineString LS1,
      ILineString LS2, double pas) {
    double long1;
    int nb_tirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<IOrientableCurve>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = LS1.length();
    nb_tirets = (int) (long1 / pas);
    for (i = 0; i <= nb_tirets; i++) {
      tiret = new GM_LineString();
      pt1 = Operateurs.pointEnAbscisseCurviligne(LS1, i * pas);
      pt2 = Operateurs.projection(pt1, LS2);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret.addControlPoint(pt1);
      tiret.addControlPoint(pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit des petits tirets entre 1 ligne et un aggregat pour représenter un
   * lien d'appariement. NB: projete les points sur l'aggregat, plutot que de se
   * baser sur l'abscisse curviligne
   * @param LS1 a linestring
   * @param aggregat an aggregate
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tiretsProjetes(ILineString LS1,
      IAggregate<IGeometry> aggregat, double pas) {
    double long1;
    int nb_tirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<IOrientableCurve>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = LS1.length();
    nb_tirets = (int) (long1 / pas);
    for (i = 0; i <= nb_tirets; i++) {
      tiret = new GM_LineString();
      pt1 = Operateurs.pointEnAbscisseCurviligne(LS1, i * pas);
      pt2 = Operateurs.projection(pt1, aggregat);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret.addControlPoint(pt1);
      tiret.addControlPoint(pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant les
   * lignes
   * @param LS1 a linestring
   * @param LS2 a linestring
   * @return link geometry
   */
  public static ILineString tiretProjete(ILineString LS1, ILineString LS2) {
    IDirectPosition milieu = Operateurs.milieu(LS1);
    IDirectPosition projete = Operateurs.projection(milieu, LS2);
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(milieu);
    tiret.addControlPoint(projete);
    return tiret;
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant la ligne
   * à l'aggregat
   * @param LS1 a linestring
   * @param aggegat an aggregate
   * @return link geometry
   */
  public static ILineString tiretProjete(ILineString LS1,
      IAggregate<IGeometry> aggegat) {
    IDirectPosition milieu = Operateurs.milieu(LS1);
    IDirectPosition projete = Operateurs.projection(milieu, aggegat);
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(milieu);
    tiret.addControlPoint(projete);
    return tiret;
  }

  /** Définit la géométrie d'un lien entre 1 point et son projeté sur la ligne 
  * @param PT a point
   * @param LS2 a linestring
   * @return link geometry
   */
   public static ILineString tiretProjete(IPoint PT, ILineString LS2) {
    IDirectPosition pt = PT.getPosition();
    IDirectPosition projete = Operateurs.projection(pt, LS2);
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(pt);
    tiret.addControlPoint(projete);
    return tiret;
  }

  /** Définit la géométrie d'un lien entre 1 point et son projeté sur l'aggregat * @param point a point
   * @param aggregat an aggregate
   * @return link geometry
   */
  public static ILineString tiretProjete(IPoint PT,
      IAggregate<IGeometry> aggregat) {
    IDirectPosition pt = PT.getPosition();
    IDirectPosition projete = Operateurs.projection(pt, aggregat);
    GM_LineString tiret = new GM_LineString();
    tiret.addControlPoint(pt);
    tiret.addControlPoint(projete);
    return tiret;
  }

  /**
   * Size of the buffer used below.
   */
  private final double bufferSize = 20;

  /**
   * Affecte une géométrie au lien. Cette géométrie est principalement adaptée
   * au cas de l'appariement de réseaux. Attention: peut laisser une geometrie
   * nullle si on ne pointe vers rien (cas des noeuds souvent).
   * 
   * @param tirets true: crée des petits traits régulièrement espacés pour
   *          relier les arcs; false: ne crée pour chaque arc qu'un seul trait
   *          reliant le milieu de l'arc.
   * 
   * @param pas L'écart entre les tirets, le cas échéant
   * */
  public void setGeometrieReseaux(boolean tirets, double pas) {
    Iterator<IFeature> itObjRef, itObjComp;
    IGeometry geomRef = null, geomComp = null;
    boolean refPoint;
    IGeometry buffer;
    IPoint centroide;
    ILineString ligne;
    IMultiCurve<IOrientableCurve> lignes;

    GM_Aggregate<IGeometry> geomLien = new GM_Aggregate<IGeometry>();
    itObjRef = this.getObjetsRef().iterator();
    while (itObjRef.hasNext()) {
      // determination du coté ref
      geomRef = (itObjRef.next()).getGeom();
      if (geomRef instanceof IPoint) {
        refPoint = true;
      } else {
        if (geomRef instanceof ILineString) {
          refPoint = false;
        } else {
          System.out.println("Géométrie réseau: Type de géométrie non géré "
              + geomRef.getClass());
          continue;
        }
      }

      // cas "1 noeud ref --> d'autres choses": 1 tiret + 1 buffer
      if (refPoint) {
        GM_Aggregate<IGeometry> groupe = new GM_Aggregate<IGeometry>();
        itObjComp = this.getObjetsComp().iterator();
        while (itObjComp.hasNext()) {
          // determination du coté comp
          geomComp = (itObjComp.next()).getGeom();
          groupe.add(geomComp);
        }
        buffer = groupe.buffer(this.bufferSize);
        centroide = new GM_Point(buffer.centroid());
        ligne = new GM_LineString();
        ligne.addControlPoint(centroide.getPosition());
        ligne.addControlPoint(((IPoint) geomRef).getPosition());
        geomLien.add(buffer);
        geomLien.add(ligne);
        continue;
      }

      // cas "1 arc ref --> d'autres choses": des tirets
      GM_Aggregate<IGeometry> aggr = new GM_Aggregate<IGeometry>();
      itObjComp = this.getObjetsComp().iterator();
      while (itObjComp.hasNext()) {
        // determination du coté comp
        geomComp = (itObjComp.next()).getGeom();
        aggr.add(geomComp);
      }
      if (tirets) {
        lignes = Lien.tiretsProjetes((ILineString) geomRef, aggr, pas);
        geomLien.add(lignes);
      } else {
        ligne = Lien.tiretProjete((ILineString) geomRef, aggr);
        geomLien.add(ligne);
      }
    }
    if (geomLien.size() != 0) {
      this.setGeom(geomLien);
    }
  }

  // ////////////////////////////////////////////////////
  // POUR LES LIENS VERS DES SURFACES
  // ////////////////////////////////////////////////////
  /**
   * Distance surfacique entre les surfaces du lien ; Methode UNIQUEMENT valable
   * pour des liens pointant vers 1 ou n objets ref et com avec une géométrie
   * SURFACIQUE.
   * @return surface distance
   */
  public double distanceSurfaciqueRobuste() {
    GM_MultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<IOrientableSurface>();
    GM_MultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<IOrientableSurface>();
    IFeature obj;
    IOrientableSurface geometrie;
    Iterator<IFeature> it;

    it = this.getObjetsRef().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomRef.add(geometrie);
    }
    it = this.getObjetsComp().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomComp.add(geometrie);
    }

    return Distances.distanceSurfaciqueRobuste(geomRef, geomComp);
  }

  /**
   * Exactitude (définie par Atef) entre les surfaces du lien ; Methode
   * UNIQUEMENT valable pour des liens pointant vers 1 ou n objets ref et com
   * avec une géométrie SURFACIQUE.
   * @return exactitude
   */
  public double exactitude() {
    GM_MultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<IOrientableSurface>();
    GM_MultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<IOrientableSurface>();
    IFeature obj;
    IOrientableSurface geometrie;
    Iterator<IFeature> it;

    it = this.getObjetsRef().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomRef.add(geometrie);
    }
    it = this.getObjetsComp().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomComp.add(geometrie);
    }

    return Distances.exactitude(geomRef, geomComp);
  }

  /**
   * Exactitude (définie par Atef) entre les surfaces du lien ; Methode
   * UNIQUEMENT valable pour des liens pointant vers 1 ou n objets ref et com
   * avec une géométrie SURFACIQUE.
   * @return completeness
   */
  public double completude() {
    GM_MultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<IOrientableSurface>();
    GM_MultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<IOrientableSurface>();
    IFeature obj;
    IOrientableSurface geometrie;
    Iterator<IFeature> it;

    it = this.getObjetsRef().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomRef.add(geometrie);
    }
    it = this.getObjetsComp().iterator();
    while (it.hasNext()) {
      obj = it.next();
      geometrie = (IOrientableSurface) obj.getGeom();
      if (!(geometrie instanceof ISurface)) {
        return 2;
      }
      geomComp.add(geometrie);
    }

    return Distances.exactitude(geomRef, geomComp);
  }

}
