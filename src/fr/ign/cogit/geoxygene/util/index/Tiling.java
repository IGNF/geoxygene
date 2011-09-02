/**
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
 * 
 */

package fr.ign.cogit.geoxygene.util.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.util.index.SpatialIndex;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Index spatial par simple dallage.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Sébastien Mustière
 * @author Nathalie Abadie
 * @author Julien Perret
 * @version 1.3 Gestion d'erreur de calcul des buffers dans la fonction
 *          {@link Tiling#select(IGeometry, double)}
 */

public class Tiling<Feat extends IFeature> implements SpatialIndex<Feat> {
  static Logger logger = Logger.getLogger(Tiling.class.getName());

  // ===============================================

  @Override
  public List<Object> getParametres() {
    List<Object> param = new ArrayList<Object>(0);
    param.add(Tiling.class);
    param.add(new Boolean(this.automaticUpdate));
    param.add(new GM_Envelope(this.xmin, this.xmax, this.ymin, this.ymax));
    param.add(new Integer(this.size));
    return param;
  }

  // ===============================================
  /**
   * Tableau de collections de features appartenant a chaque dalle. Un feature
   * peut appartenir a plusieurs dalles.
   */
  private List<Feat>[][] index;

  // ===============================================
  /** Taille du dallage (nombre de rectangles par cote). */
  private int size;

  /** Taille du dallage (nombre de rectangles par cote). */
  public int getSize() {
    return this.size;
  }

  // xmin, xmax, ymin, ymax
  /** paramètre interne du dallage */
  private double xmin;
  /** paramètre interne du dallage */
  private double xmax;
  /** paramètre interne du dallage */
  private double ymin;
  /** paramètre interne du dallage */
  private double ymax;

  // calcul de dX et dY
  /** paramètre interne du dallage */
  private double dX;
  /** paramètre interne du dallage */
  private double dY;

  /** Tableau à deux dimensions des dalles sous forme de Polygones. */
  private IPolygon[][] dallesPolygones;

  // ===============================================
  /** Indique si l'on a demande une mise a jour automatique. */
  private boolean automaticUpdate;

  /** Indique si l'on a demande une mise a jour automatique. */
  @Override
  public boolean hasAutomaticUpdate() {
    return this.automaticUpdate;
  }

  /**
   * Demande une mise a jour automatique. NB: Cette méthode ne fait pas les
   * éventuelles MAJ qui auriant été faites alors que le mode MAJ automatique
   * n'était pas activé.
   */
  @Override
  public void setAutomaticUpdate(boolean auto) {
    this.automaticUpdate = auto;
  }

  // ===============================================
  /** Tableau à deux dimensions des dalles. */
  private IEnvelope[][] dallage;

  /** Renvoie le tableau à 2 dimensions des dalles. */
  public IEnvelope[][] getDallage() {
    return this.dallage;
  }

  /** renvoie la dalle d'indice i,j. */
  public IEnvelope getDallage(int i, int j) {
    return this.dallage[i][j];
  }

  /** Tableau des dalles contenant le feature. */
  public IEnvelope[] getDallage(IFeature feat) {
    List<IEnvelope> result = new ArrayList<IEnvelope>(0);
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        if (this.index[i][j].contains(feat)) {
          result.add(this.dallage[i][j]);
        }
      }
    }
    IEnvelope[] array = new GM_Envelope[result.size()];
    for (int k = 0; k < result.size(); k++) {
      array[k] = result.get(k);
    }
    return array;
  }

  /** Tableau des numéros des dalles contenant le feature. */
  public List<List<Integer>> getNumDallage(Feat feat) {
    List<List<Integer>> result = new ArrayList<List<Integer>>(0);
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        if (this.index[i][j].contains(feat)) {
          List<Integer> couple = new ArrayList<Integer>(0);
          couple.add(new Integer(i));
          couple.add(new Integer(j));
          result.add(couple);
        }
      }
    }
    return result;
  }

  /**
   * Dalle couvrant le point passe en parametre. Renvoie NULL si aucune dalle ne
   * couvre ce point.
   */
  public IEnvelope getDallage(IDirectPosition dp) {
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        if (this.dallage[i][j].contains(dp)) {
          return this.dallage[i][j];
        }
      }
    }
    return null;
  }

  /**
   * Etant donné une enveloppe, renvoie les indices min et max des dalles qui
   * intersectent cette enveloppe (dans l'ordre: imin, imax, jmin, jmax)
   */
  private int[] dallesIntersectees(IEnvelope env) {
    int i, imin = 0, imax = this.size - 1, jmin = 0, jmax = this.size - 1;
    boolean min = true;
    for (i = 0; i < this.size; i++) {
      if (min
          && (env.getLowerCorner().getX() <= this.dallage[i][0]
              .getUpperCorner().getX())) {
        imin = i;
        min = false;
      }
      if ((!min)
          && (env.getUpperCorner().getX() <= this.dallage[i][0]
              .getUpperCorner().getX())) {
        imax = i;
        break;
      }
    }
    min = true;
    for (i = 0; i < this.size; i++) {
      if (min
          && (env.getLowerCorner().getY() <= this.dallage[0][i]
              .getUpperCorner().getY())) {
        jmin = i;
        min = false;
      }
      if ((!min)
          && (env.getUpperCorner().getY() <= this.dallage[0][i]
              .getUpperCorner().getY())) {
        jmax = i;
        break;
      }
    }
    int tab[] = { imin, imax, jmin, jmax };
    return tab;
    /*
     * AUTRE METHODE POSSIBLE POUR FAIRE LA MEME CHOSE MAIS BIZARREMENT CA A
     * L'AIR MOINS RAPIDE int imin, imax, jmin, jmax; double imind, jmind,
     * imindf, imaxdf,jmindf,jmaxdf;
     * 
     * imind = (env.getLowerCorner().getX()-xmin)/dX; imindf =
     * Math.floor(imind); if (imindf == imind ) {if (imindf != 0) imindf =
     * imindf -1;} imin = (new Double(imindf)).intValue();
     * 
     * imaxdf = Math.floor((env.getUpperCorner().getX()-xmin)/dX); if ( imaxdf
     * == size ) imaxdf = size-1; imax = (new Double(imaxdf)).intValue();
     * 
     * jmind = (env.getLowerCorner().getY()-ymin)/dY; jmindf =
     * Math.floor(jmind); if (jmindf == jmind ) {if (jmindf != 0) jmindf =
     * jmindf -1;} jmin = (new Double(jmindf)).intValue();
     * 
     * jmaxdf = Math.floor((env.getUpperCorner().getY()-ymin)/dY); if ( jmaxdf
     * == size ) jmaxdf = size-1; jmax = (new Double(jmaxdf)).intValue();
     */
  }

  /** Features appartenant a la dalle d'indice i,j. */
  public Collection<Feat> select(int i, int j) {
    return new HashSet<Feat>(this.index[i][j]);
  }

  @Override
  public Collection<Feat> select(IEnvelope env) {
    Collection<Feat> result = new HashSet<Feat>();
    if (env == null) {
      return result;
    }
    IGeometry geometry = new GM_Polygon(env);
    if (env.getUpperCorner().getX() == env.getLowerCorner().getX()
        && env.getUpperCorner().getY() == env.getLowerCorner().getY()) {
      geometry = new GM_Point(env.getUpperCorner());
    }
    int tab[] = this.dallesIntersectees(env);
    for (int i = tab[0]; i <= tab[1]; i++) {
      for (int j = tab[2]; j <= tab[3]; j++) {
        synchronized (this.index) {
          int tileSize = this.index[i][j].size();
          for (int ind = 0; ind < tileSize; ind++) {
            Feat feature = this.index[i][j].get(ind);
            IGeometry geom = feature.getGeom();
            if (geom == null) {
              continue;
            }
            IEnvelope envCourante = geom.envelope();
            if (env.overlaps(envCourante) && geometry.intersects(geom)) {
              result.add(feature);
            }
          }
        }
      }
    }
    return result;
  }

  @Override
  public Collection<Feat> select(IDirectPosition P, double distance) {
    return this.select(new GM_Envelope(P, distance));
  }

  @Override
  public Collection<Feat> select(IGeometry geometry) {
    int tab[];
    Collection<Feat> result = new HashSet<Feat>(0);
    IEnvelope envGeometry = geometry.envelope();
    tab = this.dallesIntersectees(envGeometry);
    for (int i = tab[0]; i <= tab[1]; i++) {
      for (int j = tab[2]; j <= tab[3]; j++) {
        if (geometry.intersects(this.dallesPolygones[i][j])) {
          synchronized (this.index) {
            for (Feat feature : this.index[i][j]) {
              IGeometry geom = feature.getGeom();
              IEnvelope envCourante = geom.envelope();
              if (envGeometry.overlaps(envCourante)
                  && geometry.intersects(geom)) {
                result.add(feature);
              }
            }
          }
        }
      }
    }
    return result;
  }

  @Override
  public Collection<Feat> select(IGeometry geometry, boolean strictlyCrosses) {
    int tab[];
    Collection<Feat> result = new HashSet<Feat>(0);
    IEnvelope envGeometry = geometry.envelope();
    tab = this.dallesIntersectees(envGeometry);
    for (int i = tab[0]; i <= tab[1]; i++) {
      for (int j = tab[2]; j <= tab[3]; j++) {
        if (geometry.intersects(this.dallesPolygones[i][j])) {
          synchronized (this.index) {
            for (Feat feature : this.index[i][j]) {
              IGeometry geom = feature.getGeom();
              IEnvelope envCourante = geom.envelope();
              if (envGeometry.overlaps(envCourante)
                  && (strictlyCrosses ? (geometry.crosses(geom)) : (geometry
                      .intersects(geom)))) {
                result.add(feature);
              }
            }
          }
        }
      }
    }
    return result;
  }

  @Override
  public Collection<Feat> select(IGeometry geometry, double distance) {
    if (distance == 0) {
      return this.select(geometry);
    }
    try {
      return this.select(geometry.buffer(distance));
    } catch (Exception e) {
      System.out
          .println("PROBLEME AVEC LA FABRICATION DU BUFFER LORS D'UNE REQUETE SPATIALE");
      e.printStackTrace();
      return new HashSet<Feat>(0);
    }
  }

  // ===============================================
  // CONSTRUCTEURS
  // ===============================================

  /**
   * Crée et instancie un dallage d'une collection de FT_Feature, en fonction
   * des limites de la zone et du nombre de cases souhaitées sur la zone.
   * 
   * @param fc La liste de Features à indexer
   * 
   * @param automaticUpd Spéciifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc
   * 
   * @param envelope Enveloppe décrivant les limites de l'index spatial. NB:
   *          Tout objet hors de ces limites ne sera pas traité lors des
   *          requêtes spatiales !!!!!
   * 
   * @param n Nombre de dalles en X et en Y, du dallage.
   */
  @SuppressWarnings("unchecked")
  public Tiling(IFeatureCollection<Feat> fc, Boolean automaticUpd,
      IEnvelope envelope, Integer n) {
    int tab[];
    // initialisation des variables
    this.size = n.intValue();
    this.dallage = new GM_Envelope[this.size][this.size];
    this.automaticUpdate = automaticUpd.booleanValue();
    this.index = new List[this.size][this.size];
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        this.index[i][j] = new ArrayList<Feat>(0);
      }
    }

    // xmin, xmax, ymin, ymax
    this.xmin = envelope.minX();
    this.xmax = envelope.maxX();
    this.ymin = envelope.minY();
    this.ymax = envelope.maxY();

    if (Tiling.logger.isTraceEnabled()) {
      Tiling.logger.trace("envelope = " + this.xmin + "," + this.xmax + "," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + this.ymin + "," + this.ymax + " - size = " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
    }
    // calcul de dX et dY
    this.dX = (this.xmax - this.xmin) / this.size;
    this.dY = (this.ymax - this.ymin) / this.size;
    // ecriture des dalles
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        GM_Envelope env = new GM_Envelope();
        env.setLowerCorner(new DirectPosition(this.xmin + (i * this.dX),
            this.ymin + (j * this.dY)));
        env.setUpperCorner(new DirectPosition(this.xmin + ((i + 1) * this.dX),
            this.ymin + ((j + 1) * this.dY)));
        this.dallage[i][j] = env;
      }
    }
    // initialisation d'un tableau de polygones
    this.dallesPolygones = new GM_Polygon[this.size][this.size];
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        this.dallesPolygones[i][j] = new GM_Polygon(this.dallage[i][j]);
      }
    }
    // calcul de l'index
    Iterator<Feat> iterator = fc.iterator();
    while (iterator.hasNext()) {
      Feat feature = iterator.next();
      IGeometry geom = feature.getGeom();
      if (geom == null) {
        continue;
      }
      IEnvelope envObjet = geom.envelope();
      tab = this.dallesIntersectees(envObjet);
      for (int i = tab[0]; i <= tab[1]; i++) {
        for (int j = tab[2]; j <= tab[3]; j++) {
          if (geom.intersects(this.dallesPolygones[i][j])) {
            this.index[i][j].add(feature);
          }
        }
      }
    }
  }

  /**
   * Crée et instancie un dallage d'une collection de FT_Feature, en fonction du
   * nombre de cases souhaitées sur la zone. NB: les limites de la zone de
   * l'index sont celles de la collection traitée. Il est donc impossible de
   * rajouter ensuite dans la collection un objet en dehors de cette zone.
   * 
   * @param fc La liste de Features à indexer
   * 
   * @param automaticUpd Spéciifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc
   * 
   * @param n Nombre de dalles en X et en Y, du dallage.
   */
  public Tiling(IFeatureCollection<Feat> fc, Boolean automaticUpd, Integer n) {
    this(fc, automaticUpd, fc.envelope(), n);
  }

  /**
   * Crée et instancie un dallage d'une collection de FT_Feature. Les paramètres
   * sont définis par la collection en entrée: 1/ Les limites de la zone de
   * l'index sont celles de la collection traitée. Il est donc impossible de
   * rajouter ensuite dans la collection un objet en dehors de cette zone. 2/ Le
   * nombre de cases est défini automatiquement pour qu'il y ait de l'ordre de
   * 50 objets par dalle en moyennne (approximatif)
   * 
   * @param fc La liste de Features à indexer
   * 
   * @param automaticUpd Spéciifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc
   * 
   */
  public Tiling(IFeatureCollection<Feat> fc, Boolean automaticUpd) {
    this(fc, automaticUpd, new Integer(Tiling.nbDallesXY(fc)));
  }

  /**
   * Calcul du choix d'un nombre de dalles arbitraire à utiliser quand
   * l'utilisateur ne le stipule pas. Le calcul renvoie sqrt(nombre d'éléments
   * de la collection/50) et 1 si la collection est vide ou contient moins de 50
   * éléments.
   * @param fc collection
   * @return un nombre de dalles arbitraire à utiliser quand l'utilisateur ne le
   *         stipule pas.
   */
  private static int nbDallesXY(IFeatureCollection<? extends IFeature> fc) {
    return Math.max((int) Math.sqrt(fc.size() / 50), 1);
  }

  /**
   * Crée et instancie un dallage en reprenant les paramètres d'un autre
   * dallage.
   */
  public Tiling(IFeatureCollection<Feat> fc, Tiling<Feat> spIdx) {
    this(fc, (Boolean) spIdx.getParametres().get(1), (IEnvelope) spIdx
        .getParametres().get(2), (Integer) spIdx.getParametres().get(3));
  }

  // ===============================================
  // MISE A JOUR
  // ===============================================

  @Override
  public void update(Feat value, int cas) {
    int tab[];
    if (cas == 1) {// ajout
      if (value == null) {
        return;
      }
      IGeometry geom = value.getGeom();
      if (geom == null) {
        return;
      }
      IEnvelope envObjet = geom.envelope();
      tab = this.dallesIntersectees(envObjet);
      for (int i = tab[0]; i <= tab[1]; i++) {
        for (int j = tab[2]; j <= tab[3]; j++) {
          if (geom.intersects(this.dallesPolygones[i][j])) {
            synchronized (this.index) {
              this.index[i][j].add(value);
            }
          }
        }
      }
    } else if (cas == -1) {// suppression
      IEnvelope[] envs = this.getDallage(value);
      for (IEnvelope env : envs) {
        Iterator<List<Integer>> itDallesConcernees = this.getNumDallage(value)
            .iterator();
        while (itDallesConcernees.hasNext()) {
          List<Integer> num = itDallesConcernees.next();
          synchronized (this.index) {
            this.index[(num.get(0)).intValue()][(num.get(1)).intValue()]
                .remove(value);
          }
        }
      }
    } else if (cas == 0) {// modification : suppression puis ajout
      this.update(value, -1);
      this.update(value, +1);
    } else {
      System.out
          .println("spatialIndex.update(value, cas) : \"cas\" doit valoir +1, -1 ou 0."); //$NON-NLS-1$
    }
  }
}
