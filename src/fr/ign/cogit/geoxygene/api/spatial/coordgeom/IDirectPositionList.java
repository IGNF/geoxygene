package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface IDirectPositionList extends Collection<IDirectPosition> {
  /**
   * Affecte une liste à this. Attention, ne vérifie pas que la liste passée en
   * paramètre ne contient que des DirectPosition. Ne clone pas la liste passée
   * en paramètre mais fait une référence.
   */
  public abstract void setList(List<IDirectPosition> theList);

  /** Renvoie la liste de DirectPosition. */
  public abstract List<IDirectPosition> getList();

  /** Renvoie l'élément de rang i */
  public abstract IDirectPosition get(int i);

  /** Affecte un élément au i-ème rang de la liste */
  public abstract void set(int i, IDirectPosition value);

  /** Ajoute un élément en fin de liste */
  public abstract boolean add(IDirectPosition value);

  /** Ajoute un élément au i-ème rang de la liste */
  public abstract void add(int i, IDirectPosition value);

  /** Ajoute une liste deDirectPosititon en fin de liste */
  public abstract boolean addAll(IDirectPositionList theList);

  /** Efface de la liste le (ou les) éléments passé en paramètre */
  public abstract void remove(IDirectPosition value);

  /** Efface le i-ème élément de la liste */
  public abstract void remove(int i);

  /** Retire une liste de DirectPosititon */
  public abstract void removeAll(IDirectPositionList theList);

  /** Efface toute la liste */
  public abstract void clear();

  /** Renvoie le nombre de éléments */
  public abstract int size();

  /** Clone this */
  public abstract Object clone();

  /** Renvoie un tableau de double de la forme [X Y X Y ... X Y] */
  public abstract double[] toArray2D();

  /** Renvoie un tableau de double de la forme [X Y Z X Y Z ... X Y Z] */
  public abstract double[] toArray3D();

  /**
   * Renvoie un tableau de double contenant tous les X des DirectPosition de la
   * liste.
   */
  public abstract double[] toArrayX();

  /**
   * Renvoie un tableau de double contenant tous les Y des DirectPosition de la
   * liste.
   */
  public abstract double[] toArrayY();

  /**
   * Renvoie un tableau de double contenant tous les Z des DirectPosition de la
   * liste.
   */
  public abstract double[] toArrayZ();

  /** Affiche les coordonnées des point (2D et 3D). */
  public abstract String toString();

  public abstract Iterator<IDirectPosition> iterator();

  public abstract boolean addAll(Collection<? extends IDirectPosition> c);

  public abstract boolean contains(Object o);

  public abstract boolean containsAll(Collection<?> c);

  public abstract boolean isEmpty();

  public abstract boolean remove(Object o);

  public abstract boolean removeAll(Collection<?> c);

  public abstract boolean retainAll(Collection<?> c);

  public abstract Object[] toArray();

  public abstract <T> T[] toArray(T[] a);

  /**
   * permuter les elements i et j
   * @param i
   * @param j
   */
  public abstract void permuter(int i, int j);

  /**
   * inverse l'ordre des directposition de la liste
   */
  public abstract void inverseOrdre();
}
