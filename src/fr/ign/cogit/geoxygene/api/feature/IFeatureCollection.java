package fr.ign.cogit.geoxygene.api.feature;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.util.index.SpatialIndex;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 * @param <Feat>
 */
public interface IFeatureCollection<Feat extends IFeature> extends
    Collection<Feat> {
  /**
   * Ajoute un {@link FeatureCollectionListener}.
   * <p>
   * Adds a {@link FeatureCollectionListener}.
   * @param l le {@link FeatureCollectionListener} à ajouter. the
   *          {@link FeatureCollectionListener} to be added.
   */
  public abstract void addFeatureCollectionListener(FeatureCollectionListener l);

  /**
   * Prévient tous les {@link FeatureCollectionListener} enregistrés qu'un
   * évènement a eu lieu.
   * <p>
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   */
  public abstract void fireActionPerformed(FeatureCollectionEvent event);

  /**
   * Boolean indiquant si les FT_Feature portent une geometrie.
   * @return vrai si les FT_Feature portent une geometrie.
   */
  public abstract boolean getFlagGeom();

  /**
   * Boolean indiquant si les FT_Feature portent une geometrie.
   * @return vrai si les FT_Feature portent une geometrie.
   */
  public abstract boolean hasGeom();

  /**
   * Boolean indiquant si les FT_Feature portent une geometrie.
   * @param Geom nouveau flag
   */
  public abstract void setFlagGeom(boolean Geom);

  /**
   * Boolean indiquant si les FT_Feature portent une topologie.
   * @return vrai si les FT_Feature portent une topologie.
   */
  public abstract boolean hasTopo();

  /**
   * Boolean indiquant si les FT_Feature portent une topologie.
   * @param Topo nouveau flag
   */
  public abstract void setFlagTopo(boolean Topo);

  /**
   * Renvoie la liste de <code>Feature</code>s composant this.
   * @return la liste de <code>Feature</code>s composant this.
   */
  public abstract List<Feat> getElements();

  /**
   * Affecte une liste de <code>Feature</code>s à this, et met à jour le lien
   * inverse. Attention detruit l'index spatial si celui existait. Il faut donc
   * le reinitialiser si on souhaite l'utiliser.
   * @param liste liste de <code>Feature</code>s à affecter
   */
  public abstract void setElements(Collection<? extends Feat> liste);

  /**
   * Renvoie le i-eme element de la liste des composants de this.
   * @param i indice de l'élément à renvoyer
   * @return le i-eme element de la liste des composants de this.
   */
  public abstract Feat get(int i);

  /**
   * Ajoute un element a la liste des composants de this, et met à jour le lien
   * inverse.
   */
  @Override
  public abstract boolean add(Feat value);

  /**
   * Ajoute les éléments d'une FT_FeatureCollection a la liste des composants de
   * this, et met à jour le lien inverse.
   */
  public abstract void addCollection(Collection<Feat> value);

  /**
   * Efface de la liste l'element passe en parametre. Attention, si l'élément
   * est persistant, celui-ci n'est pas détruit, le faire après au besoin.
   */
  public abstract boolean remove(Feat value);

  /**
   * Efface de la liste tous les élements de la collection passée en paramètre.
   * Attention, si l'élément est persistant, celui-ci n'est pas détruit, le
   * faire après au besoin.
   */
  @Override
  public abstract boolean removeAll(Collection<?> coll);

  /**
   * Efface toute la liste. Detruit l'index spatial si celui existe.
   * 
   * @see java.util.Collection#clear()
   */
  @Override
  public abstract void clear();

  @Override
  public abstract int size();

  public abstract IEnvelope getEnvelope();
  public abstract void setEnvelope(IEnvelope env);

  // ---------------------------------------
  // --- Calcul de l'emprise ---------------
  // ---------------------------------------
  /** Calcul l'emprise rectangulaire des geometries de la collection. */
  public abstract IEnvelope envelope();

  /**
   * Renvoie toutes les geometries sous la forme d'un GM_Aggregate.
   * @return toutes les geometries sous la forme d'un GM_Aggregate.
   */
  public abstract IAggregate<? extends IGeometry> getGeomAggregate();

  /**
   * Renvoie la position du centre de l'envelope de la collection.
   * @return la position du centre de l'envelope de la collection
   */
  public abstract IDirectPosition getCenter();

  /**
   * Affecte la position du centre de l'envelope de la collection.
   * @param center la position du centre de l'envelope de la collection
   */
  public abstract void setCenter(IDirectPosition center);

  /**
   * Index spatial.
   * @return l'index spatial
   */
  public abstract SpatialIndex<Feat> getSpatialIndex();

  /**
   * La collection possede-t-elle un index spatial ?
   * @return vrai si la collection possède un index spatial, faux sinon
   */
  public abstract boolean hasSpatialIndex();

  /**
   * Initialise un index spatial avec détermination automatique des paramètres.
   * Le boolean indique si on souhaite une mise a jour automatique de l'index.
   * @param spatialIndexClass Nom de la classe d'index.
   * @param automaticUpdate Spécifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc.
   */
  public abstract void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate);

  /**
   * Initialise un index spatial avec un parametre entier (utilise pour le
   * dallage). Le boolean indique si on souhaite une mise a jour automatique de
   * l'index.
   * 
   * @param spatialIndexClass Nom de la classe d'index.
   * @param automaticUpdate Spécifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc.
   * @param i Nombre de dalles en X et en Y, du dallage.
   */
  public abstract void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate, int i);

  /**
   * Initialise un index spatial d'une collection de FT_Feature, en prenant pour
   * paramètre les limites de la zone et un entier (pour le dallage, cet entier
   * est le nombre en X et Y de cases souhaitées sur la zone).
   * 
   * @param spatialIndexClass Nom de la classe d'index.
   * 
   * @param automaticUpdate Spécifie si l'index doit être mis à jour
   *          automatiquement quand on modifie les objets de fc.
   * 
   * @param enveloppe Enveloppe décrivant les limites de l'index spatial. NB:
   *          Tout objet hors de ces limites ne sera pas traité lors des
   *          requètes spatiales !!!!!
   * 
   * @param i Nombre de dalles en X et en Y, du dallage.
   */
  public abstract void initSpatialIndex(Class<?> spatialIndexClass,
      boolean automaticUpdate, IEnvelope enveloppe, int i);

  /**
   * Initialise un index spatial d'une collection de FT_Feature, en prenant pour
   * paramètre ceux d'un index existant.
   * 
   * @param spIdx un index spatial existant
   */
  public abstract void initSpatialIndex(SpatialIndex<?> spIdx);

  /**
   * Détruit l'index spatial.
   */
  public abstract void removeSpatialIndex();

  // ---------------------------------------
  // --- SELECTION AVEC L'Index spatial ----
  // ---------------------------------------
  /**
   * Selection dans le carre dont P est le centre, de cote D.
   * 
   * @param P le centre
   * @param D cote
   * @return objets qui intersectent le carre dont P est le centre, de cote D.
   */
  public abstract Collection<Feat> select(IDirectPosition P, double D);

  /**
   * Selection dans un rectangle.
   * 
   * @param env rectangle
   * @return objets qui intersectent un rectangle
   */
  public abstract Collection<Feat> select(IEnvelope env);

  /**
   * Selection des objets qui intersectent un objet geometrique quelconque.
   * 
   * @param geometry geometrie quelconque
   * @return objets qui intersectent un objet geometrique quelconque.
   */
  public abstract Collection<Feat> select(IGeometry geometry);

  /**
   * Selection des objets qui croisent ou intersectent un objet geometrique
   * quelconque.
   * 
   * @param geometry un objet geometrique quelconque
   * @param strictlyCrosses Si c'est TRUE : ne retient que les objets qui
   *          croisent (CROSS au sens JTS). Si c'est FALSE : ne retient que les
   *          objets qui intersectent (INTERSECT au sens JTS) Exemple : si 1
   *          ligne touche "geometry" juste sur une extrémité, alors avec TRUE
   *          cela ne renvoie pas la ligne, avec FALSE cela la renvoie
   * @return objets qui intersectent strictement un objet geometrique quelconque
   */
  public abstract Collection<Feat> select(IGeometry geometry,
      boolean strictlyCrosses);

  /**
   * Selection a l'aide d'un objet geometrique quelconque et d'une distance.
   * 
   * @param geometry geometrie quelconque.
   * @param distance distance maximum
   * @return objets à moins d'une certaine distance d'un objet geometrique
   *         quelconque.
   */
  public abstract Collection<Feat> select(IGeometry geometry, double distance);

  // ---------------------------------------
  // --- Méthodes nécessaire pour implémenter l'interface Collection
  // ---------------------------------------
  /**
   * Encapsulation de la methode contains() avec typage
   * 
   * @param value valeur
   * @return vrai si la collection contient la valeur, faux sinon
   */
  public abstract boolean contains(Feat value);

  /**
   * Ajoute un element a la liste des composants de this s'il n'est pas déjà
   * présent, et met à jour le lien inverse.
   * 
   * @param feature élément à ajouter
   */
  public abstract void addUnique(Feat feature);

  /**
   * Efface de la liste l'element en position i. Attention, si l'élément est
   * persistant, celui-ci n'est pas détruit, le faire après au besoin.
   * 
   * @param i indice de l'élément à supprimer
   */
  public abstract void remove(int i);

  /**
   * Efface de la liste la collection passée en parametre. Attention, si
   * l'élément est persistant, celui-ci n'est pas détruit, le faire après au
   * besoin.
   * 
   * @param value collection d'éléments à effacer
   */
  public abstract void removeCollection(IFeatureCollection<Feat> value);

  /**
   * Ajoute les éléments d'une FT_FeatureCollection a la liste des composants de
   * this, et met à jour le lien inverse.
   * 
   * @param value collection d'éléments à ajouter
   */
  public abstract void addUniqueCollection(
      IFeatureCollection<? extends Feat> value);

  @Override
  public abstract Iterator<Feat> iterator();

  @Override
  public abstract boolean contains(Object obj);

  @Override
  public abstract boolean containsAll(Collection<?> coll);

  @Override
  public abstract boolean isEmpty();

  @Override
  public abstract boolean remove(Object obj);

  @Override
  public abstract boolean retainAll(Collection<?> coll);

  @Override
  public abstract Object[] toArray();

  @Override
  public abstract boolean addAll(Collection<? extends Feat> c);

  @Override
  public abstract <T> T[] toArray(T[] a);

  public abstract Class<Feat> getClasse();

  public abstract void setClasse(Class<Feat> C);

  /**
   * Définit le nom complet (package+classe java) de la classe par défaut des
   * instances de la population. CONSEIL : ne pas utiliser cette méthode
   * directement, remplir en utilisant setClasse(). Ne met pas à jour l'attribut
   * classe. Utile uniquement pour les population peristantes.
   */
  public abstract void setNomClasse(String S);

  /**
   * Récupère le nom complet (package+classe java) de la classe par défaut des
   * instances de la population.
   * <p>
   * Pertinent uniquement pour les population peristantes.
   * <p>
   * surcharge Population.getNomClasse() en passant par le featureType
   */
  @Transient
  public abstract String getNomClasse();

  /**
   * @return Returns the featureType.
   */
  @ManyToOne
  public abstract FeatureType getFeatureType();

  /**
   * @param featureType The featureType to set.
   */
  public abstract void setFeatureType(FeatureType featureType);
}
