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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Resultats de la réalisation d'un appariement : un ensemble de liens.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 */
public class EnsembleDeLiens extends Population<Lien> {
  /**
   * Constructor.
   */
  public EnsembleDeLiens() {
    
      super(false, "Ensemble de liens", Lien.class, true); //$NON-NLS-1$
    
      /** créer un featuretype pour les arcs */
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
      AttributeType type = new AttributeType();
      String memberName = "evaluation"; //$NON-NLS-1$
      String valueType = "Double"; //$NON-NLS-1$
      type.setNomField(memberName);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
    
      /** création d'un schéma associé au featureType */
      newFeatureType.setGeometryType(IMultiCurve.class);
      this.setFeatureType(newFeatureType);
  
  }

  /**
   * Constructor.
   * @param persistant true if links have to be stored
   */
  public EnsembleDeLiens(final boolean persistant) {
    super(persistant, "Ensemble de liens", Lien.class, true); //$NON-NLS-1$
  }

  /**
   * Constructor.
   * @param classeDesLiens link class
   */
  public EnsembleDeLiens(final Class<?> classeDesLiens) {
    super(false, "Ensemble de liens", classeDesLiens, true); //$NON-NLS-1$
  }

  /**
   * Nom du l'ensemble des liens d'appariement. (ex:
   * "Appariement des routes par la méthode XX")
   */
  private String nom;

  @Override
  public final String getNom() {
    return this.nom;
  }

  @Override
  public final void setNom(final String name) {
    this.nom = name;
  }

  /**
   * Description textuelle des paramètres utilisés pour l'appariement.
   */
  private String parametrage;

  /**
   * @return matching parameters
   */
  public final String getParametrage() {
    return this.parametrage;
  }

  /**
   * @param parameters matching parameters
   */
  public final void setParametrage(final String parameters) {
    this.parametrage = parameters;
  }

  /**
   * Description textuelle du résultat de l'auto-évaluation des liens.
   */
  private String evaluationInterne;

  /**
   * @return internal evaluation
   */
  public final String getEvaluationInterne() {
    return this.evaluationInterne;
  }

  /**
   * @param evaluation internal evaluation
   */
  public final void setEvaluationInterne(final String evaluation) {
    this.evaluationInterne = evaluation;
  }

  /**
   * Description textuelle du résultat de l'évaluation globale des liens.
   */
  private String evaluationGlobale;

  /**
   * @return global evaluation
   */
  public final String getEvaluationGlobale() {
    return this.evaluationGlobale;
  }

  /**
   * @param evaluation global evaluation
   */
  public final void setEvaluationGlobale(final String evaluation) {
    this.evaluationGlobale = evaluation;
  }

  // ////////////////////////////////////////////////////////////
  // METHODES UTILES A LA MANIPULATION DES ENSEMBLES DE LIENS
  // ////////////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////////////
  // MANIPULATION DE BASE DES LISTES D'ELEMENTS
  // ////////////////////////////////////////////////////////////
  /**
   * Copie d'un ensemble de liens.
   * @return a copy of this
   */
  public final EnsembleDeLiens copie() {
    EnsembleDeLiens copie = new EnsembleDeLiens();
    copie.setElements(this.getElements());
    copie.setNom(this.getNom());
    copie.setEvaluationGlobale(this.getEvaluationGlobale());
    copie.setEvaluationInterne(this.getEvaluationInterne());
    return copie;
  }

  /**
   * Compilation de deux listes de liens.
   * @param liens1 set of links
   * @param liens2 set of links
   * @return set of links
   */
  public static final EnsembleDeLiens compile(final EnsembleDeLiens liens1,
      final EnsembleDeLiens liens2) {
    EnsembleDeLiens total = liens1.copie();
    total.compile(liens2);
    return total;
  }

  /**
   * Ajout des liens à this. NB: modifie this.
   * @param liensAAjouter links to add
   */
  public final void compile(final EnsembleDeLiens liensAAjouter) {
    this.getElements().addAll(liensAAjouter.getElements());
  }

  /**
   * Regroupement de liens pointant vers les mêmes objets. Autrement dit : les
   * liens en entrée forment un graphe entre les objets; la méthode crée un seul
   * lien pour toute partie connexe du graphe. exemple : Ref = (A,B,C), Comp =
   * (X,Y,Z) en entrée (this) on a 4 liens 1-1 (A-X) (B-X) (B-Y) (C-Z) en sortie
   * on a un lien n-m (A,B)-(X-Y) et 1 lien 1-1 (C-Z)
   * 
   * @param popRef reference population
   * @param popComp comparison population
   * @return set of links
   */
  public final EnsembleDeLiens regroupeLiens(
      final Population<? extends IFeature> popRef,
      final Population<? extends IFeature> popComp) {
    EnsembleDeLiens liensGroupes;
    Lien lienGroupe;
    CarteTopo grapheDesLiens;
    Noeud noeud;
    IFeature feat;
    grapheDesLiens = this.transformeEnCarteTopo(popRef, popComp);
    Groupe groupeTotal = grapheDesLiens.getPopGroupes().nouvelElement();
    groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
    groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
    groupeTotal.decomposeConnexes();

    liensGroupes = new EnsembleDeLiens();
    liensGroupes.setNom("TMP : liens issus d'un regroupement"); //$NON-NLS-1$
    // on parcours tous les groupes connexes créés
    for (IFeature feature : grapheDesLiens.getListeGroupes()) {
      Groupe groupeConnexe = (Groupe) feature;
      if (groupeConnexe.getListeArcs().size() == 0) {
        continue; // cas des noeuds isolés
      }
      lienGroupe = liensGroupes.nouvelElement();
      for (IFeature n : groupeConnexe.getListeNoeuds()) {
        noeud = (Noeud) n;
        feat = noeud.getCorrespondant(0);
        if (popRef.getElements().contains(feat)) {
          lienGroupe.addObjetRef(feat);
        }
        if (popComp.getElements().contains(feat)) {
          lienGroupe.addObjetComp(feat);
        }
        // nettoyage de la carteTopo créée
        noeud.setCorrespondants(new ArrayList<IFeature>(0));
      }
    }
    return liensGroupes;
  }

  /**
   * Regroupement de liens pointant vers les mêmes objets. Autrement dit : les
   * liens en entrée forment un graphe entre les objets; la méthode crée un seul
   * lien pour toute partie connexe du graphe. exemple : Ref = (A,B,C), Comp =
   * (X,Y,Z) en entrée (this) on a 4 liens 1-1 (A-X) (B-X) (B-Y) (C-Z) en sortie
   * on a un lien n-m (A,B)-(X-Y) et 1 lien 1-1 (C-Z)
   * @param popRef reference population
   * @param popComp comparison population
   * @return set of links
   */
  public final EnsembleDeLiens regroupeLiens(
      final IFeatureCollection<? extends IFeature> popRef,
      final IFeatureCollection<? extends IFeature> popComp) {
    EnsembleDeLiens liensGroupes;
    Lien lienGroupe;
    CarteTopo grapheDesLiens;
    Groupe groupeTotal, groupeConnexe;
    Noeud noeud;
    IFeature feat;
    grapheDesLiens = this.transformeEnCarteTopo(popRef, popComp);
    groupeTotal = grapheDesLiens.getPopGroupes().nouvelElement();
    groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
    groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
    groupeTotal.decomposeConnexes();

    liensGroupes = new EnsembleDeLiens();
    liensGroupes.setNom("TMP : liens issus d'un regroupement"); //$NON-NLS-1$
    // on parcours tous les groupes connexes créés
    for (IFeature feature : grapheDesLiens.getListeGroupes()) {
      groupeConnexe = (Groupe) feature;
      if (groupeConnexe.getListeArcs().size() == 0) {
        continue; // cas des noeuds isolés
      }
      lienGroupe = liensGroupes.nouvelElement();
      for (IFeature n : groupeConnexe.getListeNoeuds()) {
        noeud = (Noeud) n;
        feat = noeud.getCorrespondant(0);
        if (popRef.getElements().contains(feat)) {
          lienGroupe.addObjetRef(feat);
        }
        if (popComp.getElements().contains(feat)) {
          lienGroupe.addObjetComp(feat);
        }
        // nettoyage de la carteTopo créée
        noeud.setCorrespondants(new ArrayList<IFeature>(0));
      }
    }
    return liensGroupes;
  }

  /**
   * Transforme les liens, qui relient des objets de popRef et popComp, en une
   * carte topo (graphe sans géométrie) où : - les objets de popRef et popComp
   * sont des noeuds (sans géométrie), - les liens sont des arcs entre ces
   * noeuds (sans géométrie).
   * @param popRef reference population
   * @param popComp comparison population
   * @return topological map
   */
  public final CarteTopo transformeEnCarteTopo(
      final IFeatureCollection<? extends IFeature> popRef,
      final IFeatureCollection<? extends IFeature> popComp) {
    Iterator<IFeature> itNoeudsRef, itNoeudsComp;
    List<IFeature> noeudsRef = new ArrayList<IFeature>(0), noeudsComp = new ArrayList<IFeature>(0);

    Lien lien;
    CarteTopo grapheDesLiens = new CarteTopo("Carte de liens"); //$NON-NLS-1$
    IFeature objetRef, objetComp;
    Noeud noeudRef, noeudComp;
    Arc arc;

    // création de noeuds du graphe = les objets ref et comp
    Iterator<? extends IFeature> itObjetsRef = popRef.iterator();
    while (itObjetsRef.hasNext()) {
      objetRef = itObjetsRef.next();
      noeudRef = grapheDesLiens.getPopNoeuds().nouvelElement();
      noeudRef.addCorrespondant(objetRef);
    }
    Iterator<? extends IFeature> itObjetsComp = popComp.iterator();
    while (itObjetsComp.hasNext()) {
      objetComp = itObjetsComp.next();
      noeudComp = grapheDesLiens.getPopNoeuds().nouvelElement();
      noeudComp.addCorrespondant(objetComp);
    }

    // création des arcs du graphe = les liens d'appariement
    Iterator<Lien> itLiens = this.getElements().iterator();
    while (itLiens.hasNext()) {
      lien = itLiens.next();
      itObjetsRef = lien.getObjetsRef().iterator();
      // création des listes de noeuds concernés par le lien
      noeudsRef.clear();
      while (itObjetsRef.hasNext()) {
        objetRef = itObjetsRef.next();
        Collection<IFeature> correspondants = objetRef.getCorrespondants(grapheDesLiens.getPopNoeuds());
        noeudsRef.add(correspondants.iterator().next());
      }
      itObjetsComp = lien.getObjetsComp().iterator();
      noeudsComp.clear();
      while (itObjetsComp.hasNext()) {
        objetComp = itObjetsComp.next();
        Collection<IFeature> correspondants = objetComp.getCorrespondants(grapheDesLiens.getPopNoeuds());
        noeudsComp.add(correspondants.iterator().next());
      }

      // instanciation des arcs
      itNoeudsRef = noeudsRef.iterator();
      while (itNoeudsRef.hasNext()) {
        noeudRef = (Noeud) itNoeudsRef.next();
        itNoeudsComp = noeudsComp.iterator();
        while (itNoeudsComp.hasNext()) {
          noeudComp = (Noeud) itNoeudsComp.next();
          arc = grapheDesLiens.getPopArcs().nouvelElement();
          arc.addCorrespondant(lien);
          arc.setNoeudIni(noeudRef);
          arc.setNoeudFin(noeudComp);
        }
      }
    }
    return grapheDesLiens;
  }

  /**
   * Methode de regroupement des liens 1-1 carto qui pointent sur le meme objet
   * topo; non optimisee du tout!!!
   * @return EnsembleDeLiens
   */
  public final EnsembleDeLiens regroupeLiensCartoQuiPointentSurMemeTopo() {
    List<Lien> liens = this.getElements();
    List<Integer> remove = new ArrayList<Integer>();
    List<IFeature> objetsRef = new ArrayList<IFeature>(0), objetsComp = new ArrayList<IFeature>(0);
    Object objetTest;
    int i, j, k;

    for (i = 0; i < liens.size(); i++) {
      objetsComp.add(liens.get(i).getObjetsComp().get(0));
      objetsRef.add(liens.get(i).getObjetsRef().get(0));
    }

    // regroupement des liens
    for (j = 0; j < liens.size(); j++) {
      objetTest = objetsComp.get(j);
      for (k = j; k < liens.size(); k++) {
        if (objetTest.equals(objetsComp.get(k)) && j != k) {
          liens.get(j).addObjetRef(objetsRef.get(k));
          if (!remove.contains(k)) {
            remove.add(k);
          }
        }
      }
    }

    // destruction des liens superflus
    SortedSet<Integer> s = Collections
        .synchronizedSortedSet(new TreeSet<Integer>());
    s.addAll(remove);
    remove = new ArrayList<Integer>(s);
    for (i = s.size() - 1; i >= 0; i--) {
      liens.remove((remove.get(i)).intValue());
    }
    return this;
  }

  // ////////////////////////////////////////////////////////////
  // AUTOUR DE L'EVALUATION
  // ////////////////////////////////////////////////////////////

  /**
   * Filtrage des liens, on ne retient que ceux dont l'évaluation est supérieure
   * ou égale au seuil passé en paramètre.
   * @param seuilEvaluation evaluation threshold
   */
  public final void filtreLiens(final float seuilEvaluation) {
    for (Lien lien : this.getElements()) {
      if (lien.getEvaluation() < seuilEvaluation) {
        this.enleveElement(lien);
      }
    }
  }

  /**
   * Change l'évaluation d'un lien si celui-ci a un nombre d'objets associés à
   * la base de comparaison (flag=true) ou à la base de référence (flag=false)
   * supérieur au seuilCardinalite, en lui donnant la valeur nulle. Si le seuil
   * n'est pas dépassé, l'évaluation du lien reste ce qu'elle est.
   * @param flag : true si l'on s'intéresse aux objets de la base de comparaison
   *          des liens, false s'il s'agit de la base de référence
   * @param seuilCardinalite : seuil au dessus duquel la méthode affecte une
   *          évaluation nulle au lien
   */
  public final void evaluationLiensParCardinalite(final boolean flag,
      final double seuilCardinalite) {
    if (flag) {
      for (Lien lien : this.getElements()) {
        if (lien.getObjetsComp().size() > seuilCardinalite) {
          lien.setEvaluation(0);
        }
      }
    } else {
      for (Lien lien : this.getElements()) {
        if (lien.getObjetsRef().size() > seuilCardinalite) {
          lien.setEvaluation(0);
        }
      }
    }
  }

  /**
   * Crée une liste de population en fonction des seuils sur l'evaluation passés
   * en paramètre. 
   * Exemple: si la liste en entree contient 2 "Double" (0.5, 1), alors renvoie 3 populations avec les liens 
   * ayant respectivement leur évaluation... 
   *      0: inférieur à 0.5 (strictement) 
   *      1: entre 0.5 et 1 (strictement sur 1) 
   *      2: supérieur ou égal à 1
   * 
   * @param valeursClassement sorting values
   * @return a list of sets of links
   */
  public final List<EnsembleDeLiens> classeSelonSeuilEvaluation(
      final List<Double> valeursClassement) {
    List<EnsembleDeLiens> liensClasses = new ArrayList<EnsembleDeLiens>(0);
    Iterator<Lien> itLiens = this.getElements().iterator();
    Lien lien, lienClasse;
    double seuil;
    int i;
    boolean trouve;

    for (i = 0; i <= valeursClassement.size(); i++) {
      liensClasses.add(new EnsembleDeLiens());
    }
    while (itLiens.hasNext()) {
      lien = itLiens.next();
      trouve = false;
      for (i = 0; i < valeursClassement.size(); i++) {
        seuil = (valeursClassement.get(i)).doubleValue();
        if (lien.getEvaluation() < seuil) {
          lienClasse = liensClasses.get(i).nouvelElement();
          lienClasse.setEvaluation(lien.getEvaluation());
          lienClasse.setGeom(lien.getGeom());
          lienClasse.setCommentaire(lien.getCommentaire());
          lienClasse.setCorrespondants(lien.getCorrespondants());
          lienClasse.setObjetsComp(lien.getObjetsComp());
          lienClasse.setObjetsRef(lien.getObjetsRef());
          lienClasse.setIndicateurs(lien.getIndicateurs());
          trouve = true;
          break;
        }
      }
      if (trouve) {
        continue;
      }
      lienClasse = liensClasses.get(valeursClassement.size()).nouvelElement();
      lienClasse.setEvaluation(lien.getEvaluation());
      lienClasse.setGeom(lien.getGeom());
      lienClasse.setCommentaire(lien.getCommentaire());
      lienClasse.setCorrespondants(lien.getCorrespondants());
      lienClasse.setObjetsComp(lien.getObjetsComp());
      lienClasse.setObjetsRef(lien.getObjetsRef());
      lienClasse.setIndicateurs(lien.getIndicateurs());
    }
    return liensClasses;
  }

  // ////////////////////////////////////////////////////////////
  // AUTOUR DE LA GEOMETRIE ET DE LA VISUALISATION
  // ////////////////////////////////////////////////////////////

  /**
   * Affecte une géométrie à l'ensemble des liens, cette géométrie relie les
   * centroïdes des objets concernés entre eux.
   */
  public final void creeGeometrieDesLiens() {
    Iterator<IFeature> itRef, itComp;
    IFeature ref, comp;
    for (Lien lien : this.getElements()) {
      itRef = lien.getObjetsRef().iterator();
      GM_Aggregate<GM_Object> geom = new GM_Aggregate<GM_Object>();
      while (itRef.hasNext()) {
        ref = itRef.next();
        itComp = lien.getObjetsComp().iterator();
        while (itComp.hasNext()) {
          comp = itComp.next();
          GM_LineString ligne = new GM_LineString(ref.getGeom().centroid(), comp.getGeom()
              .centroid());
          geom.add(ligne);
        }
      }
      lien.setGeom(geom);
    }
  }

  /**
   * Affecte une géométrie à l'ensemble des liens, cette géométrie relie le
   * milieu d'une ligne au milieu d'une ligne correspondant des objets.
   */
  public final void creeGeometrieDesLiensEntreLignesEtLignes() {
    Iterator<IFeature> itRef, itComp;
    IFeature ref, comp;
    GM_LineString lineStringLien;
    GM_Aggregate<GM_Object> geom;
    IDirectPosition dpRef;

    for (Lien lien : this.getElements()) {
      geom = new GM_Aggregate<GM_Object>();
      itRef = lien.getObjetsRef().iterator();
      while (itRef.hasNext()) {
        ref = itRef.next();
        dpRef = Operateurs.milieu((ILineString) ref.getGeom());
        itComp = lien.getObjetsComp().iterator();
        while (itComp.hasNext()) {
          comp = itComp.next();
          lineStringLien = new GM_LineString(dpRef, Operateurs.milieu((GM_LineString) comp
              .getGeom()));
          geom.add(lineStringLien);
        }
      }
      lien.setGeom(geom);
    }
  }

  /**
   * Affecte une géométrie à l'ensemble des liens, cette géométrie relie le
   * centroide d'une surface au milieu du segment correspondant des objets.
   * @param comparaison : true si les objets de la BD de comparaison sont des
   *          lignes; false s'il s'agit des objets de la BD de référence
   */
  public final void creeGeometrieDesLiensEntreSurfacesEtLignes(
      final boolean comparaison) {
    Iterator<IFeature> itRef, itComp;
    IFeature ref, comp;

    for (Lien lien : this.getElements()) {
      itRef = lien.getObjetsRef().iterator();
      GM_Aggregate<GM_Object> geom = new GM_Aggregate<GM_Object>();
      while (itRef.hasNext()) {
        ref = itRef.next();
        itComp = lien.getObjetsComp().iterator();
        while (itComp.hasNext()) {
          comp = itComp.next();
          List<IDirectPosition> points = new ArrayList<IDirectPosition>();
          if (comparaison) {
            GM_Point centroideSurface = new GM_Point(ref.getGeom().centroid());
            double pointCentral = Math.floor(((GM_LineString) comp.getGeom())
                .numPoints() / 2) - 1;
            GM_Point pointMilieu = new GM_Point(
                ((GM_LineString) comp.getGeom())
                    .getControlPoint((int) pointCentral));
            points.add(centroideSurface.getPosition());
            points.add(pointMilieu.getPosition());
            geom.add(new GM_LineString(points));
          } else {
            GM_Point centroideSurface = new GM_Point(comp.getGeom().centroid());
            double pointCentral = Math.floor(((GM_LineString) ref.getGeom())
                .numPoints() / 2) - 1;
            GM_Point pointMilieu = new GM_Point(
                ((GM_LineString) ref.getGeom())
                    .getControlPoint((int) pointCentral));
            points.add(centroideSurface.getPosition());
            points.add(pointMilieu.getPosition());
            geom.add(new GM_LineString(points));
          }
        }
      }
      lien.setGeom(geom);
    }
  }
  /**
   * Affecte une géométrie à l'ensemble des liens, cette géométrie relie le
   * centroïde d'une surface à un point.
   * @param comparaison : true si les objets de la BD de comparaison sont des
   *          points; false s'il s'agit des objets de la BD de référence
   */
  public final void creeGeometrieDesLiensEntreSurfacesEtPoints(
      final boolean comparaison) {
    Iterator<Lien> itLiens = this.getElements().iterator();
    Iterator<IFeature> itRef, itComp;
    Lien lien;
    IFeature ref, comp;

    while (itLiens.hasNext()) {
      lien = itLiens.next();
      itRef = lien.getObjetsRef().iterator();
      GM_Aggregate<GM_Object> geom = new GM_Aggregate<GM_Object>();
      while (itRef.hasNext()) {
        ref = itRef.next();
        itComp = lien.getObjetsComp().iterator();
        while (itComp.hasNext()) {
          comp = itComp.next();
          List<IDirectPosition> points = new ArrayList<IDirectPosition>();
          if (comparaison) {
            GM_Point centroideSurface = new GM_Point(ref.getGeom().centroid());
            points.add(centroideSurface.getPosition());
            points.add(((GM_Point) comp.getGeom()).getPosition());
            geom.add(new GM_LineString(points));
          } else {
            GM_Point centroideSurface = new GM_Point(comp.getGeom().centroid());
            points.add(centroideSurface.getPosition());
            points.add(((GM_Point) ref.getGeom()).getPosition());
            geom.add(new GM_LineString(points));
          }
        }
      }
      lien.setGeom(geom);
    }
  }

  /**
   * Détruit la géométrie des liens.
   */
  public final void detruitGeometrieDesLiens() {
    Iterator<Lien> itLiens = this.getElements().iterator();
    Lien lien;

    while (itLiens.hasNext()) {
      lien = itLiens.next();
      GM_Aggregate<GM_Object> geom = new GM_Aggregate<GM_Object>();
      lien.setGeom(geom);
    }
  }

  // ////////////////////////////////////////////////////////////
  // AUTOUR DE LA VISUALISATION OBJETS APPARIES / NON APPARIES
  // ////////////////////////////////////////////////////////////

  /**
   * Méthode qui renvoie à partir d'un ensemble de liens une liste de dimension
   * 4, avec en 1. la population issue de la population de référence qui a été
   * appariée, en 2. la population issue de la population de comparaison qui a
   * été appariée, en 3. la population issue de la population de référence qui
   * n'a pas été appariée, en 4. la population issue de la population de
   * comparaison qui n'a pas été appariée.
   * @param ensemble ensemble de liens issu d'un appariement
   * @param popRef reference population
   * @param popComp comparison population
   * @return liste des populations appariées et non appariées
   */
  public static List<IPopulation<IFeature>> objetsApparies(
      final EnsembleDeLiens ensemble,
      final IFeatureCollection<? extends IFeature> popRef,
      final IFeatureCollection<? extends IFeature> popComp) {
    Population<IFeature> popCompAppariee = new Population<IFeature>(), popCompNonAppariee = new Population<IFeature>(), popRefAppariee = new Population<IFeature>(), popRefNonAppariee = new Population<IFeature>();

    IFeature elementPopComp, elementPopRef;

    // ajout des éléments appariés dans les populations concernées
    for (Lien lien : ensemble.getElements()) {
      List<IFeature> elementsComp = lien.getObjetsComp();
      List<IFeature> elementsRef = lien.getObjetsRef();

      Iterator<? extends IFeature> itComp = elementsComp.iterator();
      Iterator<? extends IFeature> itRef = elementsRef.iterator();

      while (itComp.hasNext()) {
        elementPopComp = itComp.next();
        popCompAppariee.add(elementPopComp);
      }

      while (itRef.hasNext()) {
        elementPopRef = itRef.next();
        popRefAppariee.add(elementPopRef);
      }
    }

    // copie des populations comp et ref dans les populations non appariées
    // popCompNonAppariee.copiePopulation(popComp);
    // popRefNonAppariee.copiePopulation(popRef);
    popCompNonAppariee.setElements(popComp);
    popRefNonAppariee.setElements(popRef);

    Iterator<IFeature> itPopCompApp = popCompAppariee.getElements().iterator();
    Iterator<IFeature> itPopRefApp = popRefAppariee.getElements().iterator();

    // élimination dans les populations non appariées des éléments appariés
    // contenus dans les populations appariés afin d'obtenir les
    // complémentaires
    while (itPopCompApp.hasNext()) {
      IFeature elementAOter = itPopCompApp.next();
      popCompNonAppariee.remove(elementAOter);
    }

    while (itPopRefApp.hasNext()) {
      IFeature elementAOter = itPopRefApp.next();
      popRefNonAppariee.remove(elementAOter);
    }

    List<IPopulation<IFeature>> listPopulation = new ArrayList<IPopulation<IFeature>>(0);
    listPopulation.add(popRefAppariee);
    listPopulation.add(popCompAppariee);
    listPopulation.add(popRefNonAppariee);
    listPopulation.add(popCompNonAppariee);

    return listPopulation;
  }

  /**
   * Methode utile principalement pour analyser les résultats d'un appariement,
   * qui découpe un réseau en plusieurs réseaux selon les valeurs de l'attribut
   * "resultatAppariement" des arcs et noeuds du réseau apparié.
   * @param valeursClassement sorting values
   * @return a list of sets of links
   */
  public final List<EnsembleDeLiens> scindeSelonValeursCommentaires(
      final List<String> valeursClassement) {
    List<EnsembleDeLiens> liensClasses = new ArrayList<EnsembleDeLiens>(0);
    int i;
    for (i = 0; i < valeursClassement.size(); i++) {
      liensClasses.add(new EnsembleDeLiens());
    }
    for (Lien lien : this.getElements()) {
      for (i = 0; i < valeursClassement.size(); i++) {
        if (lien.getCommentaire() == null) {
          continue;
        }
        if (lien.getCommentaire().startsWith(valeursClassement.get(i))) {
          (liensClasses.get(i)).add(lien);
        }
      }
    }
    return liensClasses;
  }

  @Override
  public void clear() {
    super.clear();
  }
}
