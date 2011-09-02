package fr.ign.cogit.geoxygene.api.spatial.geomcomp;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IComplex extends IGeometry {
  /** Renvoie le set des primitives */
  public abstract Set<IGeometry> getElement();

  /** Ajoute une primitive (ajoute aussi un complexe à la primitive) */
  public abstract void addElement(IPrimitive value);

  /**
   * Efface la primitive passée en paramètre (efface aussi le complexe de la
   * primitive)
   */
  public abstract void removeElement(IPrimitive value);

  /** Nombre de primitives constituant self. */
  public abstract int sizeElement();

  /** Renvoie la liste des sous-complexes */
  public abstract Set<IComplex> getSubComplex();

  /** Ajoute un sous-complexe en fin de liste. */
  public abstract void addSubComplex(IComplex value);

  /** Efface le (ou les) sous-complexes passé en paramètre. */
  public abstract void removeSubComplex(IComplex value);

  /** Nombre de sous-complexes constituant self. */
  public abstract int sizeSubComplex();

  /** Renvoie la liste des super-complexes */
  public abstract Set<IComplex> getSuperComplex();

  /** Ajoute un super-complexe en fin de liste. */
  public abstract void addSuperComplex(IComplex value);

  /** Efface le (ou les) super-complexes passé en paramètre. */
  public abstract void removeSuperComplex(IComplex value);

  /** Nombre de super-complexes constituant self. */
  public abstract int sizeSuperComplex();

  /** Un complexe est maximal s'il n'est le subcomplexe de personne. */
  public abstract boolean isMaximal();

  /** Marche pas. Renvoie null. */
  @Override
  public abstract IDirectPositionList coord();
}
