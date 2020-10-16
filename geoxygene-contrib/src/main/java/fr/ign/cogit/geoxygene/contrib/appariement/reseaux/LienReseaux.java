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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;

/**
 * Resultats de l'appariement, qui sont des liens entre objets de BDref et
 * objets de BDcomp. Un lien a aussi une géométrie qui est sa représentation
 * graphique.
 * @author Sébastien Mustiere
 */
public class LienReseaux extends Lien {
  
  static Logger logger = LogManager.getLogger(LienReseaux.class.getName());
  
  /** Les Arc1 pointés par le lien */
  private List<Arc> arcs1 = new ArrayList<Arc>(0);

  /**
   * @return Les Arc1 pointés par le lien
   */
  public List<Arc> getArcs1() {
    return this.arcs1;
  }

  /**
   * @param arcs Les Arc1 pointés par le lien
   */
  public void setArcs1(List<Arc> arcs) {
    this.arcs1 = arcs;
  }

  /**
   * @param arc Arc1 pointé par le lien
   */
  public void addArcs1(Arc arc) {
    this.arcs1.add(arc);
  }

  /** Les Noeud1 pointés par le lien */
  private List<Noeud> noeuds1 = new ArrayList<Noeud>(0);

  /**
   * @return Les Noeud1 pointés par le lien
   */
  public List<Noeud> getNoeuds1() {
    return this.noeuds1;
  }

  /**
   * @param noeuds Les Noeud1 pointés par le lien
   */
  public void setNoeuds1(List<Noeud> noeuds) {
    this.noeuds1 = noeuds;
  }

  /**
   * @param noeud Noeud1 pointé par le lien
   */
  public void addNoeuds1(Noeud noeud) {
    this.noeuds1.add(noeud);
  }

  /** Les Groupe1 pointés par le lien */
  private List<Groupe> groupes1 = new ArrayList<Groupe>(0);

  /**
   * @return Les Groupe1 pointés par le lien
   */
  public List<Groupe> getGroupes1() {
    return this.groupes1;
  }

  /**
   * @param groupes Les Groupe1 pointés par le lien
   */
  public void setGroupes1(List<Groupe> groupes) {
    this.groupes1 = groupes;
  }

  /**
   * @param groupe Groupe1 pointé par le lien
   */
  public void addGroupes1(Groupe groupe) {
    this.groupes1.add(groupe);
  }

  /** Les Arc2 pointés par le lien */
  private List<Arc> arcs2 = new ArrayList<Arc>(0);

  /**
   * @return Les Arc2 pointés par le lien
   */
  public List<Arc> getArcs2() {
    return this.arcs2;
  }

  /**
   * @param arcs Les Arc2 pointés par le lien
   */
  public void setArcs2(List<Arc> arcs) {
    this.arcs2 = arcs;
  }

  /**
   * @param arc Arc2 pointé par le lien
   */
  public void addArcs2(Arc arc) {
    this.arcs2.add(arc);
  }

  /** Les Noeud2 pointés par le lien */
  private List<Noeud> noeuds2 = new ArrayList<Noeud>(0);

  /**
   * @return Les Noeud2 pointés par le lien
   */
  public List<Noeud> getNoeuds2() {
    return this.noeuds2;
  }

  /**
   * @param noeuds Les Noeud2 pointés par le lien
   */
  public void setNoeuds2(List<Noeud> noeuds) {
    this.noeuds2 = noeuds;
  }

  /**
   * @param noeud Noeud2 pointé par le lien
   */
  public void addNoeuds2(Noeud noeud) {
    this.noeuds2.add(noeud);
  }

  /** Les Groupe2 pointés par le lien */
  private List<Groupe> groupes2 = new ArrayList<Groupe>(0);

  /**
   * @return Les Groupe2 pointés par le lien
   */
  public List<Groupe> getGroupes2() {
    return this.groupes2;
  }

  /**
   * @param groupes Les Groupe2 pointés par le lien
   */
  public void setGroupes2(List<Groupe> groupes) {
    this.groupes2 = groupes;
  }

  /**
   * @param groupe Groupe2 pointé par le lien
   */
  public void addGroupes2(Groupe groupe) {
    this.groupes2.add(groupe);
  }

  /**
   * Methode qui affecte la valeur 'eval' comme évaluation du lien et le
   * commentaire 'commentaire' à tous les objets liés par ce lien.
   */
  public void affecteEvaluationAuxObjetsLies(double eval, String commentaireEvaluation) {
    
    this.setEvaluation(eval);
    Iterator<?> itObj;
    itObj = this.getArcs2().iterator();
    while (itObj.hasNext()) {
      ArcApp arc = (ArcApp) itObj.next();
      arc.setResultatAppariement(commentaireEvaluation);
    }
    itObj = this.getArcs1().iterator();
    while (itObj.hasNext()) {
      ArcApp arc = (ArcApp) itObj.next();
      arc.setResultatAppariement(commentaireEvaluation);
    }
    itObj = this.getNoeuds2().iterator();
    while (itObj.hasNext()) {
      NoeudApp noeud = (NoeudApp) itObj.next();
      noeud.setResultatAppariement(commentaireEvaluation);
    }
    itObj = this.getNoeuds1().iterator();
    while (itObj.hasNext()) {
      NoeudApp noeud = (NoeudApp) itObj.next();
      noeud.setResultatAppariement(commentaireEvaluation);
    }
    itObj = this.getGroupes2().iterator();
    while (itObj.hasNext()) {
      GroupeApp groupe = (GroupeApp) itObj.next();
      groupe.setResultatAppariement(commentaireEvaluation);
    }
    itObj = this.getGroupes1().iterator();
    while (itObj.hasNext()) {
      GroupeApp groupe = (GroupeApp) itObj.next();
      groupe.setResultatAppariement(commentaireEvaluation);
    }
  }

  /**
   * Méthode qui renvoie en sortie des liens génériques.
   *     appariement.Lien, liens 1-1 uniquement 
   *     correspondant aux lienReseaux en entrée. 
   * 
   * Cette méthode crée une géométrie aux liens au passage.
   * 
   * @param liensReseaux
   * @param ctRef
   * @param param
   * @return a set of links between the two networks given as arguments
   */
  public static EnsembleDeLiens exportLiensAppariement(
      EnsembleDeLiens liensReseaux, ReseauApp ctRef, ParametresApp param) {
    
    EnsembleDeLiens liensGeneriques = new EnsembleDeLiens();
    liensGeneriques.setNom(liensReseaux.getNom());
    
    // On compile toutes les populations du reseau 1 [resp. 2] dans une liste
    List<IFeatureCollection<? extends IFeature>> pops1 = new ArrayList<IFeatureCollection<? extends IFeature>>(param.populationsArcs1);
    pops1.addAll(param.populationsNoeuds1);
    List<IFeatureCollection<? extends IFeature>> pops2 = new ArrayList<IFeatureCollection<? extends IFeature>>(param.populationsArcs2);
    pops2.addAll(param.populationsNoeuds2);
    
    // Boucle sur les liens entre cartes topo
    Iterator<Lien> itLiensReseaux = liensReseaux.iterator();
    while (itLiensReseaux.hasNext()) {
      
      LienReseaux lienReseau = (LienReseaux) itLiensReseaux.next();
      
      // On récupère tous les objets des cartes topo concernés
      Set<IFeature> objetsCT1PourUnLien = new HashSet<IFeature>(lienReseau.getArcs1());
      objetsCT1PourUnLien.addAll(lienReseau.getNoeuds1());
      Iterator<Groupe> itGroupes1 = lienReseau.getGroupes1().iterator();
      while (itGroupes1.hasNext()) {
        GroupeApp groupe1 = (GroupeApp) itGroupes1.next();
        objetsCT1PourUnLien.addAll(groupe1.getListeArcs());
        objetsCT1PourUnLien.addAll(groupe1.getListeNoeuds());
      }
      
      Set<IFeature> objetsCT2PourUnLien = new HashSet<IFeature>(lienReseau.getArcs2());
      objetsCT2PourUnLien.addAll(lienReseau.getNoeuds2());
      Iterator<Groupe> itGroupes2 = lienReseau.getGroupes2().iterator();
      while (itGroupes2.hasNext()) {
        GroupeApp groupe2 = (GroupeApp) itGroupes2.next();
        objetsCT2PourUnLien.addAll(groupe2.getListeArcs());
        objetsCT2PourUnLien.addAll(groupe2.getListeNoeuds());
      }
      
      // On parcours chaque couple d'objets de cartes topos appariés
      Iterator<IFeature> itObjetsCT1PourUnLien = objetsCT1PourUnLien.iterator();
      while (itObjetsCT1PourUnLien.hasNext()) {
        IFeature objetCT1 = itObjetsCT1PourUnLien.next();
        Iterator<IFeature> itObjetsCT2PourUnLien = objetsCT2PourUnLien.iterator();
        Collection<IFeature> objets1 = LienReseaux.getCorrespondants(objetCT1, pops1);
        while (itObjetsCT2PourUnLien.hasNext()) {
          
          IFeature objetCT2 = itObjetsCT2PourUnLien.next();
          Collection<IFeature> objets2 = LienReseaux.getCorrespondants(objetCT2, pops2);
          
          if (objets1.isEmpty() && objets2.isEmpty()) {
            // Cas où il n'y a pas de correspondant dans les données de départ des 2 côtés
            Lien lienG = liensGeneriques.nouvelElement();
            lienG.setEvaluation(lienReseau.getEvaluation());
            lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInBothDatabases")); //$NON-NLS-1$
            if (param.exportGeometrieLiens2vers1) {
              lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT1, objetCT2));
            } else {
              lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT2, objetCT1));
            }
            // lienG.setAttribute("evaluation", lienReseau.getEvaluation());
            continue;
          }
          
          if (objets1.isEmpty()) {
            // Cas où il n'y a pas de correspondant dans les données de BD1
            Iterator<? extends IFeature> itObjets2 = objets2.iterator();
            while (itObjets2.hasNext()) {
              IFeature objet2 = itObjets2.next();
              Lien lienG = liensGeneriques.nouvelElement();
              lienG.setEvaluation(lienReseau.getEvaluation());
              lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInDB1")); //$NON-NLS-1$
              if (param.exportGeometrieLiens2vers1) {
                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT1, objet2));
              } else {
                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet2, objetCT1));
              }
              lienG.addObjetComp(objet2);
              // lienG.setAttribute("evaluation", lienReseau.getEvaluation());
            }
            continue;
          }
          
          if (objets2.isEmpty()) {
            // Cas où il n'y a pas de correspondant dans les données de BD2
            Iterator<? extends IFeature> itObjets1 = objets1.iterator();
            while (itObjets1.hasNext()) {
              IFeature objet1 = itObjets1.next();
              Lien lienG = liensGeneriques.nouvelElement();
              lienG.setEvaluation(lienReseau.getEvaluation());
              lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInDB1")); //$NON-NLS-1$
              if (param.exportGeometrieLiens2vers1) {
                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet1, objetCT2));
              } else {
                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT2, objet1));
              }
              lienG.addObjetRef(objet1);
              // lienG.setAttribute("evaluation", lienReseau.getEvaluation());
            }
            continue;
          }
          
          // Cas où il y a des correspondants dans les deux BD
          Iterator<? extends IFeature> itObjets1 = objets1.iterator();
          while (itObjets1.hasNext()) {
              IFeature objet1 = itObjets1.next();
              Iterator<? extends IFeature> itObjets2 = objets2.iterator();
              while (itObjets2.hasNext()) {
                  IFeature objet2 = itObjets2.next();
                  Lien lienG = liensGeneriques.nouvelElement();
                  lienG.setEvaluation(lienReseau.getEvaluation());
                  lienG.setCommentaire(""); //$NON-NLS-1$
                  lienG.setReference(Integer.toString(objet1.getId()));
                  lienG.setComparaison(Integer.toString(objet2.getId()));
                  if (param.exportGeometrieLiens2vers1) {
                      lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet1, objet2));
                  } else {
                      lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet2, objet1));
                  }
                  lienG.addObjetRef(objet1);
                  lienG.addObjetComp(objet2);
                  // lienG.setAttribute("evaluation", lienReseau.getEvaluation());
              }
          }
        
        }
      }
    }
    
    if (param.debugAffichageCommentaires > 1) {
      LienReseaux.logger.info("  " + liensGeneriques.size() + " liens 1-1 have been exported");
    }
    //
    return liensGeneriques;
  }

  /**
   * Renvoie les correspondants appartenant à une des FT_FeatureCollection de la
   * liste passée en parametre.
   */
  public static Collection<IFeature> getCorrespondants(IFeature ft,
      List<IFeatureCollection<? extends IFeature>> populations) {
    
      List<IFeature> resultats = new ArrayList<IFeature>();
      for (IFeatureCollection<? extends IFeature> pop : populations) {
          resultats.addAll(ft.getCorrespondants(pop));
      }
      return resultats;
  }
  
    /**
     * Methode créant une géométrie au lien 1-1 en reliant les deux objets
     * concerné par un simple trait.
     */
    @SuppressWarnings("unchecked")
    public static IGeometry creeGeometrieLienSimple(IFeature obj1, IFeature obj2) {
    
        // POINT - POINT
        if (obj2.getGeom() instanceof IPoint && obj1.getGeom() instanceof IPoint) {
            List<IDirectPosition> points = new ArrayList<IDirectPosition>();
            
            IPoint point2 = (IPoint) obj2.getGeom();
            IDirectPosition DP2 = point2.getPosition();
            points.add(DP2);
            
            IPoint point1 = (IPoint) obj1.getGeom();
            points.add(point1.getPosition());

            return new GM_LineString(points);
        }
        
        // POINT - LIGNE
        if (obj2.getGeom() instanceof IPoint && obj1.getGeom() instanceof ILineString) {
            List<IDirectPosition> points = new ArrayList<IDirectPosition>();
            
            IPoint point2 = (IPoint) obj2.getGeom();
            IDirectPosition DP2 = point2.getPosition();
            points.add(DP2);
            
            ILineString ligne1 = (ILineString) obj1.getGeom();
            points.add(Operateurs.projection(DP2, ligne1));

            return new GM_LineString(points);
        }
        
        // LIGNE - POINT
        if (obj2.getGeom() instanceof ILineString && obj1.getGeom() instanceof IPoint) {
            List<IDirectPosition> points = new ArrayList<IDirectPosition>();
        
            ILineString ligne2 = (ILineString) obj2.getGeom();
            IDirectPosition DP2 = Operateurs.milieu(ligne2);
            points.add(DP2);
            
            IPoint point1 = (IPoint) obj1.getGeom();
            points.add(point1.getPosition());

            return new GM_LineString(points);
        }
        
        // LIGNE - LIGNE
        if (obj2.getGeom() instanceof ILineString && obj1.getGeom() instanceof ILineString) {
            List<IDirectPosition> points = new ArrayList<IDirectPosition>();
            
            ILineString ligne2 = (ILineString) obj2.getGeom();
            IDirectPosition DP2 = Operateurs.milieu(ligne2);
            points.add(DP2);
            
            ILineString ligne1 = (ILineString) obj1.getGeom();
            points.add(Operateurs.projection(DP2, ligne1));

            return new GM_LineString(points);
        }
        
        // POINT - MULTILIGNE
        if (obj2.getGeom() instanceof IPoint && obj1.getGeom() instanceof IMultiCurve) {
            // 
            IMultiCurve multiligne = new GM_MultiCurve();
            //
            IPoint point2 = (IPoint) obj2.getGeom();
            IDirectPosition DP2 = point2.getPosition();
            //
            for (IGeometry lineGeom1 : ((GM_Aggregate<?>) obj1.getGeom()).getList()) {
                // 
                List<IDirectPosition> points = new ArrayList<IDirectPosition>();
                //
                points.add(DP2);
                // 
                ILineString ligne1 = (ILineString) lineGeom1;
                points.add(Operateurs.projection(DP2, ligne1));
                // 
                ILineString ligne = new GM_LineString(points);
                multiligne.add(ligne);
            }
            return multiligne;
        }
        
        // MULTILIGNE - POINT
        if (obj2.getGeom() instanceof IMultiCurve && obj1.getGeom() instanceof IPoint) {
            //
            IMultiCurve multiligne = new GM_MultiCurve();
            //
            for (IGeometry lineGeom : ((GM_Aggregate<?>) obj2.getGeom()).getList()) {
                // 
                List<IDirectPosition> points = new ArrayList<IDirectPosition>();
            
                ILineString ligne2 = (ILineString) lineGeom;
                IDirectPosition DP2 = Operateurs.milieu(ligne2);
                points.add(DP2);
                
                IPoint point1 = (IPoint) obj1.getGeom();
                points.add(point1.getPosition());
                
                ILineString ligne = new GM_LineString(points);
                multiligne.add(ligne);
            }
            return multiligne;
        }
        
        
        // MULTILIGNE - MULTILIGNE
        if (obj2.getGeom() instanceof IMultiCurve && obj1.getGeom() instanceof IMultiCurve) {
            //
            IMultiCurve multiligne = new GM_MultiCurve();
            //
            for (IGeometry lineGeom : ((GM_Aggregate<?>) obj2.getGeom()).getList()) {
                ILineString ligne2 = (ILineString) lineGeom;
                IDirectPosition DP2 = Operateurs.milieu(ligne2);
                //
                for (IGeometry lineGeom1 : ((GM_Aggregate<?>) obj1.getGeom()).getList()) {
                    // 
                    List<IDirectPosition> points = new ArrayList<IDirectPosition>();
                    //
                    points.add(DP2);
                    // 
                    ILineString ligne1 = (ILineString) lineGeom1;
                    points.add(Operateurs.projection(DP2, ligne1));
                    // 
                    ILineString ligne = new GM_LineString(points);
                    multiligne.add(ligne);
                }
            }
            return multiligne;
        }
    
        // Cas non traité
        logger.error("NON TRAITE : " + obj1.getGeom().getClass() + ", " + obj2.getGeom().getClass());
        return null;
    
  }

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
   * Méthode qui affecte une geometrie aux liens de réseau et remplit les
   * commentaires des liens. UTILE POUR CODE / DEBUG UNIQUEMENT
   */
  public static void exportAppCarteTopo(EnsembleDeLiens liensReseaux, ParametresApp param) {
    
    // Création de la géométrie des liens
    for (Lien lien : liensReseaux) {
        LienReseaux lienR = (LienReseaux) lien;
      
        lienR.setGeom(lienR.creeGeometrieLien(param.debugTirets,
          param.debugPasTirets, param.debugBuffer, param.debugTailleBuffer));
        
        // lienR.setSchema(lien.getSchema());
        // lienR.setAttribute("evaluation", lien.getEvaluation());
        
        /////////////////////////////////////////
        // Récupération des objets pointés par le lien
        
        // Liens vers les objets de référence
        List<IFeature> tousobjetsRef = new ArrayList<IFeature>();
        tousobjetsRef.addAll(lienR.getArcs1());
        tousobjetsRef.addAll(lienR.getNoeuds1());
        Iterator<Groupe> itGroupe = lienR.getGroupes1().iterator();
        while (itGroupe.hasNext()) {
            Groupe groupe = itGroupe.next();
            tousobjetsRef.addAll(groupe.getListeArcs());
            tousobjetsRef.addAll(groupe.getListeNoeuds());
        }
        lienR.setObjetsRef(tousobjetsRef);
        
        // Liens vers les objets de comparaison
        List<IFeature> tousobjetsComp = new ArrayList<IFeature>();
        tousobjetsComp.addAll(lienR.getArcs2());
        tousobjetsComp.addAll(lienR.getNoeuds2());
        itGroupe = lienR.getGroupes2().iterator();
        while (itGroupe.hasNext()) {
            Groupe groupe = itGroupe.next();
            tousobjetsComp.addAll(groupe.getListeArcs());
            tousobjetsComp.addAll(groupe.getListeNoeuds());
        }
        lienR.setObjetsComp(tousobjetsComp);
      
    }
    
    // if ( param.debugAffichageCommentaires > 1 )
    // System.out.println("BILAN de l'appariement sur le réseau 1");
    // if ( param.debugAffichageCommentaires > 1 )
    // System.out.println("NB : bilan sur les objets des réseaux créés, et non sur les objets initiaux, il peut y avoir quelques nuances");
    //
    // liensGeneriques = new EnsembleDeLiens();
    // liensGeneriques.setNom(liensReseaux.getNom());
    // itLiens = liensReseaux.getElements().iterator();
    // while (itLiens.hasNext()) {
    //     lienR = (LienReseaux) itLiens.next();
    //     
    //     lienG = (Lien)liensGeneriques.nouvelElement();
    //     lienG.setEvaluation(lienR.getEvaluation());
    //
    //     /////////////////////////////////////////
    //     // Récupération des objets pointés par le lien
    //
    //     // Liens vers les objets de référence
    // tousobjetsRef = new ArrayList<FT_Feature>();
    // tousobjetsRef.addAll(lienR.getArcs1());
    // tousobjetsRef.addAll(lienR.getNoeuds1());
    // itGroupe = lienR.getGroupes1().iterator();
    // while (itGroupe.hasNext()) {
    // groupe = itGroupe.next();
    // tousobjetsRef.addAll(groupe.getListeArcs());
    // tousobjetsRef.addAll(groupe.getListeNoeuds());
    // }
    // lienG.setObjetsRef(tousobjetsRef);
    //
    // // Liens vers les objets de comparaison
    // tousobjetsComp = new ArrayList<FT_Feature>();
    // tousobjetsComp.addAll(lienR.getArcs2());
    // tousobjetsComp.addAll(lienR.getNoeuds2());
    // itGroupe = lienR.getGroupes2().iterator();
    // while (itGroupe.hasNext()) {
    // groupe = itGroupe.next();
    // tousobjetsComp.addAll(groupe.getListeArcs());
    // tousobjetsComp.addAll(groupe.getListeNoeuds());
    // }
    // lienG.setObjetsComp(tousobjetsComp);
    //
    // /////////////////////////////////////////
    // // Détermination du type du lien
    // if (lienR.getNoeuds1().size() != 0 ) {
    // lienG.setType("Lien de noeud ref");
    // // liensGeneriques.enleveElement(lienG);
    // // continue;
    // }
    // else if (lienR.getArcs1().size() != 0 ) lienG.setType("Lien d'arc ref");
    //
    // /////////////////////////////////////////
    // // Remplissage des chaines de caractères pour l'export
    // Iterator<FT_Feature> itRef = lienG.getObjetsRef().iterator();
    // String txt = new String("COGITID:");
    // while (itRef.hasNext()) {
    // FT_Feature objet = itRef.next();
    // Iterator<FT_Feature> itObjGeo = objet.getCorrespondants().iterator();
    // while (itObjGeo.hasNext()) {
    // FT_Feature objetGeo = itObjGeo.next();
    // txt = txt.concat(objetGeo.getId()+" ");
    // }
    // }
    // lienG.setReference(txt);
    //
    // txt = new String("COGITID:");
    // Iterator<FT_Feature> itComp= lienG.getObjetsComp().iterator();
    // while (itComp.hasNext()) {
    // FT_Feature objet = itComp.next();
    // Iterator<FT_Feature> itObjGeo = objet.getCorrespondants().iterator();
    // while (itObjGeo.hasNext()) {
    // FT_Feature objetGeo = itObjGeo.next();
    // txt = txt.concat(objetGeo.getId()+" ");
    // }
    // }
    // lienG.setComparaison(txt);
    //
    // /////////////////////////////////////////
    // // création de la géométrie des liens
    // lienG.setGeom(lienR.creeGeometrieLien(param.debugTirets,
    // param.debugPasTirets, param.debugBuffer, param.debugTailleBuffer));
    // }
    // return liensGeneriques;
  }

  /**
   * Methode créant une géométrie pour les liens de réseau.
   * <ul>
   * <li>1/ Pour chaque noeud du réseau 1 apparié, cette géométrie est
   * constituée
   * <ul>
   * <li>- d'un buffer entourant les objets homologues dans le réseau,
   * <li>- d'un trait reliant le noeud à ce buffer.
   * </ul>
   * <li>2/ Pour chaque arc du réseau 1 apparié, cette géométrie est constituée
   * <ul>
   * <li>- d'un ensemble de tirets reliant les arcs homologues de manière
   * régulière (intervalle entre les tirets en paramètre),
   * <li>- ou alors d'un ensemble de traits reliant le milieu des arcs appariés.
   * </ul>
   * </ul>
   * @param tirets spécifie si on veut une géométrie faite de tirets (true), ou
   *          plutôt d'un unique trait pour chaque couple d'arcs (false)
   * 
   * @param pasTirets Si on veut des tirets réguliers, distance entre ces
   *          tirets.
   * 
   * @param tailleBuffer Taille du buffer autour des objets appariés à un noeud.
   */
  private IGeometry creeGeometrieLien(boolean tirets, double pasTirets,
      boolean buffer, double tailleBuffer) {
    
    if (LienReseaux.logger.isDebugEnabled()) {
      LienReseaux.logger.debug(tirets
          + " - " + pasTirets + " - " + buffer + " - " + tailleBuffer); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    Iterator<Groupe> itGroupes;
    Iterator<Noeud> itNoeuds;
    Iterator<Noeud> itNoeudsComp;
    Iterator<Arc> itArcs;
    Iterator<Arc> itArcsComp;
    NoeudApp noeudComp, noeudRef;
    ArcApp arcComp, arcRef;
    GroupeApp groupeComp;
    GM_LineString ligne, chemin;
    GM_Aggregate<IGeometry> geomLien;
    
    // LIEN D'UN NOEUD REF VERS DES NOEUDS COMP ET/OU DES GROUPES COMP
    if (this.getNoeuds1().size() == 1) {
      if (LienReseaux.logger.isDebugEnabled()) {
        LienReseaux.logger
            .debug(I18N.getString("LienReseaux.Nodes1") + this.getNoeuds1().size()); //$NON-NLS-1$
      }
      noeudRef = (NoeudApp) this.getNoeuds1().get(0);
      geomLien = new GM_Aggregate<IGeometry>();
      // 1 noeud ref - n noeuds comp isolés --> 1 aggrégat de traits et de
      // surfaces
      itNoeuds = this.getNoeuds2().iterator();
      while (itNoeuds.hasNext()) {
        noeudComp = (NoeudApp) itNoeuds.next();
        ligne = new GM_LineString(noeudRef.getCoord(), noeudComp.getCoord());
        geomLien.add(ligne);
        if (buffer) {
          geomLien.add(noeudComp.getGeometrie().buffer(tailleBuffer));
        }
      }
      // 1 noeud ref - n groupes --> aggrégat de traits et de surface autour du
      // groupe
      itGroupes = this.getGroupes2().iterator();
      while (itGroupes.hasNext()) {
        groupeComp = (GroupeApp) itGroupes.next();
        itArcsComp = groupeComp.getListeArcs().iterator();
        while (itArcsComp.hasNext()) {
          arcComp = (ArcApp) itArcsComp.next();
          geomLien.add(arcComp.getGeometrie().buffer(tailleBuffer));
        }
        itNoeudsComp = groupeComp.getListeNoeuds().iterator();
        while (itNoeudsComp.hasNext()) {
          noeudComp = (NoeudApp) itNoeudsComp.next();
          geomLien.add(noeudComp.getGeometrie().buffer(tailleBuffer));
        }
        // on fait le trait entre le noeud ref et le groupe comp
        ligne = new GM_LineString(noeudRef.getCoord(), noeudRef.noeudLePlusProche(groupeComp).getCoord());
        geomLien.add(ligne);
      }
      if (LienReseaux.logger.isDebugEnabled()) {
        // LienReseaux.logger.debug(I18N.getString("LienReseaux.LinkGeometry") + geomLien); //$NON-NLS-1$
      }
      if (geomLien.coord().size() > 1) {
        return geomLien;
      }
      LienReseaux.logger.info(I18N.getString("LienReseaux.NodeLinkNotCreated")); //$NON-NLS-1$
      return null;
    }
    
    // LIEN D'ARCS REF VERS DES ARCS OU DES GROUPES COMP
    Iterator<Arc> itArcsRef = this.getArcs1().iterator();
    geomLien = new GM_Aggregate<IGeometry>();
    while (itArcsRef.hasNext()) {
      arcRef = (ArcApp) itArcsRef.next();
      // 1 arc ref directement vers des noeuds
      itNoeuds = this.getNoeuds2().iterator();
      while (itNoeuds.hasNext()) {
        noeudComp = (NoeudApp) itNoeuds.next();
        if (tirets) {
          geomLien.add(Lien.tirets(arcRef.getGeometrie(), noeudComp.getGeometrie(), pasTirets));
        } else {
          geomLien.add(Lien.tiret(arcRef.getGeometrie(), noeudComp.getGeometrie()));
        }
      }
      // 1 arc ref vers des groupes comp (groupes en parrallèle) --> plusieurs
      // séries de tirets
      itGroupes = this.getGroupes2().iterator();
      while (itGroupes.hasNext()) {
        // 1 arc ref vers un groupe comp (des arcs en série) --> des tirets
        groupeComp = (GroupeApp) itGroupes.next();
        chemin = groupeComp.compileArcs(arcRef);
        if (chemin != null) {
          if (tirets) {
            geomLien.add(Lien.tirets(arcRef.getGeometrie(), chemin, pasTirets));
          } else {
            geomLien.add(Lien.tiret(arcRef.getGeometrie(), chemin));
          }
        }
      }
      // 1 arc ref vers des arcs comp en série --> des tirets (utile pour le
      // pre-appariement uniquement)
      itArcs = this.getArcs2().iterator();
      while (itArcs.hasNext()) {
        arcComp = (ArcApp) itArcs.next();
        if (tirets) {
          geomLien.add(Lien.tirets(arcRef.getGeometrie(),
              arcComp.getGeometrie(), 25));
        } else {
          geomLien
              .add(Lien.tiret(arcRef.getGeometrie(), arcComp.getGeometrie()));
        }
      }

    }
    if (LienReseaux.logger.isDebugEnabled()) {
      LienReseaux.logger
          .debug(I18N.getString("LienReseaux.LinkGeometry") + geomLien); //$NON-NLS-1$
    }
    if (geomLien.coord().size() > 1) {
      return geomLien;
    }
    LienReseaux.logger.info(I18N.getString("LienReseaux.EdgeLinkNotCreated")); //$NON-NLS-1$
    return null;
  }

  /**
   * 
   */
  public void clear() {
    this.arcs1.clear();
    this.arcs2.clear();
    this.noeuds1.clear();
    this.noeuds2.clear();
    this.groupes1.clear();
    this.groupes2.clear();
    this.clearCorrespondants();
  }
}
