/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.index.SpatialIndex;

/**
 * Collection (liste) de FT_Feature. Peut porter un index spatial.
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 * @composed 0 - n FT_Feature
 */
public class FT_FeatureCollection<Feat extends FT_Feature> implements
            Collection<Feat> {
    static Logger logger = Logger.getLogger(FT_FeatureCollection.class
                .getName());
    protected List<FeatureCollectionListener> listenerList
    = new ArrayList<FeatureCollectionListener>();

    /**
     * Ajoute un {@link FeatureCollectionListener}.
     * <p>
     * Adds a {@link FeatureCollectionListener}.
     * @param l
     *            le {@link FeatureCollectionListener} à ajouter. the
     *            {@link FeatureCollectionListener} to be added.
     */
    public void addFeatureCollectionListener(FeatureCollectionListener l) {
        this.listenerList.add(l);
    }

    /**
     * prévient tous les {@link FeatureCollectionListener} enregistrès qu'un
     * évènement a eu lieu.
     * <p>
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created.
     */
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
     * Constructeur
     */
    public FT_FeatureCollection() {
    }

    /**
     * Constructeur recopiant une autre collection. ATTENTION: ne recopie pas
     * l'éventuel index spatial.
     *
     * @param listeACopier
     *            collection à recopier
     */
    public FT_FeatureCollection(FT_FeatureCollection<Feat> listeACopier) {
        this.setFlagGeom(listeACopier.getFlagGeom());
        this.setFlagTopo(listeACopier.flagTopo);
        this.getElements().addAll(listeACopier.getElements());
    }

    /**
     * Constructeur à partir d'une collection de FT_Feature
     *
     * @param collectionACopier
     *            collection à recopier
     */
    public FT_FeatureCollection(Collection<Feat> collectionACopier) {
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

    /**
     * Boolean indiquant si les FT_Feature portent une geometrie.
     *
     * @return vrai si les FT_Feature portent une geometrie.
     */
    public boolean getFlagGeom() {
        return this.flagGeom;
    }

    /**
     * Boolean indiquant si les FT_Feature portent une geometrie.
     *
     * @return vrai si les FT_Feature portent une geometrie.
     */
    public boolean hasGeom() {
        return this.flagGeom;
    }

    /**
     * Boolean indiquant si les FT_Feature portent une geometrie.
     *
     * @param Geom
     *            nouveau flag
     */
    public void setFlagGeom(boolean Geom) {
        this.flagGeom = Geom;
    }

    /**
     * Boolean indiquant si les FT_Feature portent une topologie (false par
     * defaut).
     */
    protected boolean flagTopo = false;

    /**
     * Boolean indiquant si les FT_Feature portent une topologie.
     *
     * @return vrai si les FT_Feature portent une topologie.
     */
    public boolean hasTopo() {
        return this.flagTopo;
    }

    /**
     * Boolean indiquant si les FT_Feature portent une topologie.
     *
     * @param Topo
     *            nouveau flag
     */
    public void setFlagTopo(boolean Topo) {
        this.flagTopo = Topo;
    }

    // ---------------------------------------
    // --- Accesseurs ------------------------
    // ---------------------------------------
    /**
     * La liste des <code>Feature</code>s composant this.
     */
    protected List<Feat> elements = new ArrayList<Feat>();

    /**
     * Renvoie la liste de <code>Feature</code>s composant this.
     *
     * @return la liste de <code>Feature</code>s composant this.
     */
    public List<Feat> getElements() {
        return this.elements;
    }

    /**
     * Affecte une liste de <code>Feature</code>s à this, et met à jour le
     * lien inverse.
     * Attention detruit l'index spatial si celui existait. Il faut donc le
     * reinitialiser si on souhaite l'utiliser.
     *
     * @param liste
     *            liste de <code>Feature</code>s à affecter
     */
    @SuppressWarnings("unchecked")
    public void setElements(Collection<Feat> liste) {
        synchronized (this.elements) {
            List<Feat> old = new ArrayList<Feat>(this.elements);
            for (Feat O : old) {
                this.elements.remove(O);
                O.getFeatureCollections().remove(this);
            }
            for (Feat O : liste) {
                this.elements.add(O);
                if (!O.getFeatureCollections().contains(this)) { O
                    .getFeatureCollections().add(
                            (FT_FeatureCollection<FT_Feature>) this);
                }
            }
        }
        if (this.isIndexed) { removeSpatialIndex(); }
    }

    /**
     * Renvoie le i-eme element de la liste des composants de this.
     *
     * @param i
     *            indice de l'élément à renvoyer
     * @return le i-eme element de la liste des composants de this.
     */
    public Feat get(int i) {
        synchronized (this.elements) {
            return this.elements.get(i);
        }
    }

    /**
     * Ajoute un element a la liste des composants de this, et met à jour le
     * lien inverse.
     */
    @SuppressWarnings("unchecked")
    public boolean add(Feat value) {
        if (value == null) { return false; }
        boolean result = false;
        synchronized (this.elements) {
            result = this.elements.add(value);
        }
        result = value.getFeatureCollections().add(
                    (FT_FeatureCollection<FT_Feature>) this)
                    && result;
        if (this.isIndexed && this.spatialindex.hasAutomaticUpdate()) {
            this.spatialindex.update(value, +1);
        }
        this.fireActionPerformed(new FeatureCollectionEvent(this, value,
                    FeatureCollectionEvent.Type.ADDED, value.getGeom()));
        return result;
    }

    /**
     * Ajoute les éléments d'une FT_FeatureCollection a la liste des
     * composants
     * de this, et met à jour le lien inverse.
     */
    public void addCollection(FT_FeatureCollection<Feat> value) {
        if (value == null) { return; }
        synchronized (value.elements) {
            for (Feat element : value.elements) {
                this.add(element);
            }
        }
    }

    /**
     * Efface de la liste l'element passe en parametre. Attention, si
     * l'élément
     * est persistant, celui-ci n'est pas détruit, le faire après au besoin.
     */
    public boolean remove(Feat value) {
        if (value == null) { return false; }
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

    /**
     * Efface de la liste tous les éléments de la collection passée en
     * paramètre. Attention, si l'élément est persistant, celui-ci n'est pas
     * détruit, le faire après au besoin.
     */
    @Override
    public boolean removeAll(Collection<?> coll) {
        if (coll == null) { return false; }
        if (coll.size() == 0) { return false; }
        boolean result = true;
        List<GM_Object> removedCollectionGeometry = new ArrayList<GM_Object>();
        synchronized (this.elements) {
            for (Object o : coll) {
                result = this.remove(o) && result;
                if (o instanceof FT_Feature) {
                    FT_Feature feature = (FT_Feature) o;
                    if (feature.getGeom() != null) {
                        removedCollectionGeometry.add(feature.getGeom());
                    }
                }
            }
        }
        this.fireActionPerformed(new FeatureCollectionEvent(this, null,
                    FeatureCollectionEvent.Type.REMOVED, JtsAlgorithms.union(removedCollectionGeometry)));
        return result;
    }

    /**
     * Efface toute la liste. Detruit l'index spatial si celui existe.
     *
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear() {
        List<GM_Object> removedCollectionGeometry = new ArrayList<GM_Object>();
        synchronized (this.elements) {
            for (Feat feature : this) {
                feature.getFeatureCollections().remove(this);
                if (feature.getGeom() != null) {
                    removedCollectionGeometry.add(feature.getGeom());
                }
            }
            this.elements.clear();
        }
        if (this.isIndexed) { removeSpatialIndex(); }
        this.fireActionPerformed(new FeatureCollectionEvent(this, null,
                    FeatureCollectionEvent.Type.REMOVED, JtsAlgorithms.union(removedCollectionGeometry)));
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    // ---------------------------------------
    // --- Calcul de l'emprise ---------------
    // ---------------------------------------
    /** Calcul l'emprise rectangulaire des geometries de la collection. */
    public GM_Envelope envelope() {
        if (this.hasGeom()) { return this.getGeomAggregate().envelope(); }
        logger
        .warn("ATTENTION appel de envelope() sur une FT_FeatureCollection sans geometrie ! (renvoie null) "); //$NON-NLS-1$
        return null;
    }

    /**
     * Renvoie toutes les geometries sous la forme d'un GM_Aggregate.
     *
     * @return toutes les geometries sous la forme d'un GM_Aggregate.
     */
    @SuppressWarnings("unchecked")
    public GM_Aggregate<? extends GM_Object> getGeomAggregate() {
        if (this.hasGeom()) {
            List<GM_Object> list = new ArrayList<GM_Object>();
            Class<?> geomType = null;
            boolean mixedGeom = false;
            for (FT_Feature f : this.getElements().toArray(new FT_Feature[0])) {
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
            if (mixedGeom || geomType == null || GM_Aggregate.class.isAssignableFrom(geomType)) {
                GM_Aggregate<GM_Object> aggr = new GM_Aggregate<GM_Object>(list);
                return aggr;
            }
            if (geomType.equals(GM_LineString.class)) {
                GM_MultiCurve aggr = new GM_MultiCurve(list);
                return aggr;
            }
            if (geomType.equals(GM_Polygon.class)) {
                GM_MultiSurface aggr = new GM_MultiSurface(list);
                return aggr;
            }
            if (geomType.equals(GM_Point.class)) {
                GM_MultiPoint aggr = new GM_MultiPoint();
                for (GM_Object geom : list) {
                    aggr.add((GM_Point) geom);
                }
                return aggr;
            }
            logger.info(geomType);
        }
        logger
        .warn("ATTENTION appel de getGeom() sur une FT_FeatureCollection sans geometrie ! (renvoie null) "); //$NON-NLS-1$
        return null;
    }

    private DirectPosition center = null;

    /**
     * Renvoie la position du centre de l'envelope de la collection.
     *
     * @return la position du centre de l'envelope de la collection
     */
    public DirectPosition getCenter() {
        if (this.center == null) { this.center = envelope().center(); }
        return this.center;
    }

    /**
     * Affecte la position du centre de l'envelope de la collection.
     *
     * @param center
     *            la position du centre de l'envelope de la collection
     */
    public void setCenter(DirectPosition center) {
        this.center = center;
    }

    // ---------------------------------------
    // --- Index spatial ---------------------
    // ---------------------------------------
    /** Index spatial. */
    private SpatialIndex<Feat> spatialindex;
    /** La collection possede-t-elle un index spatial ? */
    private boolean isIndexed = false;

    /**
     * Index spatial.
     *
     * @return l'index spatial
     */
    public SpatialIndex<Feat> getSpatialIndex() {
        return this.spatialindex;
    }

    /**
     * La collection possede-t-elle un index spatial ?
     *
     * @return vrai si la collection possède un index spatial, faux sinon
     */
    public boolean hasSpatialIndex() {
        return this.isIndexed;
    }

    /**
     * Initialise un index spatial avec détermination automatique des
     * paramètres. Le boolean indique si on souhaite une mise a jour
     * automatique
     * de l'index.
     *
     * @param spatialIndexClass
     *            Nom de la classe d'index.
     * @param automaticUpdate
     *            spéciifie si l'index doit être mis à jour automatiquement
     *            quand on modifie les objets de fc.
     */
    @SuppressWarnings("unchecked")
    public void initSpatialIndex(Class<?> spatialIndexClass,
                boolean automaticUpdate) {
        if (!this.hasGeom()) {
            logger
                        .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
            return;
        }
        try {
            this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
                        .getConstructor(
                                    new Class[] { FT_FeatureCollection.class,
                                                Boolean.class }).newInstance(
                                    new Object[] { this,
                                                new Boolean(automaticUpdate) });
            this.isIndexed = true;
        } catch (Exception e) {
            logger.error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * Initialise un index spatial avec un parametre entier (utilise pour le
     * dallage). Le boolean indique si on souhaite une mise a jour automatique
     * de l'index.
     *
     * @param spatialIndexClass
     *            Nom de la classe d'index.
     * @param automaticUpdate
     *            spéciifie si l'index doit être mis à jour automatiquement
     *            quand on modifie les objets de fc.
     * @param i
     *            Nombre de dalles en X et en Y, du dallage.
     */
    @SuppressWarnings("unchecked")
    public void initSpatialIndex(Class<?> spatialIndexClass,
                boolean automaticUpdate, int i) {
        if (!this.hasGeom()) {
            logger
                        .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
            return;
        }
        try {
            this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
                        .getConstructor(
                                    new Class[] { FT_FeatureCollection.class,
                                                Boolean.class, Integer.class })
                        .newInstance(
                                    new Object[] { this,
                                                new Boolean(automaticUpdate),
                                                new Integer(i) });
            this.isIndexed = true;
        } catch (Exception e) {
            logger.error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * Initialise un index spatial d'une collection de FT_Feature, en prenant
     * pour paramètre les limites de la zone et un entier (pour le dallage, cet
     * entier est le nombre en X et Y de cases souhaitées sur la zone).
     *
     * @param spatialIndexClass
     *            Nom de la classe d'index.
     * @param automaticUpdate
     *            spéciifie si l'index doit être mis à jour automatiquement
     *            quand on modifie les objets de fc.
     * @param enveloppe
     *            Enveloppe décrivant les limites de l'index spatial. NB: Tout
     *            objet hors de ces limites ne sera pas traité lors des
     *            requêtes
     *            spatiales !!!!!
     * @param i
     *            Nombre de dalles en X et en Y, du dallage.
     */
    @SuppressWarnings("unchecked")
    public void initSpatialIndex(Class<?> spatialIndexClass,
                boolean automaticUpdate, GM_Envelope enveloppe, int i) {
        if (!this.hasGeom()) {
            logger
                        .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
            return;
        }
        try {
            this.spatialindex = (SpatialIndex<Feat>) spatialIndexClass
                        .getConstructor(
                                    new Class[] { FT_FeatureCollection.class,
                                                Boolean.class,
                                                GM_Envelope.class,
                                                Integer.class }).newInstance(
                                    new Object[] { this,
                                                new Boolean(automaticUpdate),
                                                enveloppe, new Integer(i) });
            this.isIndexed = true;
        } catch (Exception e) {
            logger.error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * Initialise un index spatial d'une collection de FT_Feature, en prenant
     * pour paramètre ceux d'un index existant.
     *
     * @param spIdx
     *            un index spatial existant
     */
    @SuppressWarnings("unchecked")
    public void initSpatialIndex(SpatialIndex<?> spIdx) {
        // enlevé : Class spatialIndexClass,
        if (!this.hasGeom()) {
            logger
                        .warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !"); //$NON-NLS-1$
            return;
        }
        try {
            this.spatialindex = spIdx.getClass().getConstructor(
                        new Class[] { FT_FeatureCollection.class,
                                    spIdx.getClass() }).newInstance(
                        new Object[] { this, spIdx });
            this.isIndexed = true;
        } catch (Exception e) {
            logger.error("Probleme a l'initialisation de l'index spatial !"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * détruit l'index spatial.
     */
    public void removeSpatialIndex() {
        this.spatialindex = null;
        this.isIndexed = false;
    }

    // ---------------------------------------
    // --- SELECTION AVEC L'Index spatial ----
    // ---------------------------------------
    /**
     * Selection dans le carre dont P est le centre, de cote D.
     *
     * @param P
     *            le centre
     * @param D
     *            cote
     * @return objets qui intersectent le carre dont P est le centre, de cote D.
     */
    public FT_FeatureCollection<Feat> select(DirectPosition P, double D) {
        if (!this.isIndexed) {
            FT_FeatureCollection<Feat> selectedFeatures = new FT_FeatureCollection<Feat>();
            synchronized (this.elements) {
                for (Feat feature : this) {
                    if (feature.getGeom().distance(P.toGM_Point()) <= D) selectedFeatures
                    .add(feature);
                }
            }
            return selectedFeatures;
        }
        return this.spatialindex.select(P, D);
    }

    /**
     * Selection dans un rectangle.
     *
     * @param env
     *            rectangle
     * @return objets qui intersectent un rectangle
     */
    public FT_FeatureCollection<Feat> select(GM_Envelope env) {
        if (env.width() == 0 || env.length() == 0 || env.isEmpty()) {
            return new FT_FeatureCollection<Feat>();
        }
        GM_Object envGeom = env.getGeom();
        if (!this.isIndexed) {
            FT_FeatureCollection<Feat> selectedFeatures = new FT_FeatureCollection<Feat>();
            synchronized (this.elements) {
                selectedFeatures.addAll(this.elements);
            }
            for (Iterator<Feat> it = selectedFeatures.iterator(); it.hasNext();) {
                Feat feature = it.next();
                if (!feature.getGeom().intersects(envGeom)) {
                    it.remove();
                }
            }
            return selectedFeatures;
        }
        return this.spatialindex.select(env);
    }

    /**
     * Selection des objets qui intersectent un objet geometrique quelconque.
     *
     * @param geometry
     *            geometrie quelconque
     * @return objets qui intersectent un objet geometrique quelconque.
     */
    public FT_FeatureCollection<Feat> select(GM_Object geometry) {
        if (!this.isIndexed) {
            FT_FeatureCollection<Feat> selectedFeatures = new FT_FeatureCollection<Feat>();
            synchronized (this.elements) {
                for (Feat feature : this) {
                    if (feature.getGeom().intersects(geometry)) selectedFeatures
                    .add(feature);
                }
            }
            return selectedFeatures;
        }
        return this.spatialindex.select(geometry);
    }

    /**
     * Selection des objets qui croisent ou intersectent un objet geometrique
     * quelconque.
     *
     * @param geometry
     *            un objet geometrique quelconque
     * @param strictlyCrosses
     *            Si c'est TRUE : ne retient que les objets qui croisent (CROSS
     *            au sens JTS). Si c'est FALSE : ne retient que les objets qui
     *            intersectent (INTERSECT au sens JTS) Exemple : si 1 ligne
     *            touche "geometry" juste sur une extrémité, alors avec TRUE
     *            cela ne renvoie pas la ligne, avec FALSE cela la renvoie
     * @return objets qui intersectent strictement un objet geometrique
     *         quelconque
     */
    public FT_FeatureCollection<Feat> select(GM_Object geometry,
                boolean strictlyCrosses) {
        if (!this.isIndexed) {
            FT_FeatureCollection<Feat> selectedFeatures = new FT_FeatureCollection<Feat>();
            synchronized (this.elements) {
                for (Feat feature : this) {
                    if (feature.getGeom().intersectsStrictement(geometry)) selectedFeatures
                    .add(feature);
                }
            }
            return selectedFeatures;
        }
        return this.spatialindex.select(geometry, strictlyCrosses);
    }

    /**
     * Selection a l'aide d'un objet geometrique quelconque et d'une distance.
     *
     * @param geometry
     *            geometrie quelconque.
     * @param distance
     *            distance maximum
     * @return objets à moins d'une certaine distance d'un objet geometrique
     *         quelconque.
     */
    public FT_FeatureCollection<Feat> select(GM_Object geometry,
                double distance) {
        if (!this.isIndexed) {
            FT_FeatureCollection<Feat> selectedFeatures
            = new FT_FeatureCollection<Feat>();
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
    /**
     * Encapsulation de la methode contains() avec typage
     *
     * @param value
     *            valeur
     * @return vrai si la collection contient la valeur, faux sinon
     */
    public boolean contains(Feat value) {
        synchronized (this.elements) {
            return this.elements.contains(value);
        }
    }

    /**
     * Ajoute un element a la liste des composants de this s'il n'est pas déjà
     * présent, et met à jour le lien inverse.
     *
     * @param feature
     *            élément à ajouter
     */
    @SuppressWarnings("unchecked")
    public void addUnique(Feat feature) {
        if (feature == null) { return; }
        if (this.elements.contains(feature)) { return; }
        synchronized (this.elements) {
            this.elements.add(feature);
        }
        feature.getFeatureCollections().add(
                    (FT_FeatureCollection<FT_Feature>) this);
        if (this.isIndexed) {
            if (this.spatialindex.hasAutomaticUpdate()) {
                this.spatialindex.update(feature, +1);
            }
        }
        this.fireActionPerformed(new FeatureCollectionEvent(this, feature,
                    FeatureCollectionEvent.Type.ADDED, feature.getGeom()));
    }

    /**
     * Efface de la liste l'element en position i. Attention, si l'élément est
     * persistant, celui-ci n'est pas détruit, le faire après au besoin.
     *
     * @param i
     *            indice de l'élément à supprimer
     */
    public void remove(int i) {
        if (i > this.size()) { return; }
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

    /**
     * Efface de la liste la collection passée en parametre. Attention, si
     * l'élément est persistant, celui-ci n'est pas détruit, le faire après
     * au
     * besoin.
     *
     * @param value
     *            collection d'éléments à effacer
     */
    public void removeCollection(FT_FeatureCollection<Feat> value) {
        if (value == null) { return; }
        for (Feat f : value.getElements()) { remove(f); }
    }

    /**
     * Ajoute les éléments d'une FT_FeatureCollection a la liste des
     * composants
     * de this, et met à jour le lien inverse.
     *
     * @param value
     *            collection d'éléments à ajouter
     */
    public void addUniqueCollection(
                FT_FeatureCollection<? extends Feat> value) {
        if (value == null) { return; }
        synchronized (value.elements) {
            for (Feat elem : value.elements) {
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
            if (!contains(element)) { return false; }
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
        List<Feat> toRemove = new ArrayList<Feat>();
        synchronized (this.elements) {
            for (Feat feature : this.elements) {
                if (!coll.contains(feature)) { toRemove.add(feature); }
            }
        }
        return removeAll(toRemove);
    }

    @Override
    public Object[] toArray() {
        synchronized (this.elements) {
            return this.elements.toArray();
        }
    }

    @Override
    public boolean addAll(Collection<? extends Feat> c) {
        if (c == null) { return false; }
        Feat elem;
        Iterator<? extends Feat> iter = c.iterator();
        boolean result = true;
        while (iter.hasNext()) {
            elem = iter.next();
            result = add(elem) && result;
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
     * Classe par défaut des instances de la population.
     * Ceci est utile pour pouvoir savoir dans quelle classe créer de nouvelles
     * instances.
     */
    protected Class<Feat> classe;

    public Class<Feat> getClasse() {
        return this.classe;
    }

    public void setClasse(Class<Feat> C) {
        this.classe = C;
        this.nomClasse = this.classe.getName();
    }

    /**
     * Nom complet (package+classe java) de la classe par défaut des instances
     * de la population.
     * Pertinent uniquement pour les population peristantes.
     */
    protected String nomClasse = ""; //$NON-NLS-1$

    /**
     * définit le nom complet (package+classe java) de la classe par défaut
     * des instances de la population.
     * CONSEIL : ne pas utiliser cette méthode directement, remplir en
     * utilisant setClasse().
     * Ne met pas à jour l'attribut classe.
     * Utile uniquement pour les population peristantes.
     */
    public void setNomClasse(String S) {
        this.nomClasse = S;
    }

    /**
     * Récupère le nom complet (package+classe java) de la classe par défaut
     * des instances de la population.
     * <p>
     * Pertinent uniquement pour les population peristantes.
     * <p>
     * surcharge Population.getNomClasse() en passant par le featureType
     */
    @Transient
    public String getNomClasse() {
        if (this.getFeatureType() != null) {
            return this.getFeatureType().getNomClasse();
        }
        return this.nomClasse;
    }

    /**
     * méthodes permettant de manipuler une population normale,
     * avec lien vers le FeatureType correspondant. Ce type de Population n'a
     * pas besoin d'être persistent (il peut l'être cependant) car les
     * métadonnées qu'il représentent sont déjà stockées dans le schéma
     * conceptuel du jeu.
     */
    protected FeatureType featureType;

    /**
     * @return Returns the featureType.
     */
    @ManyToOne
    public FeatureType getFeatureType() {
        return this.featureType;
    }

    /**
     * @param featureType
     *            The featureType to set.
     */
    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }
}
