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

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.util.index.SpatialIndex;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Collection (liste) de IFeature. Peut porter un index spatial.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 * @composed 0 - n FT_Feature
 */
public class FT_FeatureCollection<Feat extends IFeature> implements
    Collection<Feat>, IFeatureCollection<Feat> {
  /**
   * The logger.
   */
  static Logger logger = Logger.getLogger(FT_FeatureCollection.class.getName());
  protected List<FeatureCollectionListener> listenerList = new ArrayList<FeatureCollectionListener>(
      0);

  @Override
  public void addFeatureCollectionListener(FeatureCollectionListener l) {
    this.listenerList.add(l);
  }

  @Override
  public void fireActionPerformed(FeatureCollectionEvent event) {
    // Guaranteed to return a non-null array
    FeatureCollectionListener[] listeners = this.listenerList
        .toArray(new FeatureCollectionListener[0]);
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 1; i >= 0; i -= 1) {
      listeners[i].changed(event);
    }
  }

  /**
   * Constructeur.
   */
  public FT_FeatureCollection() {
  }

  /**
   * Constructeur recopiant une autre collection. ATTENTION: ne recopie pas
   * l'éventuel index spatial.
   * 
   * @param listeACopier collection à recopier
   */
  public FT_FeatureCollection(IFeatureCollection<Feat> listeACopier) {
    this();
    this.setFlagGeom(listeACopier.getFlagGeom());
    this.setFlagTopo(listeACopier.hasTopo());
    this.getElements().addAll(listeACopier.getElements());
  }

  /**
   * Constructeur à partir d'une collection de FT_Feature.
   * @param collectionACopier collection à recopier
   */
  public FT_FeatureCollection(Collection<Feat> collectionACopier) {
    this();
    this.getElements().addAll(collectionACopier);
  }

  // ---------------------------------------
  // --- Indicateurs de geometrie et topo --
  // ---------------------------------------
  /**
   * Boolean indiquant si les FT_Feature portent une geometrie (true par
   * defaut).
   */
  protected boolean flagGeom = true;

  @Override
  public boolean getFlagGeom() {
    return this.flagGeom;
  }

  @Override
  public boolean hasGeom() {
    return this.flagGeom;
  }

  @Override
  public void setFlagGeom(boolean Geom) {
    this.flagGeom = Geom;
  }

  /**
   * Boolean indiquant si les FT_Feature portent une topologie (false par
   * defaut).
   */
  protected boolean flagTopo = false;

  @Override
  public boolean hasTopo() {
    return this.flagTopo;
  }

  @Override
  public void setFlagTopo(boolean Topo) {
    this.flagTopo = Topo;
  }

  // ---------------------------------------
  // --- Accesseurs ------------------------
  // ---------------------------------------
  /**
   * La liste des <code>Feature</code>s composant this.
   */
  protected List<Feat> elements = new ArrayList<Feat>(0);

  @Override
  public List<Feat> getElements() {
    return this.elements;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setElements(Collection<? extends Feat> liste) {
    synchronized (this.elements) {
      List<Feat> old = new ArrayList<Feat>(this.elements);
      for (Feat O : old) {
        this.elements.remove(O);
        O.getFeatureCollections().remove(this);
      }
      for (Feat O : liste) {
        this.elements.add(O);
        if (!O.getFeatureCollections().contains(this)) {
          O.getFeatureCollections().add((IFeatureCollection<IFeature>) this);
        }
      }
    }
    if (this.isIndexed) {
      this.removeSpatialIndex();
    }
  }

  @Override
  public Feat get(int i) {
    synchronized (this.elements) {
      return this.elements.get(i);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean add(Feat value) {
    if (value == null) {
      return false;
    }
    boolean result = false;
    synchronized (this.elements) {
      result = this.elements.add(value);
    }
    if (value.getFeatureCollections() != null) {
    	result = value.getFeatureCollections().add(
    			(IFeatureCollection<IFeature>) this)
    			&& result;
    }
    if (this.isIndexed && this.spatialindex.hasAutomaticUpdate()) {
      this.spatialindex.update(value, +1);
    }
    this.fireActionPerformed(new FeatureCollectionEvent(this, value,
        FeatureCollectionEvent.Type.ADDED, value.getGeom()));
    return result;
  }

  @Override
  public void addCollection(Collection<Feat> value) {
    if (value == null) {
      return;
    }
    synchronized (value) {
      for (Feat element : value) {
        this.add(element);
      }
    }
  }

  @Override
  public boolean remove(Feat value) {
    if (value == null) {
      logger.error("null feature"); //$NON-NLS-1$
      return false;
    }
    boolean result = false;
    synchronized (this.elements) {
      result = this.elements.remove(value);
    }
    value.getFeatureCollections().remove(this);
    if (this.isIndexed && this.spatialindex.hasAutomaticUpdate()) {
      this.spatialindex.update(value, -1);
    }

    this.fireActionPerformed(new FeatureCollectionEvent(this, value,
        FeatureCollectionEvent.Type.REMOVED, value.getGeom()));
    return result;
  }

  @Override
  public boolean removeAll(Collection<?> coll) {
    if (coll == null) {
      return false;
    }
    if (coll.isEmpty()) {
      return false;
    }
    boolean result = true;
    //  IEnvelope envelope = this.envelope();
    // List<GM_Object> removedCollectionGeometry = new ArrayList<GM_Object>();
    synchronized (this.elements) {
      for (Object o : coll) {
        result = this.remove(o) && result;
        /*
         * if (o instanceof FT_Feature) { FT_Feature feature = (FT_Feature) o;
         * if (feature.getGeom() != null) {
         * removedCollectionGeometry.add(feature.getGeom()); } }
         */
      }
    }
    this.fireActionPerformed(new FeatureCollectionEvent(this, null,
        FeatureCollectionEvent.Type.REMOVED,  this.getEnvelope().getGeom()));
        return result;
    }

    @Override
    public void clear() {
        synchronized (this.elements) {
            for (Feat feature : this) {
                feature.getFeatureCollections().remove(this);
            }
            this.elements.clear();
        }
        this.center = null;
        this.classe = null;
        this.featureType = null;
        this.listenerList.clear();
        if (this.isIndexed) {
            this.removeSpatialIndex();
        }
        /*
         * this.fireActionPerformed(new FeatureCollectionEvent(this, null,
         * FeatureCollectionEvent.Type.REMOVED,
         * JtsAlgorithms.union(removedCollectionGeometry)));
         */
    }

  @Override
  public int size() {
    return this.elements.size();
  }

  IEnvelope envelope = null;
  @Override
  public IEnvelope getEnvelope() {
      if (this.envelope == null) {
          this.envelope = this.envelope();
      }
      return this.envelope;
  }
  @Override
  public void setEnvelope(IEnvelope env) {
      this.envelope = env;
  }

  // ---------------------------------------
  // --- Calcul de l'emprise ---------------
  // ---------------------------------------
  @Override
  public IEnvelope envelope() {
    if (this.hasGeom()) {
      return this.getGeomAggregate().envelope();
    }
    FT_FeatureCollection.logger
        .warn("ATTENTION appel de envelope() sur une FT_FeatureCollection sans geometrie ! (renvoie null) "); //$NON-NLS-1$
    return null;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public IAggregate<? extends IGeometry> getGeomAggregate() {
    if (this.hasGeom()) {
      List<IGeometry> list = new ArrayList<IGeometry>(0);
      Class<?> geomType = null;
      boolean mixedGeom = false;
      for (IFeature f : this.getElements().toArray(new IFeature[0])) {
        if (f.getGeom() != null) {
          list.add(f.getGeom());
          if (geomType == null) {
            geomType = f.getGeom().getClass();
          } else {
            if (geomType != f.getGeom().getClass()) {
              mixedGeom = true;
            }
          }
        }
      }
      if (mixedGeom || geomType == null
          || GM_Aggregate.class.isAssignableFrom(geomType)) {
        GM_Aggregate<IGeometry> aggr = new GM_Aggregate<IGeometry>(list);
        return aggr;
      }
      if (GM_LineString.class.isAssignableFrom(geomType)) {
        GM_MultiCurve<?> aggr = new GM_MultiCurve(list);
        return aggr;
      }
      if (GM_Polygon.class.isAssignableFrom(geomType)) {
        GM_MultiSurface<?> aggr = new GM_MultiSurface(list);
        return aggr;
      }
      if (GM_Point.class.isAssignableFrom(geomType)) {
        GM_MultiPoint aggr = new GM_MultiPoint();
        for (IGeometry geom : list) {
          aggr.add((GM_Point) geom);
        }
        return aggr;
      }
      FT_FeatureCollection.logger.info(geomType);
    }
    FT_FeatureCollection.logger
        .warn("ATTENTION appel de getGeom() sur une FT_FeatureCollection sans geometrie ! (renvoie null) "); //$NON-NLS-1$
    return null;
  }

  private IDirectPosition center = null;

  @Override
  public IDirectPosition getCenter() {
    if (this.center == null) {
      this.center = this.envelope().center();
    }
    return this.center;
  }

  @Override
  public void setCenter(IDirectPosition dp) {
    this.center = dp;
  }

  // ---------------------------------------
  // --- Index spatial ---------------------
  // ---------------------------------------
  /** Index spatial. */
  private SpatialIndex<Feat> spatialindex;
  /** La collection possede-t-elle un index spatial ? */
  private boolean isIndexed = false;

  @Override
  public SpatialIndex<Feat> getSpatialIndex() {
    return this.spatialindex;
  }

  @Override
  public boolean hasSpatialIndex() {
    return this.isIndexed;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate) {
    if (!this.hasGeom()) {
      FT_FeatureCollection.logger
          .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
      return;
    }
    try {
      this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
          .getConstructor(
              new Class[] { IFeatureCollection.class, Boolean.class })
          .newInstance(new Object[] { this, new Boolean(automaticUpdate) });
      this.isIndexed = true;
    } catch (Exception e) {
      FT_FeatureCollection.logger
          .error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate, int i) {
    if (!this.hasGeom()) {
      FT_FeatureCollection.logger
          .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
      return;
    }
    try {
      this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
          .getConstructor(
              new Class[] { IFeatureCollection.class, Boolean.class,
                  Integer.class })
          .newInstance(
              new Object[] { this, new Boolean(automaticUpdate), new Integer(i) });
      this.isIndexed = true;
    } catch (Exception e) {
      FT_FeatureCollection.logger
          .error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate, IEnvelope enveloppe, int i) {
    if (!this.hasGeom()) {
      FT_FeatureCollection.logger
          .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
      return;
    }
    try {
      this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
          .getConstructor(
              new Class[] { IFeatureCollection.class, Boolean.class,
                  IEnvelope.class, Integer.class }).newInstance(
              new Object[] { this, new Boolean(automaticUpdate), enveloppe,
                  new Integer(i) });
      this.isIndexed = true;
    } catch (Exception e) {
      FT_FeatureCollection.logger
          .error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void initSpatialIndex(SpatialIndex<?> spIdx) {
    // enlevé : Class spatialIndexClass,
    if (!this.hasGeom()) {
      FT_FeatureCollection.logger
          .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
      return;
    }
    try {
      this.spatialindex = spIdx
          .getClass()
          .getConstructor(
              new Class[] { IFeatureCollection.class, spIdx.getClass() })
          .newInstance(new Object[] { this, spIdx });
      this.isIndexed = true;
    } catch (Exception e) {
      FT_FeatureCollection.logger
          .error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
      e.printStackTrace();
    }
  }

  @Override
  public void removeSpatialIndex() {
    this.spatialindex = null;
    this.isIndexed = false;
  }

  // ---------------------------------------
  // --- SELECTION AVEC L'Index spatial ----
  // ---------------------------------------
  @Override
  public Collection<Feat> select(IDirectPosition P, double D) {
    if (!this.isIndexed) {
      Collection<Feat> selectedFeatures = new HashSet<Feat>(0);
      synchronized (this.elements) {
        for (Feat feature : this) {
          if (feature.getGeom().distance(P.toGM_Point()) <= D) {
            selectedFeatures.add(feature);
          }
        }
      }
      return selectedFeatures;
    }
    return this.spatialindex.select(P, D);
  }

  @Override
  public Collection<Feat> select(IEnvelope env) {
    if (env.width() == 0 || env.length() == 0 || env.isEmpty()) {
      return new HashSet<Feat>(0);
    }
    IPolygon envGeom = env.getGeom();
    if (!this.isIndexed) {
      Collection<Feat> selectedFeatures = new HashSet<Feat>(0);
      synchronized (this.elements) {
        selectedFeatures.addAll(this.elements);
      }
      for (Iterator<Feat> it = selectedFeatures.iterator(); it.hasNext();) {
        Feat feature = it.next();
        if (feature.getGeom() == null || !feature.getGeom().intersects(envGeom)) {
          it.remove();
        }
      }
      return selectedFeatures;
    }
    return this.spatialindex.select(env);
  }

  @Override
  public Collection<Feat> select(IGeometry geometry) {
    if (!this.isIndexed) {
      Collection<Feat> selectedFeatures = new HashSet<Feat>(0);
      synchronized (this.elements) {
        for (Feat feature : this) {
          if (feature.getGeom().intersects(geometry)) {
            selectedFeatures.add(feature);
          }
        }
      }
      return selectedFeatures;
    }
    return this.spatialindex.select(geometry);
  }

  @Override
  public Collection<Feat> select(IGeometry geometry, boolean strictlyCrosses) {
    if (!this.isIndexed) {
      Collection<Feat> selectedFeatures = new HashSet<Feat>(0);
      synchronized (this.elements) {
        for (Feat feature : this) {
          if (feature.getGeom().intersectsStrictement(geometry)) {
            selectedFeatures.add(feature);
          }
        }
      }
      return selectedFeatures;
    }
    return this.spatialindex.select(geometry, strictlyCrosses);
  }

  @Override
  public Collection<Feat> select(IGeometry geometry, double distance) {
    if (!this.isIndexed) {
      Collection<Feat> selectedFeatures = new HashSet<Feat>(0);
      synchronized (this.elements) {
        for (Feat feature : this) {
          if (feature.getGeom().distance(geometry) <= distance) {
            selectedFeatures.add(feature);
          }
        }
      }
      return selectedFeatures;
    }
    return this.spatialindex.select(geometry, distance);
  }

  // ---------------------------------------
  // --- méthodes nécessaire pour implémenter l'interface Collection
  // ---------------------------------------
  @Override
  public boolean contains(Feat value) {
    synchronized (this.elements) {
      return this.elements.contains(value);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void addUnique(Feat feature) {
    if (feature == null) {
      return;
    }
    if (this.elements.contains(feature)) {
      return;
    }
    synchronized (this.elements) {
      this.elements.add(feature);
    }
    feature.getFeatureCollections().add((IFeatureCollection<IFeature>) this);
    if (this.isIndexed) {
      if (this.spatialindex.hasAutomaticUpdate()) {
        this.spatialindex.update(feature, +1);
      }
    }

//        this.fireActionPerformed(new FeatureCollectionEvent(this, feature,
//                FeatureCollectionEvent.Type.ADDED, feature.getGeom()));
  }

  /**
   * Efface de la liste l'element en position i. Attention, si l'élément est
   * persistant, celui-ci n'est pas détruit, le faire après au besoin.
   * 
   * @param i indice de l'élément à supprimer
   */
  @Override
  public void remove(int i) {
    if (i > this.size()) {
      return;
    }
    Feat value = this.get(i);
    synchronized (this.elements) {
      this.elements.remove(value);
    }
    value.getFeatureCollections().remove(this);
    if (this.isIndexed) {
      if (this.spatialindex.hasAutomaticUpdate()) {
        this.spatialindex.update(value, -1);
      }
    }

    this.fireActionPerformed(new FeatureCollectionEvent(this, value,
        FeatureCollectionEvent.Type.REMOVED, value.getGeom()));
  }

  @Override
  public void removeCollection(IFeatureCollection<Feat> value) {
    if (value == null) {
      return;
    }
    for (Feat f : value.getElements()) {
      this.remove(f);
    }
  }

  @Override
  public void addUniqueCollection(IFeatureCollection<? extends Feat> value) {
    if (value == null) {
      return;
    }
    synchronized (value.getElements()) {
      for (Feat elem : value.getElements()) {
        this.addUnique(elem);
      }
    }
  }

  @Override
  public Iterator<Feat> iterator() {
    synchronized (this.elements) {
      return this.elements.iterator();
    }
  }

  @Override
  public boolean contains(Object obj) {
    synchronized (this.elements) {
      return this.elements.contains(obj);
    }
  }

  @Override
  public boolean containsAll(Collection<?> coll) {
    Iterator<?> i = coll.iterator();
    while (i.hasNext()) {
      Object element = i.next();
      if (!this.contains(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(Object obj) {
    return this.remove((Feat) obj);
  }

  @Override
  public boolean retainAll(Collection<?> coll) {
    List<Feat> toRemove = new ArrayList<Feat>(0);
    synchronized (this.elements) {
      for (Feat feature : this.elements) {
        if (!coll.contains(feature)) {
          toRemove.add(feature);
        }
      }
    }
    return this.removeAll(toRemove);
  }

  @Override
  public Object[] toArray() {
    synchronized (this.elements) {
      return this.elements.toArray();
    }
  }

  @Override
  public boolean addAll(Collection<? extends Feat> c) {
    if (c == null) {
      return false;
    }
    Iterator<? extends Feat> iter = c.iterator();
    boolean result = true;
    while (iter.hasNext()) {
      Feat elem = iter.next();
      result = this.add(elem) && result;
    }
    return result;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    synchronized (this.elements) {
      return this.elements.toArray(a);
    }
  }

  // ---------------------------------------
  // --- méthodes contenant les FeatureTypes
  // ---------------------------------------
  /**
   * Classe par défaut des instances de la population. Ceci est utile pour
   * pouvoir savoir dans quelle classe créer de nouvelles instances.
   */
  protected Class<Feat> classe;

  @Override
  public Class<Feat> getClasse() {
    return this.classe;
  }

  @Override
  public void setClasse(Class<Feat> C) {
    this.classe = C;
    this.nomClasse = this.classe.getName();
  }

  /**
   * Nom complet (package+classe java) de la classe par défaut des instances de
   * la population. Pertinent uniquement pour les population peristantes.
   */
  protected String nomClasse = ""; //$NON-NLS-1$

  @Override
  public void setNomClasse(String S) {
    this.nomClasse = S;
  }

  @Override
  @Transient
  public String getNomClasse() {
    if (this.getFeatureType() != null) {
      return this.getFeatureType().getNomClasse();
    }
    return this.nomClasse;
  }

  /**
   * méthodes permettant de manipuler une population normale, avec lien vers le
   * FeatureType correspondant. Ce type de Population n'a pas besoin d'être
   * persistent (il peut l'être cependant) car les métadonnées qu'il
   * représentent sont déjà stockées dans le schéma conceptuel du jeu.
   */
  protected FeatureType featureType;

  @Override
  @ManyToOne
  public FeatureType getFeatureType() {
    return this.featureType;
  }

  @Override
  public void setFeatureType(FeatureType featureType) {
    this.featureType = featureType;
  }
}
