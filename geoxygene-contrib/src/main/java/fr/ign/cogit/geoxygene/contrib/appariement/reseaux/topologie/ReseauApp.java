/*
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
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Réseau à apparier, spécialisation d'une carte topo, utile quasi-uniquement
 * pour spécifier un constructeur adhoc.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class ReseauApp extends CarteTopo {
  
  /**
   * Constructeur par défaut : ATTENTION, constructeur à éviter car aucune
   * population n'est créée
   */
  public ReseauApp() {
    this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
  }

  /**
   * Constructeur qui créé une carte topo non persistante de type Reseau_App,
   * avec des populations de Arc_App, Noeud_App, Face_App, et Groupe_App
   */
  public ReseauApp(String nom_logique) {
    this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
    this.setNom(nom_logique);
    this.setPersistant(false);
    
    Population<ArcApp> arcs = new Population<ArcApp>(false, "Edge", ArcApp.class, true);
    
    /** créer un featuretype pour les arcs */
    FeatureType featureType = new FeatureType();
    
    /** création d'un schéma associé au featureType */
    featureType.setGeometryType(GM_LineString.class);
    arcs.setFeatureType(featureType);
    this.addPopulation(arcs);
    Population<NoeudApp> noeuds = new Population<NoeudApp>(false, "Node", NoeudApp.class, true);
    
    /** créer un featuretype pour les noeuds */
    featureType = new FeatureType();
    
    /** création d'un schéma associé au featureType */
    featureType.setGeometryType(GM_Point.class);
    noeuds.setFeatureType(featureType);
    this.addPopulation(noeuds);
    Population<FaceApp> faces = new Population<FaceApp>(false, "Face", FaceApp.class, true);
    
    /** créer un featuretype pour les faces */
    featureType = new FeatureType();
    
    /** création d'un schéma associé au featureType */
    featureType.setGeometryType(GM_Polygon.class);
    faces.setFeatureType(featureType);
    this.addPopulation(faces);
    Population<GroupeApp> groupes = new Population<GroupeApp>(false, "Group", GroupeApp.class, false);
    this.addPopulation(groupes);
  }

  /**
   * Set the default size of the nodes to param.distanceNoeudsMax.
   * <p>
   * On affecte une taille par défaut à tous nouveaux sans taille.
   * La valeur affectée est param.distanceNoeudsMax.
   * @param param matching parameters
   * @deprecated
   */
  @Deprecated
  public void instancieAttributsNuls(ParametresApp param) {
    for (Noeud node : this.getPopNoeuds()) {
      NoeudApp noeud = (NoeudApp) node;
      if (noeud.getTaille() == 0) {
        noeud.setTaille(param.distanceNoeudsMax);
      }
    }
  }
  
  public void instancieAttributsNuls(float distanceNoeudsMax) {
    for (Noeud node : this.getPopNoeuds()) {
      NoeudApp noeud = (NoeudApp) node;
      if (noeud.getTaille() == 0) {
        noeud.setTaille(distanceNoeudsMax);
      }
    }
  }
  
  /**
   * Idem + transfert des attributs
   * 
   * @param tolerance
   * @param schemaDefaultFeature
   */
  @SuppressWarnings("unchecked")
  public void rendPlanaire2(double tolerance) {

      // si pas d'arc, c'est planaire
      if (this.getPopArcs().isEmpty()) {
          return;
      }
      
      // System.out.println("Schema = " + this.getPopArcs().getFeatureType().getFeatureAttributes().size());
      
      List<IFeature> dejaTraites = new ArrayList<IFeature>();
      List<Arc> arcsEnleves = new ArrayList<Arc>(0);

      // initialisation de l'index des arcs au besoin
      if (!this.getPopArcs().hasSpatialIndex()) {
          this.getPopArcs().initSpatialIndex(Tiling.class, true);
      } else {
          // force the automatic update of the index if it exists
          this.getPopArcs().getSpatialIndex().setAutomaticUpdate(true);
      }

      this.fireActionPerformed(new ActionEvent(this, 0,
              I18N.getString("CarteTopo.PlanarGraphCreation"), this.getPopArcs().size())); //$NON-NLS-1$

      for (int indexArc = 0; indexArc < this.getPopArcs().size(); indexArc++) {

          Arc currentEdge = this.getPopArcs().get(indexArc);
          
          if (arcsEnleves.contains(currentEdge) || dejaTraites.contains(currentEdge)) {
              // CarteTopo.logger.debug("Already handled or removed");
              // si on a déjà traité l'arc ou qu'on l'a déjà découpé, passer
              // au suivant
              continue;
          }

          // les arcs qui croisent l'arc courant
          // Optimisation et blindage pour tous les cas non garanti (Seb)
          Collection<Arc> selection = this.getPopArcs().select(currentEdge.getGeometrie());
          
          // on enlève l'arc courant et les arcs déjà enlevés
          selection.remove(currentEdge);
          selection.removeAll(arcsEnleves);
          // selection.removeAll(dejaTraites); // ADDED
          
          CarteTopo.logger.debug(selection.size() + " Edges intersected");
          List<Arc> listeInter = new ArrayList<Arc>(0);
          // On construit un multipoint contenant les extrémités de l'arc courant
          GM_MultiPoint frontiereArc = new GM_MultiPoint();
          frontiereArc.add(new GM_Point(currentEdge.getGeometrie().startPoint()));
          frontiereArc.add(new GM_Point(currentEdge.getGeometrie().endPoint()));

          // pour chaque arc qui intersecte l'arc courant
          for (Arc arcSel : selection) {
              // On construit un multipoint contenant les extrémités de l'arc
              // de la sélection
              if (CarteTopo.logger.isDebugEnabled()) {
                  CarteTopo.logger.debug("Treating selected edge " + arcSel); //$NON-NLS-1$
              }
              GM_MultiPoint frontiereArcSel = new GM_MultiPoint();
              frontiereArcSel.add(new GM_Point(arcSel.getGeometrie().startPoint()));
              frontiereArcSel.add(new GM_Point(arcSel.getGeometrie().endPoint()));
              // on calcule l'intersection de l'arc courant avec l'arc de la sélection
              IGeometry intersection = arcSel.getGeometrie().intersection(currentEdge.getGeometrie());
              CarteTopo.logger.debug("Intersection " + intersection); //$NON-NLS-1$
             
              // si l'intersection trouvée fait partie des extrémités des 2 arcs,
              // alors on passe à l'arc suivant
              if ((!(intersection instanceof GM_Aggregate) || (intersection instanceof GM_MultiPoint))
                      && frontiereArc.contains(intersection) && frontiereArcSel.contains(intersection)) {
                  continue;
              }
              // on a une intersection ailleurs que sur une extrémité
              listeInter.add(arcSel);
          }
          if (listeInter.isEmpty()) {
              this.fireActionPerformed(new ActionEvent(this, 1, I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
              continue; // pas d'intersection avec cet arc
          }

          // on découpe tout
          IGeometry nodedLineStrings = currentEdge.getGeometrie();
          for (Arc edge : listeInter) {
              if (!edge.getGeometrie().isEmpty()) {
                  try {
                      nodedLineStrings = nodedLineStrings.union(edge.getGeometrie());
                  } catch (Exception e) {
                      CarteTopo.logger.error("Crappy edge: " + edge.getGeometrie().toString()); //$NON-NLS-1$            
                  }
              }
          }
          listeInter.add(currentEdge); // on le rajoute pour la suite
          // 
          if (nodedLineStrings instanceof ILineString) {
              boolean toutesEgales = true;
              for (Arc arcSel : listeInter) {
                  toutesEgales = toutesEgales && arcSel.getGeometrie().equals(nodedLineStrings);
              }
              if (toutesEgales) {
                  CarteTopo.logger.debug(I18N.getString("CarteTopo.EqualGeometries")); //$NON-NLS-1$
                  
                  Arc arcNouveau = this.getPopArcs().nouvelElement(nodedLineStrings);
                  if (arcNouveau.getId() == 0) {
                      logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
                  }
                  
                  boolean premierArc = true;
                  for (Arc arcSel : listeInter) {
                      arcNouveau.addAllCorrespondants(arcSel.getCorrespondants());
                      arcsEnleves.add(arcSel);
                      if (premierArc) {
                          // on affecte la valeur initiale de l'orientation
                          // avec le premier arc rencontré
                          // TODO : Check that it is the correct one
                          arcNouveau.setOrientation(arcSel.getOrientation());
                          
                          // Transfert des attributs sur le premier arc trouve                           
                          arcNouveau.setSchema(arcSel.getSchema());
                          if (arcSel.getFeatureType() != null && arcSel.getFeatureType().getFeatureAttributes() != null) {
                              Object[] valAttribute = new Object[arcSel.getFeatureType().getFeatureAttributes().size()];
                              for (int j = 0; j < arcSel.getFeatureType().getFeatureAttributes().size(); j++) {
                                  GF_AttributeType attributeType = arcSel.getFeatureType().getFeatureAttributes().get(j);
                                  String name = attributeType.getMemberName();
                                  valAttribute[j] = arcSel.getAttribute(name);
                              }
                              arcNouveau.setAttributes(valAttribute);
                          }
                         
                          premierArc = false;
                      } else {
                          // ensuite, si la valeur diffère de la valeur
                          // initale, on met l'orientation dans les deux sens
                          if (arcNouveau.getOrientation() != arcSel.getOrientation()) {
                              arcNouveau.setOrientation(2);// TODO Check that too
                          }
                          
                          // TODO : et pour les attributs, que faire
                      }
                  }
                  // le nouvel arc possède la même géométrie que l'arc
                  // initial, pas la peine de revenir
                  dejaTraites.add(arcNouveau);
              
              } else {
                  CarteTopo.logger.error(I18N.getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
                  CarteTopo.logger.error(I18N.getString("CarteTopo.IntersectionProblem")); //$NON-NLS-1$
                  CarteTopo.logger.error(I18N.getString("CarteTopo.EdgeProblem") + currentEdge); //$NON-NLS-1$
                  CarteTopo.logger.error(I18N.getString("CarteTopo.UnionWithSeveralEdges")); //$NON-NLS-1$
                  for (Arc a : listeInter) {
                      CarteTopo.logger.error(this.getPopArcs().contains(a) + " - " + a); //$NON-NLS-1$
                      CarteTopo.logger.error(a.getNoeudIni());
                      CarteTopo.logger.error(a.getNoeudFin());
                  }
              }
              this.fireActionPerformed(new ActionEvent(this, 1, I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
              continue;
          }
          
          // cas où il faut découper
          if (nodedLineStrings instanceof IMultiCurve) { 
              
              // 1: on rajoute les morceaux d'arcs découpés
              for (ILineString ligneDecoupe : (IMultiCurve<ILineString>) nodedLineStrings) {
                  
                  Arc arcNouveau = this.getPopArcs().nouvelElement(ligneDecoupe);
                  if (arcNouveau.getId() == 0) {
                      logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
                  }
                  
                  // on recherche à quel(s) arc(s) initial appartient chaque bout découpé
                  for (Arc arcSel : listeInter) {
                      // on devrait mettre == 0 ci-dessous, mais pour gérer
                      // les erreurs d'arrondi on met <0.01
                      if (Distances.premiereComposanteHausdorff(ligneDecoupe, arcSel.getGeometrie()) < 0.01) {
                          
                          // on appartient à lui
                          arcNouveau.addAllCorrespondants(arcSel.getCorrespondants());
                          arcNouveau.setOrientation(arcSel.getOrientation());
                          
                          // Transfert des attributs : on a perdu l'original !!!!
                          arcNouveau.setSchema(arcSel.getSchema());
                          if (arcSel.getFeatureType() != null && arcSel.getFeatureType().getFeatureAttributes() != null) {
                              Object[] valAttribute = new Object[arcSel.getFeatureType().getFeatureAttributes().size()];
                              for (int j = 0; j < arcSel.getFeatureType().getFeatureAttributes().size(); j++) {
                                  GF_AttributeType attributeType = arcSel.getFeatureType().getFeatureAttributes().get(j);
                                  String name = attributeType.getMemberName();
                                  valAttribute[j] = arcSel.getAttribute(name);
                              }
                              arcNouveau.setAttributes(valAttribute);
                          }
                          
                          // si on appartient à l'arc initial, pas la peine de
                          // revenir
                          if (arcSel == currentEdge) {
                              dejaTraites.add(arcNouveau);
                          }
                      }
                  }
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger.debug(I18N.getString("CarteTopo.NewEdge") + " " + arcNouveau); //$NON-NLS-1$
                  }
              }
              
              // 2: on virera les arcs initiaux qui ont été découpés
              for (Arc arcSel : listeInter) {
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger.debug(I18N.getString("CarteTopo.IntersectionFound") + arcSel); //$NON-NLS-1$
                  }
                  arcSel.setCorrespondants(new ArrayList<IFeature>(0));
                  arcsEnleves.add(arcSel);
              }
              
              this.fireActionPerformed(new ActionEvent(this, 1, I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
              continue;
          }
          
          // cas imprévu: OUPS
          CarteTopo.logger.error(I18N.getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
          CarteTopo.logger.error(I18N.getString("CarteTopo.UnionBug") + nodedLineStrings.getClass()); //$NON-NLS-1$
          CarteTopo.logger.error(I18N.getString("CarteTopo.EdgeProblem") + currentEdge.getGeom().coord()); //$NON-NLS-1$
      }
      
      if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Removing " + arcsEnleves.size() + " edges");
      }
      this.enleveArcs(arcsEnleves);
      // On construit les nouveaux noeuds éventuels et la topologie arcs/noeuds
      this.getPopNoeuds().setElements(new ArrayList<Noeud>());
      if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Creating missing nodes");
      }
      this.creeNoeudsManquants(tolerance);
      /**
       * vérification des arcs qui s'intersectent presque à moins de tolérance
       * FIXME ATTENTION : ce bout de code est à nettoyer et à corriger éventuellement.
       */
      if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Checking nodes");
      }
      for (Noeud noeud : this.getPopNoeuds()) {
          if (noeud.arcs().size() == 1) {
              Collection<Arc> arcs = this.getPopArcs().select(noeud.getGeom().buffer(tolerance));
              arcs.removeAll(noeud.arcs());
              if (!arcs.isEmpty()) {
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger.debug(I18N.getString("CarteTopo.HandlingNode") + noeud); //$NON-NLS-1$
                  }
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger
                              .debug(I18N.getString("CarteTopo.NumberOfNeighborNodes") + this.getPopNoeuds().select(noeud.getGeom().buffer(tolerance)).size()); //$NON-NLS-1$
                  }
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger.debug(I18N.getString("CarteTopo.NumberOfNeighborEdges") + arcs.size()); //$NON-NLS-1$
                  }
                  Arc arc = arcs.iterator().next();
                  if (CarteTopo.logger.isDebugEnabled()) {
                      CarteTopo.logger.debug(I18N.getString("CarteTopo.EdgeSplitting") + arc); //$NON-NLS-1$
                  }
                  arc.projeteEtDecoupe(noeud);
              }
          }
      }
  }

}
