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
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * Resultat de l'appariement : lien entre des objets homologues de deux bases de
 * données. Un lien a aussi une géométrie qui est sa représentation graphique.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 */
public class Lien extends DefaultFeature {
    
    public Lien() {
        SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
        schemaDefaultFeature.setNom("lien");
        schemaDefaultFeature.setNomSchema("lien");
        this.setSchema(schemaDefaultFeature);
        
        FeatureType newFeatureType = new FeatureType();
        AttributeType type = new AttributeType();
        String memberName = "evaluation"; //$NON-NLS-1$
        String valueType = "Double"; //$NON-NLS-1$
        type.setNomField(memberName);
        type.setMemberName(memberName);
        type.setValueType(valueType);
        newFeatureType.addFeatureAttribute(type);
        
        /*AttributeType type2 = new AttributeType();
        String memberName2 = "commentaire"; //$NON-NLS-1$
        String valueType2 = "String"; //$NON-NLS-1$
        type2.setNomField(memberName2);
        type2.setMemberName(memberName2);
        type2.setValueType(valueType2);
        newFeatureType.addFeatureAttribute(type2);*/
        
        this.setFeatureType(newFeatureType);
    }
    
    
  /**
   * Les objets d'une BD pointés par le lien.
   */
  private List<IFeature> objetsRef = new ArrayList<IFeature>(0);

  /**
   * @return reference object list
   */
  public List<IFeature> getObjetsRef() {
    return this.objetsRef;
  }

  /**
   * @param liste reference object list
   */
  public final void setObjetsRef(final List<IFeature> liste) {
    this.objetsRef = liste;
  }

  /**
   * @param objet reference object
   */
  public final void addObjetRef(final IFeature objet) {
    this.objetsRef.add(objet);
  }

  /**
   * @param objets reference object list
   */
  public final void addObjetsRef(final List<IFeature> objets) {
    this.objetsRef.addAll(objets);
  }

  /**
   * Les objets de l'autre BD pointés par le lien.
   */
  private List<IFeature> objetsComp = new ArrayList<IFeature>(0);

  /**
   * @return comparison object list
   */
  public final List<IFeature> getObjetsComp() {
    return this.objetsComp;
  }

  /**
   * @param liste comparison object list
   */
  public final void setObjetsComp(final List<IFeature> liste) {
    this.objetsComp = liste;
  }

  /**
   * @param objet comparison object to add to the list
   */
  public final void addObjetComp(final IFeature objet) {
    this.objetsComp.add(objet);
  }

  /**
   * @param objets comparison object list to add to the list
   */
  public final void addObjetsComp(final List<IFeature> objets) {
    this.objetsComp.addAll(objets);
  }

  public final String getFeatureListAsString(List<IFeature> features) {
    String result = ""; //$NON-NLS-1$
    for (IFeature feature : features) {
      result += feature.getId() + " "; //$NON-NLS-1$
    }
    return result;
  }

  public final String getObjetsCompAsString() {
    return this.getFeatureListAsString(this.objetsComp);
  }

  public final String getObjetsRefAsString() {
    return this.getFeatureListAsString(this.objetsRef);
  }

  /**
   * Estimation de la qualité du lien d'appariement. Entre 0 et 1 en général
   */
  private double evaluation;

  /**
   * @return matching link evaluation
   */
  public final double getEvaluation() {
    return this.evaluation;
  }

  /**
   * @param eval matching link evaluation
   */
  public final void setEvaluation(final double eval) {
    this.evaluation = eval;
  }

  /**
   * Liste d'indicateurs utilisés pendant les calculs d'appariement.
   */
  private List<Object> indicateurs = new ArrayList<Object>();

  /**
   * @return indicators
   */
  public final List<Object> getIndicateurs() {
    return this.indicateurs;
  }

  /**
   * @param liste indicators
   */
  public final void setIndicateurs(final List<Object> liste) {
    this.indicateurs = liste;
  }

  /**
   * @param objet an indicator
   */
  public final void addIndicateur(final Object objet) {
    this.indicateurs.add(objet);
  }

  /**
   * Texte libre pour décrire le lien d'appariement.
   */
  private String commentaire = new String();

  /**
   * @return comment
   */
  public final String getCommentaire() {
    return this.commentaire;
  }

  /**
   * @param comment comment
   */
  public final void setCommentaire(final String comment) {
    this.commentaire = comment;
  }

  /**
   * Texte libre pour décrire le nom du procesus d'appariement.
   */
  private String nom = new String();

  /**
   * @return matching name
   */
  public final String getNom() {
    return this.nom;
  }

  /**
   * @param name matching name
   */
  public final void setNom(final String name) {
    this.nom = name;
  }

  /**
   * Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud").
   */
  private String type = new String();

  /**
   * @return matching type
   */
  public final String getType() {
    return this.type;
  }

  /**
   * @param aType matching type
   */
  public final void setType(final String aType) {
    this.type = aType;
  }

  /**
   * Texte libre pour décrire les objets de la BD1 pointés.
   */
  private String reference = new String();

  /**
   * @return reference database description
   */
  public final String getReference() {
    return this.reference;
  }

  /**
   * @param aReference reference database description
   */
  public final void setReference(final String aReference) {
    this.reference = aReference;
  }

  /**
   * Texte libre pour décrire les objets de la BD2 pointés.
   */
  private String comparaison = new String();

  /**
   * @return comparison database description
   */
  public final String getComparaison() {
    return this.comparaison;
  }

  /**
   * @param comparison comparison database description
   */
  public final void setComparaison(final String comparison) {
    this.comparaison = comparison;
  }
  private List<Arc> arcs = null;
  public List<Arc> getArcs() {
    return this.arcs;
  }
  public void setArcs(List<Arc> arcs) {
    this.arcs = arcs;
  }

  // ////////////////////////////////////////////////////
  // Methodes utiles à la manipulation des liens
  // ////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////
  // POUR TOUS LES LIENS
  // ////////////////////////////////////////////////////
  /**
   * Recopie les valeurs de lienACopier dans this.
   * @param lienACopier link to copy
   */
  public final void copie(final Lien lienACopier) {
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
   * d'appariement.
   * @param linestring1 a linestring
   * @param linestring2 a linestring
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tirets(
      final ILineString linestring1, final ILineString linestring2,
      final double pas) {
    double long1, long2;
    int nbTirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = linestring1.length();
    long2 = linestring2.length();
    nbTirets = (int) (long1 / pas);
    for (i = 0; i <= nbTirets; i++) {
      pt1 = Operateurs.pointEnAbscisseCurviligne(linestring1, i * pas);
      pt2 = Operateurs.pointEnAbscisseCurviligne(linestring2, i * pas * long2
          / long1);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret = new GM_LineString(pt1, pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit des petits tirets entre 1 ligne et un point pour représenter un
   * lien d'appariement.
   * @param linestring a linestring
   * @param point a point
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tirets(
      final ILineString linestring, final IPoint point, final double pas) {
    double long1;
    int nbTirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = linestring.length();
    nbTirets = (int) (long1 / pas);
    for (i = 0; i <= nbTirets; i++) {
      pt1 = Operateurs.pointEnAbscisseCurviligne(linestring, i * pas);
      pt2 = point.getPosition();
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret = new GM_LineString(pt1, pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant les
   * milieux des lignes.
   * @param linestring1 a linestring
   * @param linestring2 a linestring
   * @return link geometry
   */
  public static ILineString tiret(final ILineString linestring1, final ILineString linestring2) {
    return new GM_LineString(Operateurs.milieu(linestring1), Operateurs
        .milieu(linestring2));
  }

  /**
   * Définit la géométrie d'un lien entre 1 ligne et un point par un trait
   * reliant le milieu de la ligne au point.
   * @param linestring a linestring
   * @param point a point
   * @return link geometry
   */
  public static ILineString tiret(final ILineString linestring, final IPoint point) {
    return new GM_LineString(Operateurs.milieu(linestring), point.getPosition());
  }

  /**
   * Définit des petits tirets entre 2 lignes pour représenter un lien
   * d'appariement. NB: projete les points sur l'arc LS2, plutot que de se baser
   * sur l'abscisse curviligne.
   * @param linestring1 a linestring
   * @param linestring2 a linestring
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tiretsProjetes(
      final ILineString linestring1, final ILineString linestring2,
      final double pas) {
    double long1;
    int nbTirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = linestring1.length();
    nbTirets = (int) (long1 / pas);
    for (i = 0; i <= nbTirets; i++) {
      pt1 = Operateurs.pointEnAbscisseCurviligne(linestring1, i * pas);
      pt2 = Operateurs.projection(pt1, linestring2);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret = new GM_LineString(pt1, pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit des petits tirets entre 1 ligne et un aggregat pour représenter un
   * lien d'appariement. NB: projete les points sur l'aggregat, plutot que de se
   * baser sur l'abscisse curviligne.
   * @param linestring a linestring
   * @param aggregat an aggregate
   * @param pas step
   * @return link geometry
   */
  public static IMultiCurve<IOrientableCurve> tiretsProjetes(
      final ILineString linestring, final IAggregate<IGeometry> aggregat,
      final double pas) {
    double long1;
    int nbTirets;
    GM_MultiCurve<IOrientableCurve> tirets = new GM_MultiCurve<>();
    ILineString tiret;
    IDirectPosition pt1, pt2;
    int i;

    long1 = linestring.length();
    nbTirets = (int) (long1 / pas);
    for (i = 0; i <= nbTirets; i++) {
      pt1 = Operateurs.pointEnAbscisseCurviligne(linestring, i * pas);
      pt2 = Operateurs.projection(pt1, aggregat);
      if (pt1 == null || pt2 == null) {
        continue;
      }
      tiret = new GM_LineString(pt1, pt2);
      tirets.add(tiret);
    }
    return tirets;
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant les
   * lignes.
   * @param linestring1 a linestring
   * @param linestring2 a linestring
   * @return link geometry
   */
  public static ILineString tiretProjete(final ILineString linestring1,
      final ILineString linestring2) {
    IDirectPosition milieu = Operateurs.milieu(linestring1);
    IDirectPosition projete = Operateurs.projection(milieu, linestring2);
    return new GM_LineString(milieu, projete);
  }

  /**
   * Définit la géométrie d'un lien entre 2 lignes par un trait reliant la ligne
   * à l'aggregat.
   * @param linestring a linestring
   * @param aggegat an aggregate
   * @return link geometry
   */
  public static ILineString tiretProjete(final ILineString linestring,
      final IAggregate<IGeometry> aggegat) {
    IDirectPosition milieu = Operateurs.milieu(linestring);
    IDirectPosition projete = Operateurs.projection(milieu, aggegat);
    return new GM_LineString(milieu, projete);
  }

  /**
   * Définit la géométrie d'un lien entre 1 point et son projeté sur la ligne.
   * @param point a point
   * @param linestring a linestring
   * @return link geometry
   */
  public static ILineString tiretProjete(final IPoint point,
      final ILineString linestring) {
    IDirectPosition pt = point.getPosition();
    IDirectPosition projete = Operateurs.projection(pt, linestring);
    return new GM_LineString(pt, projete);
  }

  /**
   * Définit la géométrie d'un lien entre 1 point et son projeté sur l'aggregat.
   * @param point a point
   * @param aggregat an aggregate
   * @return link geometry
   */
  public static ILineString tiretProjete(final IPoint point,
      final IAggregate<IGeometry> aggregat) {
    IDirectPosition pt = point.getPosition();
    IDirectPosition projete = Operateurs.projection(pt, aggregat);
    return new GM_LineString(pt, projete);
  }

  /**
   * Size of the buffer used below.
   */
  private final double bufferSize = 20;

  /**
   * Affecte une géométrie au lien. Cette géométrie est principalement adaptée
   * au cas de l'appariement de réseaux. Attention : peut laisser une géometrie
   * nullle si on ne pointe vers rien (cas des noeuds souvent).
   * 
   * @param tirets true : crée des petits traits régulièrement espacés pour
   *          relier les arcs ; false : ne crée pour chaque arc qu'un seul trait
   *          reliant le milieu de l'arc.
   * 
   * @param pas L'écart entre les tirets, le cas échéant
   */
  public final void setGeometrieReseaux(final boolean tirets, final double pas) {
    Iterator<IFeature> itObjRef, itObjComp;
    IGeometry geomRef, geomComp;
    boolean refPoint;
    IGeometry buffer;
    IPoint centroide;
    ILineString ligne;
    IMultiCurve<IOrientableCurve> lignes;

    GM_Aggregate<IGeometry> geomLien = new GM_Aggregate<>();
    itObjRef = this.getObjetsRef().iterator();
    while (itObjRef.hasNext()) {
      // determination du côté ref
      geomRef = (itObjRef.next()).getGeom();
      if (geomRef instanceof IPoint) {
        refPoint = true;
      } else {
        if (geomRef instanceof ILineString) {
          refPoint = false;
        } else {
          System.out.println("géométrie réseau: " + //$NON-NLS-1$
              "Type de géométrie non géré " //$NON-NLS-1$
              + geomRef.getClass());
          continue;
        }
      }

      // cas "1 noeud ref --> d'autres choses": 1 tiret + 1 buffer
      if (refPoint) {
        GM_Aggregate<IGeometry> groupe = new GM_Aggregate<>();
        itObjComp = this.getObjetsComp().iterator();
        while (itObjComp.hasNext()) {
          // determination du côté comp
          geomComp = (itObjComp.next()).getGeom();
          groupe.add(geomComp);
        }
        buffer = groupe.buffer(this.bufferSize);
        centroide = new GM_Point(buffer.centroid());
        ligne = new GM_LineString(centroide.getPosition(), ((IPoint) geomRef).getPosition());
        geomLien.add(buffer);
        geomLien.add(ligne);
        continue;
      }

      // cas "1 arc ref --> d'autres choses": des tirets
      GM_Aggregate<IGeometry> aggr = new GM_Aggregate<>();
      itObjComp = this.getObjetsComp().iterator();
      while (itObjComp.hasNext()) {
        // determination du côté comp
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
  @SuppressWarnings("unchecked")
  private static IMultiSurface<IOrientableSurface> buildUnionGeometry(List<IFeature> list) {
    List<IOrientableSurface> listRef = new ArrayList<>();
    for (IFeature feat : list) {
      listRef.add((IOrientableSurface) feat.getGeom());
    }
    IGeometry unionRef = JtsAlgorithms.union(listRef);
    IMultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<>();
    if (unionRef instanceof IMultiSurface) {
      geomRef = (IMultiSurface<IOrientableSurface>) unionRef;
    } else {
      if (unionRef instanceof IOrientableSurface) {
        geomRef.add((IOrientableSurface) unionRef);
      }
    }
    return geomRef;
  }
  
  /**
   * Distance surfacique entre les surfaces du lien. Methode UNIQUEMENT valable
   * pour des liens pointant vers 1 ou n objets ref et com avec une géométrie
   * SURFACIQUE.
   * @return surface distance
   */
  public final double distanceSurfaciqueRobuste() {
    IMultiSurface<IOrientableSurface> geomRef = buildUnionGeometry(this.getObjetsRef());
    IMultiSurface<IOrientableSurface> geomComp = buildUnionGeometry(this.getObjetsComp());
    return Distances.distanceSurfaciqueRobuste(geomRef, geomComp);
  }
  
  private double distanceSurfacique = -1;
  public final double getDistanceSurfacique() {
    if (this.distanceSurfacique == -1) {
      this.distanceSurfacique = this.distanceSurfaciqueRobuste();
    }
    return this.distanceSurfacique;
  }
  /**
   * Exactitude (définie par Atef) entre les surfaces du lien. Methode
   * UNIQUEMENT valable pour des liens pointant vers 1 ou n objets ref et com
   * avec une géométrie SURFACIQUE.
   * @return exactitude
   */
  public final double exactitude() {
    IMultiSurface<IOrientableSurface> geomRef = buildUnionGeometry(this.getObjetsRef());
    IMultiSurface<IOrientableSurface> geomComp = buildUnionGeometry(this.getObjetsComp());
    return Distances.exactitude(geomRef, geomComp);
  }
  private double exactitude = -1;
  public final double getExactitude() {
    if (this.exactitude == -1) {
      this.exactitude = this.exactitude();
    }
    return this.exactitude;
  }
  /**
   * Exactitude (définie par Atef) entre les surfaces du lien. Methode
   * UNIQUEMENT valable pour des liens pointant vers 1 ou n objets ref et com
   * avec une géométrie SURFACIQUE.
   * @return completeness
   */
  public final double completude() {
    IMultiSurface<IOrientableSurface> geomRef = buildUnionGeometry(this.getObjetsRef());
    IMultiSurface<IOrientableSurface> geomComp = buildUnionGeometry(this.getObjetsComp());
    return Distances.exactitude(geomComp, geomRef);
  }
  private double completude = -1;
  public final double getCompletude() {
    if (this.completude == -1) {
      this.completude = this.completude();
    }
    return this.completude;
  }
}
