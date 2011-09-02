package fr.ign.cogit.geoxygene.api.spatial.geomaggr;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IAggregate<GeomType extends IGeometry> extends IGeometry,
    Collection<GeomType> {
  /** Renvoie l'attribut element = liste de GM_Object. */
  public abstract List<GeomType> getList();

  /** Affecte une liste de GM_Object. */
  public abstract void setList(List<GeomType> L);

  /** Renvoie l'élément de rang i */
  public abstract GeomType get(int i);

  /** Affecte un élément au i-ème rang de la liste */
  public abstract GeomType set(int i, GeomType value);

  /** Ajoute un élément en fin de liste */
  @Override
  public abstract boolean add(GeomType value);

  /** Ajoute une liste d'éléments en fin de liste */
  public abstract boolean addAll(List<GeomType> theList);

  /** Ajoute un élément au i-ème rang de la liste */
  public abstract void add(int i, GeomType value);

  /** Efface de la liste le (ou les) éléments passé en paramètre */
  public abstract boolean remove(GeomType value);

  /** Efface le i-ème élément de la liste */
  public abstract void remove(int i);

  /** Efface toute la liste */
  @Override
  public abstract void clear();

  /** Renvoie le nombre de éléments */
  @Override
  public abstract int size();

  /** Convertit l'agrégat en un tableau de GM_Obect. */
  @Override
  public abstract IGeometry[] toArray();

  /** Renvoie une DirectPositionList de tous les points de l'objet. */
  @Override
  public abstract IDirectPositionList coord();

  @Override
  public abstract boolean addAll(Collection<? extends GeomType> c);

  @Override
  public abstract boolean contains(Object o);

  @Override
  public abstract boolean containsAll(Collection<?> c);

  @Override
  public abstract Iterator<GeomType> iterator();

  @Override
  public abstract boolean remove(Object o);

  @Override
  public abstract boolean removeAll(Collection<?> c);

  @Override
  public abstract boolean retainAll(Collection<?> c);

  @Override
  public abstract <T> T[] toArray(T[] a);

  @Override
  public abstract Object clone();
}
