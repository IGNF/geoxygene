package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;

public interface IPolygon extends ISurfacePatch {
  /** Renvoie l'anneau extérieur */
  public abstract IRing getExterior();

  /** Affecte une valeur à l'anneau extérieur */
  public abstract void setExterior(IRing value);

  /**
   * Renvoie 1 si l'anneau extérieur est affecté, 0 sinon. Il paraît qu'il
   * existe des cas où on peut avoir une surface avec que des frontières
   * intérieures.
   */
  public abstract int sizeExterior();

  /** Renvoie la liste des anneaux intérieurs */
  public abstract List<IRing> getInterior();

  /** Renvoie l'anneau intérieur de rang i */
  public abstract IRing getInterior(int i);

  /** Affecte un GM_Ring au rang i */
  public abstract void setInterior(int i, IRing value);

  /** Ajoute un GM_Ring en fin de liste */
  public abstract void addInterior(IRing value);

  /** Ajoute un GM_ring au rang i */
  public abstract void addInterior(int i, IRing value);

  /** Efface le (ou les) GM_Ring passé en paramètre */
  public abstract void removeInterior(IRing value);

  /** Efface le GM_Ring de rang i */
  public abstract void removeInterior(int i);

  /** Nombre d'anneaux intérieurs */
  public abstract int sizeInterior();

  /**
   * Renvoie un GM_Polygon en "retournant" la frontière (inverse l'ordre du
   * trace des points).
   */
  @Override
  public abstract ISurfacePatch reverse();

  @Override
  public abstract Object clone();

  @Override
  public abstract boolean isPolygon();
}
